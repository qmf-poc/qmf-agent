package qmf.poc.agent.transport.ws

import qmf.poc.agent.transport.{Receiver, Sender, TransportDecoder, TransportEncoder}
import spray.json.JsonParser.ParsingException
import spray.json.{JsObject, JsString, JsValue, given}

import java.net.URI
import java.net.http.{HttpClient, WebSocket}
import java.util
import java.util.concurrent.{CompletionStage, Executors, LinkedBlockingQueue}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.jdk.FutureConverters.*

// TODO: shutdown?
private val virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor()

private def makeListener(
                          completionPromise: Promise[Unit],
                          broker: Broker) = new WebSocket.Listener():
  override def onOpen(webSocket: WebSocket): Unit = {
    println("onOpen using sub-protocol \"" + webSocket.getSubprotocol + "\"")
    super.onOpen(webSocket)
  }

  private def error(frame: CharSequence, exception: Exception): Unit =
    println(s"""Unknown frame: "$frame"""")
    println(exception)

  override def onText(webSocket: WebSocket, frame: CharSequence, last: Boolean): CompletionStage[?] = {
    val websocketMessage = frame.toString
    println(s"<== $websocketMessage")
    try
      val seq = websocketMessage.parseJson.asJsObject.getFields("method", "params")
      seq match
        case Seq(JsString("pong"), JsString(payload)) => broker.handle(Pong(payload))
        case Seq(JsString("requestSnapshot"), JsObject(params)) => params.toSeq match
          case Seq(("password", JsString(password)), ("user", JsString(user))) =>
            broker.handle(RequestSnapshot(user, password))
          case _ => error(frame, Exception("Unknown method and/or params type"))
        case _ => error(frame, Exception("Unknown method and/or params type"))
    catch
      case e: ParsingException => error(frame, e)
    super.onText(webSocket, frame, last)
  }

  override def onClose(webSocket: WebSocket, statusCode: Int, reason: String): CompletionStage[?] = {
    println(s"WebSocket connection closed: $reason")
    completionPromise.trySuccess(())
    super.onClose(webSocket, statusCode, reason)
  }

  override def onError(webSocket: WebSocket, error: Throwable): Unit = {
    println(s"WebSocket connection failed: ${error.getMessage}")
    completionPromise.tryFailure(error)
  }

def websocketClient(url: String = "wss://echo.websocket.org", broker: Broker) (using ec: ExecutionContext) =
  val completionPromise = Promise[Unit]()
  val listener = makeListener(completionPromise, broker)

  val client = HttpClient.newBuilder()
    .executor(virtualThreadExecutor)
    .build()

  client.newWebSocketBuilder()
    .buildAsync(new URI(url), listener).asScala.map(ws =>
      new AutoCloseable:
        private val outgoingListener: util.concurrent.Future[?] = virtualThreadExecutor.submit(new Runnable:
          def run(): Unit = {
            try {
              while (!Thread.currentThread().isInterrupted) {
                val message = broker.take
                println(s"==> $message")
                ws.sendText(message.jsonrpc, true)
              }
            } catch {
              case _: InterruptedException =>
                println("Websocket disconnected from broker")
            }
          }
        )
        
        def close(): Unit =
          Await.result(ws.sendClose(WebSocket.NORMAL_CLOSURE, "Done").asScala, 1.second)
          client.shutdown()
          outgoingListener.cancel(true)
          println("ws closed")
    )
