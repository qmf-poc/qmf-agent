package qmf.poc.agent

import qmf.poc.agent.vth.VirtualThreadExecutionContext

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, Promise}
import qmf.poc.agent.transport.ws.{Alive, Broker, BrokerLive, Ping, RequestSnapshot, Snapshot, websocketClient}

given ec: ExecutionContext = VirtualThreadExecutionContext()

@main def main(): Unit =
  val broker = BrokerLive()
  val ws = Await.result(websocketClient("ws://localhost:8081/agent", broker), 10.second)
  // val ws = Await.result(websocketClient("ws://qmfpoc.s4y.solutions:8081/agent", broker), 10.second)

  broker.put(Alive("poc agent"))
  // broker.handle(RequestSnapshot("db2inst1", "password"))

  val terminationPromise = Promise[Unit]()
  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable() {
    def run(): Unit = {
      println("SIGTERM received. Cleaning up...")
      ws.close()
      terminationPromise.trySuccess(())
    }
  }))
  println("Agent started. Press Ctrl-C to exit...")
  Await.result(terminationPromise.future, Duration.Inf)
  println("Shutdown")
