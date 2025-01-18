package qmf.poc.agent.transport.ws

import qmf.poc.agent.transport.{Receiver, Sender, TransportDecoder, TransportEncoder}

import java.net.URI
import java.net.http.{HttpClient, WebSocket}
import java.util
import java.util.concurrent.{CompletionStage, Executors, LinkedBlockingQueue}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.jdk.FutureConverters.*

// TODO: shutdown?
private val virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor()

private def makeListener[Tout <: WebSocketMessage](
                                            completionPromise: Promise[Unit],
                                            receiverBuffer: util.Collection[Tout])
                                          (using decoder: TransportDecoder[Tout]) = new WebSocket.Listener():
  override def onOpen(webSocket: WebSocket): Unit = {
    println("onOpen using subprotocol \"" + webSocket.getSubprotocol + "\"")
    super.onOpen(webSocket)
  }

  override def onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletionStage[?] = {
    println(s"Received: $data")
    receiverBuffer.add(decoder.decode(data.toString))
    super.onText(webSocket, data, last)
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

def connectWebSocket[Tin <: JSONRPCMessage, Tout <: WebSocketMessage](url: String = "wss://echo.websocket.org")
                                                                     (using
                                                                      ec: ExecutionContext,
                                                                      encoder: TransportEncoder[Tin],
                                                                      decoder: TransportDecoder[Tout]): Future[Sender[Tin] & Receiver[Tout] & AutoCloseable] =
  val completionPromise = Promise[Unit]()
  val messageQueue = new LinkedBlockingQueue[Tout]()
  val listener = makeListener(completionPromise, messageQueue)

  val client = HttpClient.newBuilder()
    .executor(virtualThreadExecutor)
    .build()

  client.newWebSocketBuilder()
    .buildAsync(new URI(url), listener).asScala.map(ws =>
      new Sender[Tin] with Receiver[Tout] with AutoCloseable:
        self =>
        def send(message: Tin): Future[Unit] =
          println(s"==> $message")
          ws.sendText(encoder.encode(message), true).asScala.map(_ => ())

        def receive: Tout =
          messageQueue.take()

        def close(): Unit =
          Await.result(ws.sendClose(WebSocket.NORMAL_CLOSURE, "Done").asScala, 1.second)
          client.shutdown()
          println("ws closed")
    )
