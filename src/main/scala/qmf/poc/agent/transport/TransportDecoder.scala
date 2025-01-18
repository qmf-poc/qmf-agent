package qmf.poc.agent.transport

trait TransportDecoder[+T]:
  def decode(data: String): T
