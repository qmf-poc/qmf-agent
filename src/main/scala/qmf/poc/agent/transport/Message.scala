package qmf.poc.agent.transport

import qmf.poc.agent.catalog.models.Catalog
import qmf.poc.agent.catalog.models.Catalog.given
import spray.json.given
import spray.json.DefaultJsonProtocol.given

trait OutgoingMessage:
  val jsonrpc: String

case class Pong(id: Int, payload: String) extends OutgoingMessage:
  val jsonrpc: String = s"""{"jsonrpc": "2.0", "id": $id,"result": "$payload"}"""
case class Snapshot(id: Int, catalog: Catalog) extends OutgoingMessage:
  val jsonrpc: String = s"""{"jsonrpc": "2.0", "id": $id, "result": ${catalog.toJson}}"""
case class ErrorSnapshot(id: Int, code: Int, user: String, password: String, message: String) extends OutgoingMessage:
  val jsonrpc: String =
    s"""{"jsonrpc": "2.0", "id": $id, "error": {"code": $code, "message": ${message.toJson}, "data": {"user": "$user", "password": "$password"}}}"""
case class Alive(agent: String) extends OutgoingMessage:
  val jsonrpc: String = s"""{"jsonrpc": "2.0", "method": "alive", "params" : "$agent"}"""
case class ResponseObjectRun(id: Int, owner: String, name: String, body: String, format: String) extends OutgoingMessage:
  val jsonrpc: String =
    s"""{"jsonrpc": "2.0", "id": $id, "result": {"owner": "$owner", "name": "$name", "body": ${body.toJson}, "format": "$format"}}"""
case class ErrorObjectRun(id: Int, owner: String, name: String, format: String, code: Int, message: String) extends OutgoingMessage:
  val jsonrpc: String =
    s"""{"jsonrpc": "2.0", "id": $id, "error": {"code": $code, "message": ${message.toJson}, "data": {"owner": "$owner", "name": "$name", "format": "$format"}}}"""

case class Close(agent: String, code: Int, reason: String) extends OutgoingMessage:
  val jsonrpc: String = s"""{"jsonrpc": "2.0", "method": "close", "params": {"code": $code, "reason": "$reason"}}"""

trait IncomingMessage

case class ServiceReady(service: String) extends IncomingMessage
case class Ping(id: Int, payload: String) extends IncomingMessage
case class RequestSnapshot(id: Int, user: String, password: String) extends IncomingMessage
case class RequestRunObject(id: Int, user: String, password: String, owner: String, name: String, format: String)
    extends IncomingMessage
