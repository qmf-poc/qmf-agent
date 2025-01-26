package qmf.poc.agent.transport

import qmf.poc.agent.transport.ws.{IncomingMessage, OutgoingMessage}

import scala.concurrent.Future

trait Sender:
  def send(message: OutgoingMessage): Future[Unit]