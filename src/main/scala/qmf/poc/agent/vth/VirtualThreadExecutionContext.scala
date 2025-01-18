package qmf.poc.agent.vth

import scala.concurrent.ExecutionContext
import java.util.concurrent.{Executors, ThreadFactory}

object VirtualThreadExecutionContext {
  def apply(): ExecutionContext = {
    val factory = Thread.ofVirtual().factory()

    val executor = Executors.newThreadPerTaskExecutor(factory)

    ExecutionContext.fromExecutor(
      executor,
      e => System.err.println(s"Uncaught exception: ${e.getMessage}")
    )
  }

  // Alternative version with custom thread naming
  def withNaming(namePrefix: String): ExecutionContext = {
    val factory = Thread.ofVirtual().name(namePrefix, 0).factory()

    val executor = Executors.newThreadPerTaskExecutor(factory)

    ExecutionContext.fromExecutor(
      executor,
      e => System.err.println(s"Uncaught exception: ${e.getMessage}")
    )
  }
}
