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
        logger.debug(s"Incoming message: $incoming, fork...")
        scope.fork(() => {
          logger.debug(s"forked thread for $incoming")
          try {
            incoming match {
              case ServiceReady(service) =>
                logger.info(s"""Service $"service" connected, send alive notification""")
                outgoingQueue.put(Alive("poc agent"))
                logger.debug("Exit thread")
              case Ping(id, payload) =>
                logger.info(s"Got Ping(id=$id, payload=$payload) from service")
                outgoingQueue.put(Pong(id, payload))
                logger.debug("Exit thread")
              case RequestSnapshot(id, user, password) =>
                logger.info("Got RequestSnapshot")
                val qmfUser: String =
                  Option(System.getProperty("qmf.user")).getOrElse(user)

                val qmfPassword: String =
                  Option(System.getProperty("qmf.password")).getOrElse(password)
                Using(ConnectionPool.memo(qmfUser, qmfPassword)) { connectionPool =>
                  {
                    val catalog = CatalogProvider(connectionPool).catalog
                    catalog match
                      case Some(catalog) => {
                        logger.debug(s"Catalog put to queue ($outgoingQueue, size: ${outgoingQueue.size})...")
                        outgoingQueue.put(Snapshot(id, catalog));
                        logger.debug(s"Catalog put to queue done ($outgoingQueue, size: ${outgoingQueue.size})")
                      }
                      case e => logger.warn("No DB connection") // TODO: notify service?
                  }
                }
                logger.debug("Exit thread")
              case RequestRunObject(id, user, password, owner, name, format) =>
                logger.info("Got RequestRunObject")
                QMFObjectRunner.retrieveObjectHTML(user, password, owner, name, format) match
                  case Success(body) => outgoingQueue.put(ResponseObjectRun(id, owner, name, body, format))
                  case Failure(exception) =>
                    logger.warn(exception.getMessage, exception)
                    outgoingQueue.put(ErrorObjectRun(id, owner, name, format, -1, exception.getMessage))
                  case null => logger.warn("This NEVER should happen")
                logger.debug("Exit thread")
            }
          } catch {
            case _: InterruptedException =>
              logger.warn("Interrupted")
              Thread.currentThread().interrupt() // Preserve interrupt status
            case e: Exception =>
              logger.warn(s"Unexpected error while processing message: ${e.getMessage}", e)
          }
        })
      catch
        case _: InterruptedException =>
          logger.warn("Interrupted")
          Thread.currentThread().interrupt() // Preserve interrupt status
        case e: Exception =>
          logger.warn(s"Unexpected error while processing message: ${e.getMessage}", e)
    }
    logger.info("Broker exit.")
