package qmf.poc.agent

import qmf.poc.agent.config.AgentConfig
import qmf.poc.agent.ws.protocol.{JSONRPCRequest, JSONRPCResponse, PingRequest}
import qmf.poc.agent.ws.webSocketApp
import zio.*
import zio.http.Client

val wsRequestsQueue = Queue.bounded[JSONRPCRequest](100)
val wsResponsesQueue = Queue.bounded[JSONRPCResponse](100)

object Main extends ZIOAppDefault:
  override def run: ZIO[Any, Throwable, Unit] =
    val program = for
      config <- ZIO.service[AgentConfig]
      _ <- webSocketApp
      _ <- ZIO.logInfo("Agent started")
      requests <- ZIO.service[Queue[JSONRPCRequest]]
      _ <- requests.offer(PingRequest("Hello"))
      _ <- ZIO.sleep(1.second)
      _ <- ZIO.logInfo("Exiting")
    yield ()

    ZIO.scoped(program)
      .provide(
        AgentConfig.layer,
        Client.default,
        ZLayer(wsRequestsQueue),
        ZLayer(wsResponsesQueue))
