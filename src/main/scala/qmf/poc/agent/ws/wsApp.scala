package qmf.poc.agent.ws

import qmf.poc.agent.ws.protocol.{JSONRPCRequest, JSONRPCResponse}
import zio.http.*
import zio.http.ChannelEvent.{Read, Registered}
import zio.{Queue, Ref, Scope, UIO, ZIO}

/**
 * Handles new items in the queue of requests
 *
 * The effect is to be run forever (i.e., wait for new requests indefinitely) and forked
 * @param channel the websocket channel to send the request to
 * @return a ZIO effect that takes a request from the queue and sends it to the channel
 */
private def requestsProcessor(channel: WebSocketChannel): ZIO[Queue[JSONRPCRequest], Throwable, Unit] =
  (for {
    queue <- ZIO.service[Queue[JSONRPCRequest]]
    request <- queue.take
    _ <- ZIO.logInfo(s"Sending request: ${request.jsonrpc}")
    _ <- channel.send(Read(WebSocketFrame.Text(request.jsonrpc)))
  } yield ()).forever.fork.unit

/**
 * Handles new items in the queue of responses
 *
 * @param channel the websocket channel to receive the response from
 * @return generally speaking, this function never returns unless the connection is closed or fails
 */
private def responsesProcessor(channel: WebSocketChannel): ZIO[Queue[JSONRPCResponse], Throwable, Unit] =
  for {
    queue <- ZIO.service[Queue[JSONRPCResponse]]
    response <- channel.receiveAll {
      case Hand
      case Read(WebSocketFrame.Text(text)) =>
        queue.offer(JSONRPCResponse(text))
      case Registered =>
        ZIO.logInfo("Registered").unit
      case other =>
        ZIO.logInfo(s"Unknown response $other").unit
    }
  } yield ()
/**
 * Creates an effect to connect to websocket service
 *
 * @return
 */
def webSocketApp: ZIO[Queue[JSONRPCRequest] & Queue[JSONRPCResponse] & Client & Scope, Throwable, Response] =
  ZIO.logInfo("Connecting to websocket service") *>
  Handler.webSocket { channel =>
    for {
      _ <- ZIO.logInfo("Connected to websocket service")
      _ <- requestsProcessor(channel)
      _ <- responsesProcessor(channel)
    } yield ()
  }.connect("ws://localhost:8080/echo")
