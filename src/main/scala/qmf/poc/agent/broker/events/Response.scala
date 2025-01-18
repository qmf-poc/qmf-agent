package qmf.poc.agent.broker.events

sealed trait Response

case class ErrorResponse(message: String, cause: Exception) extends Response
case class PongResponse(payload: String) extends Response
case class SyncResponse(cs: String, user: String, password: String) extends Response
