package qmf.poc.agent

import zio.Config.{int, string}
import zio.config.*
import zio.{Config, ConfigProvider, ZLayer}

case class AgentConfig(serviceHostName: String, servicePort: Int)

object AgentConfig:
  given Config[AgentConfig] = (
    string("serviceHostName") ?? "Service hostname" zip
      int("servicePort") ?? "Service port"
    ).to[AgentConfig]

  val layer: ZLayer[Any, Config.Error, AgentConfig] = ZLayer(ConfigProvider.fromMap(Map(
    "serviceHostName" -> "localhost",
    "servicePort" -> "8080"
  )).load[AgentConfig])