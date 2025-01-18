package qmf.poc.agent

import zio.Config
import zio.Config.{int, string}
import zio.config.*

case class AgentConfig(serviceHostName: String,
                       servicePort: Int,
                       db2host: String,
                       db2port: Int,
                       db2database: String
                      )

object AgentConfig:
  given Config[AgentConfig] = (
    string("serviceHostName") ?? "Service hostname" zip
      int("servicePort") ?? "Service port" zip
      string("db2host") ?? "DB2 host" zip
      int("db2port") ?? "DB2 host" zip
      string("db2database") ?? "DB2 database"
    ).to[AgentConfig]
