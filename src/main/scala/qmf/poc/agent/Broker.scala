package qmf.poc.agent

import scala.util.{Success, Failure}
import org.slf4j.LoggerFactory
import qmf.poc.agent.catalog.{CatalogProvider, ConnectionPool}
import qmf.poc.agent.runner.QMFObjectRunner
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
    while !Thread.currentThread().isInterrupted do {
      try
        logger.debug(s"wait for incoming message queue")
        val incoming = incomingQueue.take
        logger.debug(s"Incoming message: $incoming")
        incoming match
          case ServiceReady(service) =>
            scope.fork(() => {
              logger.info(s"""Service $"service" connected, send alive notification""")
              outgoingQueue.put(Alive("poc agent"))
              logger.debug("Exit thread")
            })
          case Ping(id, payload) =>
            scope.fork[Unit](() => {
              logger.info(s"Got Ping(id=$id, payload=$payload) from service")
              outgoingQueue.put(Pong(id, payload))
              logger.debug("Exit thread")
            })
          case RequestSnapshot(id, user, password) =>
            scope.fork[Unit](() => {
              logger.info("Got RequestSnapshot")
              val qmfUser: String =
                Option(System.getProperty("qmf.user")).getOrElse(user)

              val qmfPassword: String =
                Option(System.getProperty("qmf.password")).getOrElse(password)
              Using(ConnectionPool.memo(qmfUser, qmfPassword)) { connectionPool =>
                {
                  val catalog = CatalogProvider(connectionPool).catalog
                  logger.debug("Catalog put to queue")
                  catalog match
                    case Some(catalog) => outgoingQueue.put(Snapshot(id, catalog)) // TODO: return agent's ID
                    case e             => logger.warn("No DB connection") // TODO: notify service?
                }
              }
              logger.debug("Exit thread")
            })
          case RequestRunObject(id, user, password, owner, name, format) =>
            scope.fork[Unit](() => {
              logger.info("Got RequestRunObject")
              QMFObjectRunner.retrieveObjectHTML(user, password, owner, name, format) match
                case Success(body) => outgoingQueue.put(ResponseObjectRun(id, owner, name, body, format))
                case Failure(exception) =>
                  logger.warn(exception.getMessage, exception)
                  outgoingQueue.put(ErrorObjectRun(id, owner, name, format, -1, exception.getMessage))
                case null => logger.warn("This NEVER should happen")
              logger.debug("Exit thread")
            })
      catch
        case _: InterruptedException =>
          logger.warn("Interrupted")
          Thread.currentThread().interrupt() // Preserve interrupt status
        case e: Exception =>
          logger.warn(s"Unexpected error while processing message: ${e.getMessage}", e)
    }
    logger.info("Broker exit.")
