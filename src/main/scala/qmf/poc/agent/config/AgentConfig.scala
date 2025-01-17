package qmf.poc.agent.config

import zio.Config
import zio.Config.{int, string}
import zio.config.*

case class AgentConfig(serviceHostName: String, servicePort: Int, connectionString: String)

object AgentConfig:
  given Config[AgentConfig] = (
    string("serviceHostName") ?? "Service hostname" zip
      int("servicePort") ?? "Service port" zip
      string("connectionString") ?? "DB2 connection string"
    ).to[AgentConfig]
