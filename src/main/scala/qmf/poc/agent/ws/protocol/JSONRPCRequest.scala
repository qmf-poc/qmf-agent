package qmf.poc.agent.ws.protocol

import zio.json.*

trait JSONRPCRequest:
  val jsonrpc: String
  
private def makeParams(method: String, payload: Any): String =
  val params = payload match
    case s: String => s.toJson
    case _ => throw new Exception("Unsupported payload type")
  s"""{"jsonrpc": "2.0", "method": "$method", "params": {"payload": "$params"}, "id": 1}"""

case class PingRequest(payload: String) extends JSONRPCRequest:
  val jsonrpc: String = makeParams("ping", payload)