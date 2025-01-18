package qmf.poc.agent.transport.ws

import qmf.poc.agent.transport.{TransportDecoder, TransportEncoder, Message}

class WebSocketMessage(val frame: String) extends Message:
  override def toString: String = frame

class JSONRPCMessage(method: String, params: String)
  extends WebSocketMessage(s"""{"jsonrpc": "2.0", "method": "$method", "params": "$params"}""")

object WebSocketMessage:
  given TransportDecoder[WebSocketMessage] with
    def decode(data: String): WebSocketMessage = WebSocketMessage(data)
  given TransportEncoder[JSONRPCMessage] with
    def encode(message: JSONRPCMessage): String = message.frame
