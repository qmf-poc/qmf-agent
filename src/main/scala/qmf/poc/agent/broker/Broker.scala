package qmf.poc.agent.broker

import qmf.poc.agent.broker.events.{ErrorResponse, Request, Response}
import qmf.poc.agent.transport.{Message, Receiver, Sender}

import java.util.concurrent.CountDownLatch
import scala.concurrent.{Await, ExecutionContext, Promise}

class Broker[Tin <: Message, Tout <: Message](sender: Sender[Tin], receiver: Receiver[Tout])
                                                    (using encoder: BrokerEncoder[Tin], decoder: BrokerDecoder[Tout]):
  trait Listener:
    def stop(): Unit

  def put(request: Request): Unit =
    sender.send(encoder.encode(request))

  def listen(onResponse: Response => Unit)(using ec: ExecutionContext): Listener =
    val promise = Promise[Thread]()
    val latch = new CountDownLatch(1)
    ec.execute(() =>
      promise.success(Thread.currentThread())
      latch.countDown()
      try
        while (!Thread.interrupted())
          val m = receiver.receive
          val response = decoder.decode(m)
          // TODO: log warn
          if (!response.isInstanceOf[ErrorResponse])
            onResponse(response)
      catch
        case e: InterruptedException =>
          println("interrupted")
    )
    val t = Await.result(promise.future, scala.concurrent.duration.Duration.Inf)
    () => if (t != null) t.interrupt()

object Broker:
  def apply[Tin <: Message, Tout <: Message](sender: Sender[Tin], receiver: Receiver[Tout])
                                            (using encoder: BrokerEncoder[Tin], decoder: BrokerDecoder[Tout]): Broker[Tin, Tout] =
    new Broker[Tin, Tout](sender, receiver)