package qmf.poc.agent.transport

import java.util.concurrent.LinkedBlockingQueue

trait ReadOnlyQueue[E]:
  def take: E

  def isEmpty: Boolean

trait WriteOnlyQueue[E]:
  def put(e: E): Unit

class SplitQueue[E]() extends ReadOnlyQueue[E] with WriteOnlyQueue[E]:
  private val queue = LinkedBlockingQueue[E](16)

  def take: E = queue.take

  def poll(timeout: Long, unit: Nothing): E = queue.poll(timeout, unit)

  def isEmpty: Boolean = queue.isEmpty

  def put(e: E): Unit = {
    queue.put(e)
  }

  def offer(e: E, timeout: Long, unit: Nothing): Boolean = queue.offer(e, timeout, unit)
