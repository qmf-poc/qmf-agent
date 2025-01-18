package qmf.poc.agent.broker

import qmf.poc.agent.broker.events.Response
import qmf.poc.agent.transport.Message

trait BrokerDecoder[M <: Message]:
  def decode(message: M): Response
