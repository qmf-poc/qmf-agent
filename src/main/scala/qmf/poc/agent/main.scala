package qmf.poc.agent

import qmf.poc.agent.vth.VirtualThreadExecutionContext

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}
import qmf.poc.agent.transport.ws.{Alive, Broker, BrokerLive, Ping, websocketClient}

given ec: ExecutionContext = VirtualThreadExecutionContext()

@main def main(): Unit =
  val broker = BrokerLive()
  val ws = Await.result(websocketClient("ws://localhost:8080/agent", broker), 10.second)

  broker.put(Alive("poc agent"))

  Thread.sleep(100000)
  ws.close()
  println("exit")
