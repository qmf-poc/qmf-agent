package qmf.poc.agent.transport

trait TransportEncoder[-T]:
  def encode(message: T): String
