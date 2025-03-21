package qmf.poc.agent.transport

import org.slf4j.LoggerFactory

import java.util.concurrent.LinkedBlockingQueue

trait ReadOnlyQueue[E]:
  def take: E
  def size: Int

  def isEmpty: Boolean

trait WriteOnlyQueue[E]:
  def put(e: E): Unit
  def size: Int

class SplitQueue[E]() extends ReadOnlyQueue[E] with WriteOnlyQueue[E]:
  private val queue = LinkedBlockingQueue[E](16)
  private val logger = LoggerFactory.getLogger("q")

  def take: E = {
    logger.debug("take, size: " + queue.size())
    val e = queue.take
    logger.debug(s"taken: $e, size: ${queue.size()}")
    e
  }

  def poll(timeout: Long, unit: Nothing): E = {
    logger.debug("poll, size: " + queue.size())
    val e = queue.poll(timeout, unit)
    logger.debug("polled, size: " + queue.size())
    e
  }

  def isEmpty: Boolean = queue.isEmpty

  def put(e: E): Unit = {
    logger.debug("put, size: " + queue.size())
    queue.put(e)
    logger.debug("put done, size: " + queue.size())
  }

  def offer(e: E, timeout: Long, unit: Nothing): Boolean = {
    logger.debug("offer, size: " + queue.size())
    val b = queue.offer(e, timeout, unit)
    logger.debug("offer done, size: " + queue.size())
    b
  }
  
  def size: Int = queue.size()
