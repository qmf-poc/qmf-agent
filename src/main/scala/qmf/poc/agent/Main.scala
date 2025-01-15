package qmf.poc.agent

import qmf.poc.agent.config.AgentConfig
import zio.*

object Main extends ZIOAppDefault:
  override def run: ZIO[Environment & ZIOAppArgs & Scope, Any, Any] =
    val program = for
      config <- ZIO.service[AgentConfig]
      _ <- Console.printLine(config.serviceHostName)
    yield ()

    program.provide(AgentConfig.layer)
