package qmf.poc.agent

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
        "db2host" -> "qmfpoc.s4y.solutions",
        "db2port" -> "50000",
        "db2database" -> "sample:user=db2inst1",
      ))
    )

  val program: ZIO[Queue[JSONRPCRequest] & Queue[JSONRPCResponse] & Client & Scope, Throwable, Unit] = for
    config <- ZIO.config[AgentConfig]
    _ <- webSocketApp
    _ <- ZIO.logInfo("Agent started")
    requests <- ZIO.service[Queue[JSONRPCRequest]]
    _ <- requests.offer(PingRequest("Hello"))
    _ <- ZIO.sleep(1.second)
    _ <- ZIO.logInfo("Exiting")
  yield ()

  override def run: ZIO[Scope, Throwable, Unit] =
    program.provide(
        Client.default,
        ZLayer.fromZIO(wsRequestsQueue),
        ZLayer.fromZIO(wsResponsesQueue),
      )
