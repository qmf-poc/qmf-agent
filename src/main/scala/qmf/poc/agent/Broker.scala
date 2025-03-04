package qmf.poc.agent

import org.slf4j.LoggerFactory
import qmf.poc.agent.catalog.{CatalogProvider, ConnectionPool}
import qmf.poc.agent.transport.*

import java.util.concurrent.StructuredTaskScope
import scala.util.Using

object Broker:
  def run(
      scope: StructuredTaskScope[Unit],
      incomingQueue: ReadOnlyQueue[IncomingMessage],
      outgoingQueue: WriteOnlyQueue[OutgoingMessage]
  ): Unit =
    val logger = LoggerFactory.getLogger("broker")
    logger.debug("Enter broker loop")
    while (!Thread.currentThread().isInterrupted) do
      try
        logger.debug(s"wait for incoming message queue")
        val incoming = incomingQueue.take
        logger.debug(s"Incoming message: $incoming")
        incoming match
          case ServiceReady(service) =>
            scope.fork(() => {
              logger.info(s"""Service $"service" connected, send alive notification""")
              outgoingQueue.put(Alive("poc agent"))
            })
          case Pong(payload) => logger.info(s"Got Pong($payload) from service")
          case RequestSnapshot(user, password) =>
            scope.fork[Unit](() =>
              Using(ConnectionPool.memo(user, password)) { connectionPool =>
                CatalogProvider(connectionPool).catalog match
                  case Some(catalog) => outgoingQueue.put(Snapshot("poc agent", catalog)) // TODO: return agent's ID
                  case _             => logger.warn("No DB connection") // TODO: notify service?
              }
            )
      catch
        case _: InterruptedException =>
          logger.warn("Interrupted")
          Thread.currentThread().interrupt() // Preserve interrupt status
        case e: Exception =>
          logger.warn(s"Unexpected error while processing message: ${e.getMessage}", e)
    logger.info("Broker exit.")
