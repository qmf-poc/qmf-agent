package qmf.poc.agent

import org.slf4j.LoggerFactory
import qmf.poc.agent.catalog.ConnectionPool
import qmf.poc.agent.transport.{IncomingMessage, OutgoingMessage, SplitQueue}

import java.util.concurrent.{ExecutorService, Executors, /* StructuredTaskScope,*/ ThreadFactory}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Promise}

@main def main(): Unit =
  try
    val logger = LoggerFactory.getLogger("main")
    logger.info("Agent starting ...")
    logger.info(s"DB2 connection string ${ConnectionPool.db2cs} (-Dagent.db2cs)")
    logger.info(s"Service websocket URI: ${WebSocketClient.serviceURL} (-Dagent.service.ws.uri)")

    val incomingQueue = new SplitQueue[IncomingMessage]
    val outgoingQueue = new SplitQueue[OutgoingMessage]

    given ThreadFactory = Executors.defaultThreadFactory() // Thread.ofVirtual().factory()

    given ExecutorService = Executors.newFixedThreadPool(16)
    given ExecutionContext = ExecutionContext.global // ExecutionContext.fromExecutor(given_ExecutorService)

    val taskBroker = given_ExecutorService.submit(new Runnable {
      def run(): Unit = {
        Broker.run(incomingQueue, outgoingQueue)
      }
    })

    val taskWebSocket = given_ExecutorService.submit(new Runnable {
      def run(): Unit = {
        while true do {
          WebSocketClient.run(incomingQueue, outgoingQueue)
          val interrupted = Thread.interrupted()
          if (interrupted) {
            logger.info("WebSocket reconnection loop exit")
            return
          }
          val timeout = 2
          logger.debug(s"WebSocket connection will retried $timeout sec")
          Thread.sleep(timeout * 1000)
          logger.debug(s"WebSocket connection is about to retried")
        }
      }
    })

    val terminationPromise = Promise[Unit]()
    val shutdownPromise = Promise[Unit]()
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable() {
      def run(): Unit = {
        logger.info("SIGTERM received.")
        taskBroker.cancel(true)
        taskWebSocket.cancel(true)
        terminationPromise.success(())
        Await.result(shutdownPromise.future, Duration.Inf)
      }
    }))

    // logger.debug("send alive 2")
    // outgoingQueue.put(Alive("poc agent2"))

    logger.info("Agent started. Press Ctrl-C to exit...")
    Await.result(terminationPromise.future, Duration.Inf)
    logger.debug("Agent shutting down...")
    // taskBroker.get()
    logger.info("Broker shutdown.")
    // taskWebSocket.get()
    logger.info("WebSocket shutdown.")
    // mainScope.shutdown()
    // mainScope.join()
    Thread.sleep(500)
    logger.info("Agent shutdown.")
    shutdownPromise.success(())
  catch
    case e: InterruptedException =>
      println("Parent interrupted, should never happen...")
