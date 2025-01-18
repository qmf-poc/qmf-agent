package qmf.poc.agent.transport

import scala.concurrent.Future

trait Sender[-M]:
  def send(message: M): Future[Unit]