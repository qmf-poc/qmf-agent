package qmf.poc.agent

import qmf.poc.agent.config.AgentConfig
import qmf.poc.agent.ws.protocol.{JSONRPCRequest, JSONRPCResponse, PingRequest}
import qmf.poc.agent.ws.webSocketApp
import zio.*
import zio.http.Client

val wsRequestsQueue = Queue.bounded[JSONRPCRequest](100)
val wsResponsesQueue = Queue.bounded[JSONRPCResponse](100)

object Main extends ZIOAppDefault:
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(
      ConfigProvider.fromMap(Map(
        "serviceHostName" -> "localhost",
        "servicePort" -> "8080",
        "connectionString" -> "jdbc:db2://localhost:50000/sample:user=db2inst1;password=password;",
      ))
    )
    
  override def run: ZIO[Any, Throwable, Unit] =
    val program = for
      config <- ZIO.config[AgentConfig]
      _ <- webSocketApp
      _ <- ZIO.logInfo("Agent started")
      requests <- ZIO.service[Queue[JSONRPCRequest]]
      _ <- requests.offer(PingRequest("Hello"))
      _ <- ZIO.sleep(1.second)
      _ <- ZIO.logInfo("Exiting")
    yield ()

    ZIO.scoped(program)
      .provide(
        Client.default,
        ZLayer(wsRequestsQueue),
        ZLayer(wsResponsesQueue))
