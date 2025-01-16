package qmf.poc.agent.ws.protocol

trait JSONRPCRequest:
  val jsonrpc: String

case class PingRequest(payload: String) extends JSONRPCRequest:
  val jsonrpc: String = s"""{"jsonrpc": "2.0", "method": "ping", "params": {payload:"$payload"}, "id": 1}"""