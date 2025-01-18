package qmf.poc.agent.broker.events

sealed trait Request

case class PingRequest(payload: String) extends Request
case class AliveRequest(agent: String) extends Request
