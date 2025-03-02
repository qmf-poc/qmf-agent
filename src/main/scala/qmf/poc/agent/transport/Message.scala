package qmf.poc.agent.transport

import qmf.poc.agent.catalog.models.Catalog
import qmf.poc.agent.catalog.models.CatalogJsonFormat.given
import spray.json.given

trait OutgoingMessage:
  val jsonrpc: String

case class Ping(payload: String) extends OutgoingMessage:
  val jsonrpc: String =  s"""{"jsonrpc": "2.0", "method": "ping", "params" : "$payload"}"""

case class Alive(agent: String) extends OutgoingMessage:
  val jsonrpc: String = s"""{"jsonrpc": "2.0", "method": "alive", "params" : "$agent"}"""

case class Snapshot(agent: String, catalog: Catalog) extends OutgoingMessage:
  val jsonrpc: String =  s"""{"jsonrpc": "2.0", "method": "snapshot", "params": ${catalog.toJson}}"""

case class Close(agent: String, code: Int, reason: String) extends OutgoingMessage:
  val jsonrpc: String = s"""{"jsonrpc": "2.0", "method": "close", "params": {"code": $code, "reason": "$reason"}}"""

trait IncomingMessage

case class ServiceReady(service: String) extends IncomingMessage
case class Pong(payload: String) extends IncomingMessage
case class RequestSnapshot(user: String, password: String) extends IncomingMessage
  
