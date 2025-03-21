package qmf.poc.agent

import org.slf4j.LoggerFactory
import qmf.poc.agent.catalog.ConnectionPool
import qmf.poc.agent.transport.{IncomingMessage, OutgoingMessage, SplitQueue}

import java.util.concurrent.{ExecutorService, Executors, StructuredTaskScope, ThreadFactory}
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

    given ThreadFactory = Thread.ofVirtual().factory()
    given ExecutorService = Executors.newThreadPerTaskExecutor(given_ThreadFactory) // Thread.ofVirtual().factory())
    given ExecutionContext = ExecutionContext.fromExecutor(given_ExecutorService)
    val mainScope = new StructuredTaskScope[ScopeResult]("mainScope", given_ThreadFactory)

    val taskBroker = mainScope.fork(() => {
      logger.debug("Broker thread started")
      Broker.run(mainScope, incomingQueue, outgoingQueue)
      logger.debug("Broker thread exit")
      ScopeResult.Ok
    })

    val taskWebSocket = mainScope.fork(() => {
      logger.debug("WebSocketClient thread started")
      while !Thread.currentThread().isInterrupted do
        WebSocketClient.run(mainScope, incomingQueue, outgoingQueue)
        if Thread.currentThread().isInterrupted then
          logger.info("WebSocketClient thread interrupted")
        else
          val timeout = 2
          logger.debug(s"WebSocket connection will retried $timeout sec")
          Thread.sleep(timeout * 1000)
          logger.debug(s"WebSocket connection is about to retried")
      logger.debug("WebSocketClient thread exit")
      ScopeResult.Ok
    })

    val terminationPromise = Promise[Unit]()
    val shutdownPromise = Promise[Unit]()
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable() {
      def run(): Unit = {
        logger.info("SIGTERM received.")
        terminationPromise.success(())
        Await.result(shutdownPromise.future, Duration.Inf)
      }
    }))

    // logger.debug("send alive 2")
    // outgoingQueue.put(Alive("poc agent2"))

    logger.info("Agent started. Press Ctrl-C to exit...")
    Await.result(terminationPromise.future, Duration.Inf)
    logger.debug("Agent shutting down...")
    mainScope.shutdown()
    mainScope.join()
    Thread.sleep(1500)
    logger.info("Agent shutdown.")
    shutdownPromise.success(())
  catch
    case e: InterruptedException =>
      println("Parent interrupted, should never happen...")
