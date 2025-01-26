package qmf.poc.agent.transport

import qmf.poc.agent.transport.ws.OutgoingMessage

trait Receiver:
  def receive: OutgoingMessage