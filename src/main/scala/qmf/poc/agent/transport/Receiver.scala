package qmf.poc.agent.transport

trait Receiver[+M]:
  def receive: M