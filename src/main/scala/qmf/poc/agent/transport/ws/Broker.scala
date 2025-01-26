package qmf.poc.agent.transport.ws

import qmf.poc.agent.catalog.{CatalogProvider, ConnectionPool}

import java.util.concurrent.LinkedBlockingQueue
import scala.util.Using

trait Broker:
  def handle(incoming: IncomingMessage): Unit

  def put(message: OutgoingMessage): Unit

  def take: OutgoingMessage

class BrokerLive extends Broker:
  private val incomingQueue = new LinkedBlockingQueue[IncomingMessage]
  private val outgoingQueue = new LinkedBlockingQueue[OutgoingMessage]

  override def handle(incoming: IncomingMessage): Unit = incoming match
    case Pong(payload) => ()
    case RequestSnapshot(user, password) =>
      // TODO: rut in background
      Using(ConnectionPool.memo(user, password)) { connectionPool =>
        CatalogProvider(connectionPool).catalog match
          case Some(catalog) => put(Snapshot("poc agent", catalog))
          case _ => println("printl no connection")
      }

  def put(message: OutgoingMessage): Unit =
    outgoingQueue.offer(message)

  override def take: OutgoingMessage =
    outgoingQueue.take

object BrokerLive:
  def apply() = new BrokerLive()
