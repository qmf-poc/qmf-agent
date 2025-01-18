package qmf.poc.agent.broker.ws

import qmf.poc.agent.broker.BrokerEncoder
import qmf.poc.agent.broker.events.{PingRequest, Request}
import qmf.poc.agent.transport.ws.JSONRPCMessage

object WebSocketEncoder:
  given BrokerEncoder[JSONRPCMessage] with
    def encode(request: Request): JSONRPCMessage = request match
      case PingRequest(payload) => JSONRPCMessage("ping", payload)
