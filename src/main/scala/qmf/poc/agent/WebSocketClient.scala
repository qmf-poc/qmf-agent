package qmf.poc.agent

import org.slf4j.{Logger, LoggerFactory}
import qmf.poc.agent
import qmf.poc.agent.catalog.{CatalogProvider, ConnectionPool}
import qmf.poc.agent.transport.*
import spray.json.JsonParser.ParsingException
import spray.json.{JsNumber, JsObject, JsString, given}

import java.net.URI
import java.net.http.{HttpClient, WebSocket}
import java.util
import java.util.concurrent.{CompletionStage, ExecutorService, StructuredTaskScope, ThreadFactory}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.jdk.FutureConverters.given
import scala.util.Using

object WebSocketClient:
  val serviceURL = Option(System.getProperty("agent.service.ws.uri")).getOrElse("ws://localhost:8081/agent")

  private def makeWebsocketListener(
      logger: Logger,
      incomingQueue: WriteOnlyQueue[IncomingMessage]
  ): (Future[Unit], WebSocket.Listener) =
    val completionPromise = Promise[Unit]
    (
      completionPromise.future,
      new WebSocket.Listener():
        override def onOpen(webSocket: WebSocket): Unit = {
          logger.debug(s"""onOpen using sub-protocol "${webSocket.getSubprotocol}"""")
          super.onOpen(webSocket)
        }

        override def onText(webSocket: WebSocket, frame: CharSequence, last: Boolean): CompletionStage[?] = {
          logger.debug(s"<== $frame")
          val websocketMessage = frame.toString
          try
            val seq = websocketMessage.parseJson.asJsObject.getFields("method", "params", "id")
            seq match
              case Seq(JsString("ping"), JsObject(params), JsNumber(id)) =>
                params.toSeq match
                  case Seq(("payload", JsString(payload))) =>
                    incomingQueue.put(Ping(id.toInt, payload))
                  case _ => handleError(frame, Exception(s"no payload in params : $params"))
              case Seq(JsString("snapshot"), JsObject(params), JsNumber(id)) =>
                params.toSeq match
                  case Seq(("password", JsString(password)), ("user", JsString(user))) =>
                    incomingQueue.put(RequestSnapshot(id.toInt, user, password))
                    /*

                    val qmfUser: String =
                      Option(System.getProperty("qmf.user")).getOrElse(user)

                    val qmfPassword: String =
                      Option(System.getProperty("qmf.password")).getOrElse(password)
                    Using(ConnectionPool.memo(qmfUser, qmfPassword)) { connectionPool =>
                      {
                        val catalog = CatalogProvider(connectionPool).catalog
                        logger.debug(s"match $catalog")
                        catalog match
                          case Some(catalog) =>
                            logger.debug(s"some(catalog) $catalog")
                            val message = Snapshot(id.toInt, catalog)
                            logger.debug(s"==> $message, serializing...")
                            val serialized = message.jsonrpc
                            logger.debug(s"Sending serialized ${serialized.substring(0, math.min(serialized.length, 250))}...")
                            webSocket.sendText(serialized, true)
                            logger.debug(s"Sent")
                          case e => logger.warn("No DB connection") // TODO: notify service?
                      }
                    }*/

                  case _ => handleError(frame, Exception(s"Unknown method and/or params type in: $websocketMessage"))
              case Seq(JsString("run"), JsObject(params), JsNumber(id)) =>
                params.toSeq match
                  case Seq(
                        ("format", JsString(format)),
                        ("name", JsString(name)),
                        ("owner", JsString(owner)),
                        ("password", JsString(password)),
                        ("user", JsString(user))
                      ) =>
                    incomingQueue.put(RequestRunObject(id.toInt, user, password, owner, name, format))
                  case l => handleError(frame, Exception(s"Unknown run params type in: $websocketMessage/$l"))
              case _ => handleError(frame, Exception(s"Unknown method in: $websocketMessage"))
          catch
            case e: ParsingException => handleError(frame, e)
            case e: Throwable        => logger.warn(s"${e.getMessage}")
            case e: Exception        => logger.warn(s"${e.getMessage}")
            case e: Any              => logger.warn(s"$e")
            case null                => logger.warn(s"null")
          finally super.onText(webSocket, frame, last)
          null
        }

        override def onClose(webSocket: WebSocket, statusCode: Int, reason: String): CompletionStage[?] = {
          logger.debug(s"WebSocket connection closed by peer ($statusCode): $reason")

          completionPromise.trySuccess(())
          super.onClose(webSocket, statusCode, reason)
        }

        override def onError(webSocket: WebSocket, error: Throwable): Unit = {
          logger.warn("error", error)
          completionPromise.tryFailure(error)
          super.onError(webSocket, error)
        }

        private def handleError(frame: CharSequence, exception: Exception): Unit =
          logger.warn(s""""$frame"""", exception)
    )

  private def getHttpClient(logger: Logger)(using es: ExecutorService): HttpClient =
    logger.debug("HTTP Client creating...")
    val client: HttpClient = HttpClient
      .newBuilder()
      .executor(es) // TODO: Current thread not owner or thread in flock
      .build()
    logger.debug("HTTP Client created")
    client

  private def getWebSocket(logger: Logger, listener: WebSocket.Listener, httpClient: HttpClient, url: String): WebSocket =
    logger.debug(s"Connecting $url...")

    val wsFuture = httpClient
      .newWebSocketBuilder()
      .buildAsync(new URI(url), listener)
      .asScala

    Await.result(wsFuture, Duration.Inf)

  private def outgoingQueueLoop(
      logger: Logger,
      webSocket: WebSocket,
      outgoingQueue: ReadOnlyQueue[OutgoingMessage]
  ): ScopeResult =
    logger.debug("outgoingQueueLoop enter")
    while !Thread.currentThread().isInterrupted do
      try
        logger.debug("wait for outgoing message queue")
        val message = outgoingQueue.take
        logger.debug(s"==> $message, serializing...")
        val serialized = message.jsonrpc
        logger.debug(s"Sending serialized ${serialized.substring(0, math.min(serialized.length, 250))}...")
        webSocket.sendText(serialized, true)
        logger.debug(s"Sent")
      catch
        case _: InterruptedException =>
          logger.warn("Outgoing messages listener interrupted")
          Thread.currentThread().interrupt() // Preserve interrupt status
        case e: Exception =>
          logger.warn(s"Unexpected error while processing message: ${e.getMessage}", e)
    logger.debug("outgoingQueueLoop exit")
    ScopeResult.Interrupted

  def run(
      scope: StructuredTaskScope[ScopeResult],
      incomingQueue: WriteOnlyQueue[IncomingMessage],
      outgoingQueue: ReadOnlyQueue[OutgoingMessage]
  )(using ec: ExecutionContext, es: ExecutorService, tf: ThreadFactory): Unit =
    val logger = LoggerFactory.getLogger("ws")

    logger.debug("run enter");

    logger.debug("getHttpClient...")
    val httpClient = getHttpClient(logger)

    val (completionFuture, listener) = makeWebsocketListener(logger, incomingQueue)
    try {
      val webSocket = getWebSocket(logger, listener, httpClient, serviceURL)
      try {
        val loopScope = scope.fork(() => {
          outgoingQueueLoop(logger, webSocket, outgoingQueue)
        })
        Await.ready(completionFuture, Duration.Inf)
      } finally {
        logger.debug("send close to peer")
        webSocket.sendClose(0, "Exit")
      }
    } catch
      case _: InterruptedException =>
        logger.debug("Interrupted")
        Thread.currentThread().interrupt()
      case e: Exception => logger.warn("Connection error", e)
    finally
      logger.info("HTTP Client shutdown")
      httpClient.shutdown()
      logger.debug("run exit");
