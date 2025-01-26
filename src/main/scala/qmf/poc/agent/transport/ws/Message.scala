package qmf.poc.agent.transport.ws

import qmf.poc.agent.catalog.models.Catalog
import qmf.poc.agent.transport.{Message, TransportDecoder, TransportEncoder}
import spray.json.{JsArray, JsObject, given}
import qmf.poc.agent.catalog.models.CatalogJsonFormat.given

/**
 * (*) -> Ping
 * --> Pong ->(*)
 * (*) -> Alive
 * --> RequestSnapshot -> Snapshot
 */

trait OutgoingMessage:
  val jsonrpc: String

case class Ping(payload: String) extends OutgoingMessage:
  val jsonrpc: String =  s"""{"jsonrpc": "2.0", "method": "ping", "params" : "$payload"}"""

case class Alive(agent: String) extends OutgoingMessage:
  val jsonrpc: String = s"""{"jsonrpc": "2.0", "method": "alive", "params" : "$agent"}"""

case class Snapshot(agent: String, catalog: Catalog) extends OutgoingMessage:
  val jsonrpc: String =  s"""{"jsonrpc": "2.0", "method": "snapshot", "params": ${catalog.toJson}"""

trait IncomingMessage

case class Pong(payload: String) extends IncomingMessage
case class RequestSnapshot(user: String, password: String) extends IncomingMessage
  
