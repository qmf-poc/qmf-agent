package qmf.poc.agent.config

import zio.config.*
import zio.Config.{int, string}
import zio.{Config, ConfigProvider, ZLayer}

/**
 * Define the accessors for the agent configuration
 *
 * @param serviceHostName the hostname of the service
 * @param servicePort the port of the service
 */
case class AgentConfig(serviceHostName: String, servicePort: Int)

object AgentConfig:
  /**
   * Define the configuration descriptor for the agent
   * as an implicit value to be used by the ZIO environment
   * for ex. ZIO.config[AgentConfig]
   * @return the configuration descriptor
   */
  private val agentConfig: Config[AgentConfig] = (
    string("serviceHostName") ?? "Service hostname" zip
      int("servicePort") ?? "Service port"
    ).to[AgentConfig]

  /**
   * Define the ZLayer for the agent configuration
   * to be used by the ZIO environment
   * for ex. ZIO.service[AgentConfig]
   * and provide the configuration to the environment as
   * for ex. `provide(AgentConfig.layer)`
   * @return the ZLayer for the agent configuration
   */
  val layer: ZLayer[Any, Config.Error, AgentConfig] = ZLayer(ConfigProvider.fromMap(Map(
    "serviceHostName" -> "localhost",
    "servicePort" -> "8080"
  )).load(agentConfig))
