package qmf.poc.agent.catalog

import org.slf4j.LoggerFactory

import java.nio.charset.Charset
import java.sql.Connection

class ConnectionPool(val db2cs: String, val db2user: String, val db2password: String, val charset: Charset) extends AutoCloseable:
  private var connectionOpt: Option[Connection] = None

  override def close(): Unit =
    ()
    /*
    keep open
    connectionOpt match
      case Some(c) => c.close()
      case _       => ()
     */

  private def updateConnection(): Connection = {
    connectionOpt = None
    val connection = java.sql.DriverManager.getConnection(db2cs, db2user, db2password)
    connectionOpt = Some(connection)
    connection
  }

  def connection: Connection = {
    connectionOpt match
      case Some(c) =>
        checkConnection(c)
        c
      case None => updateConnection()
  }

  private def isConnectionValid(c: Connection): Boolean =
    c.isValid(1000)

  private def checkConnection(c: Connection): Unit =
    val stmt = c.createStatement
    try
      val rs = stmt.executeQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1")
      rs.next
      rs.getInt(1)
    finally stmt.close()

  def isValid: Boolean =
    connectionOpt match
      case Some(c) => isConnectionValid(c)
      case None    => false

  def test(): Boolean =
    try
      connection
      true
    catch
      case e =>
        println(e)
        false

object ConnectionPool:
  private var previousOption: Option[ConnectionPool] = None
  private val logger = LoggerFactory.getLogger("cp")

  val db2cs: String =
    Option(System.getProperty("agent.db2cs")).getOrElse("jdbc:db2://qmfdb2.s4y.solutions:50000/sample")

  def memo(db2user: String, db2password: String, charsetName: String): ConnectionPool =
    previousOption match
      case Some(previous) =>
        logger.debug(
          s"cs(${previous.db2cs == db2cs}) user(${previous.db2user == db2user} pw(${previous.db2password == db2password}) valid(${previous.isValid})"
        )
        if (previous.db2cs == db2cs && previous.db2user == db2user && previous.db2password == db2password && previous.isValid)
          logger.debug(s"reuse previous connection $db2cs for user $db2user")
          previous
        else
          logger.debug(s"close previous connection ${previous.db2cs} for user ${previous.db2user}")
          previous.close()
          logger.debug(s"create new connection $db2cs for user $db2user, charset: $charsetName")
          val charset = Charset.forName(charsetName)
          val connectionPool = new ConnectionPool(db2cs, db2user, db2password, charset)
          previousOption = Some(connectionPool)
          connectionPool
      case None =>
        logger.debug(s"create new connection $db2cs for user $db2user, charset: $charsetName")
        val charset = Charset.forName(charsetName)
        val connectionPool = new ConnectionPool(db2cs, db2user, db2password, charset)
        previousOption = Some(connectionPool)
        connectionPool
  def memo(db2cs: String, db2user: String, db2password: String, charset: Charset): ConnectionPool =
    previousOption match
      case Some(previous) =>
        if (previous.db2cs == db2cs && previous.db2user == db2user && previous.db2password == db2password && previous.isValid)
          previous
        else
          previous.close()
          val connectionPool = new ConnectionPool(db2cs, db2user, db2password, charset)
          previousOption = Some(connectionPool)
          connectionPool
      case None => new ConnectionPool(db2cs, db2user, db2password, charset)

/*
def memo(db2user: String, db2password: String, previousOption: Option[ConnectionPool]): ConnectionPool =
  previousOption match
    case Some(previous) =>
      if (previous.db2cs == db2cs && previous.db2user == db2user && previous.db2password == db2password && previous.isValid)
        previous
      else
        previous.close()
        new ConnectionPool(db2cs, db2user, db2password)
    case None => new ConnectionPool(db2cs, db2user, db2password)
 */
