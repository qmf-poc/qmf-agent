package qmf.poc.agent

/**
 * Define the configuration for the agent.
 *
 * - **serviceHostName**:
 *   - The hostname of the service the agent will connect to.
 *   - Defaults to: `localhost`
 *   - Configuration options:
 *     - Environment variable: `SERVICE_HOSTNAME`
 *     - Configuration key: `serviceHostName`
 *     - Command-line argument: `--service-hostname`
 *     - System property: `service.hostname`
 *
 * - **servicePort**:
 *   - The port of the service the agent will connect to.
 *   - Defaults to: `8080`
 *   - Configuration options:
 *     - Environment variable: `SERVICE_PORT`
 *     - Configuration key: `servicePort`
 *     - Command-line argument: `--service-port`
 *     - System property: `service.port`
 */

package object config 

