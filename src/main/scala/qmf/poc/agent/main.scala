package qmf.poc.agent

import qmf.poc.agent.broker.Broker
import qmf.poc.agent.broker.events.PingRequest
import qmf.poc.agent.broker.ws.WebSocketDecoder.given
import qmf.poc.agent.broker.ws.WebSocketEncoder.given
import qmf.poc.agent.transport.ws.{JSONRPCMessage, WebSocketMessage, connectWebSocket}
import qmf.poc.agent.vth.VirtualThreadExecutionContext

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

given ec: ExecutionContext = VirtualThreadExecutionContext()

@main def main(): Unit =
  val ws = Await.result(connectWebSocket("ws://localhost:8080/agent"), 10.second)

  val broker = Broker[JSONRPCMessage, WebSocketMessage](ws, ws)
  val listener = broker.listen(r => println(s"<==$r"))

  broker.put(PingRequest("agent"))

  Thread.sleep(1000)
  listener.stop()
  Thread.sleep(1000)
  ws.close()
  println("exit")
