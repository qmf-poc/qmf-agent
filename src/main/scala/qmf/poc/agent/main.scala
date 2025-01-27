package qmf.poc.agent

import qmf.poc.agent.vth.VirtualThreadExecutionContext

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, Promise}
import qmf.poc.agent.transport.ws.{Alive, Broker, BrokerLive, Ping, websocketClient}

given ec: ExecutionContext = VirtualThreadExecutionContext()

@main def main(): Unit =
  val broker = BrokerLive()
  val ws = Await.result(websocketClient("ws://localhost:8080/agent", broker), 10.second)

  broker.put(Alive("poc agent"))

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
