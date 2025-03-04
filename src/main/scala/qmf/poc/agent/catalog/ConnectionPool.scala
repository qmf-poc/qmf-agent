package qmf.poc.agent.catalog

import org.slf4j.LoggerFactory

import java.sql.Connection

class ConnectionPool(val db2cs: String, val db2user: String, val db2password: String) extends AutoCloseable:
  private var connectionOpt: Option[Connection] = None

  override def close(): Unit =
    ()
    /*
    keep open
    connectionOpt match
      case Some(c) => c.close()
      case _       => ()
     */

  private def updateConnection(): Option[Connection] =
    try connectionOpt = Option(java.sql.DriverManager.getConnection(db2cs, db2user, db2password))
    catch
      case e =>
        println(e)
        connectionOpt = None
    connectionOpt

  def connection: Option[Connection] =
    connectionOpt.filter(c => isConnectionValid(c)).orElse(updateConnection())

  private def isConnectionValid(c: Connection): Boolean =
    c.isValid(1000)
    /*
    try
      val stmt = c.createStatement
      val rs = stmt.executeQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1")
      rs.next && rs.getInt(1) == 1
    catch
      case e: Exception =>
        println(e)
        false
     */

  def isValid: Boolean =
    connectionOpt match
      case Some(c) => isConnectionValid(c)
      case None    => false
/*
  def test(): Boolean =
    try
      connection
      true
    catch
      case e =>
        println(e)
        false
 */
object ConnectionPool:
  private var previousOption: Option[ConnectionPool] = None
  private val logger = LoggerFactory.getLogger("cp")

  val db2cs: String =
    Option(System.getProperty("agent.db2cs")).getOrElse("jdbc:db2://qmfpoc.s4y.solutions:50000/sample")

  def memo(db2user: String, db2password: String): ConnectionPool =
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
          logger.debug(s"create new connection $db2cs for user $db2user")
          val connectionPool = new ConnectionPool(db2cs, db2user, db2password)
          previousOption = Some(connectionPool)
          connectionPool
      case None =>
        logger.debug(s"create new connection $db2cs for user $db2user")
        val connectionPool = new ConnectionPool(db2cs, db2user, db2password)
        previousOption = Some(connectionPool)
        connectionPool

  def memo(db2cs: String, db2user: String, db2password: String): ConnectionPool =
    previousOption match
      case Some(previous) =>
        if (previous.db2cs == db2cs && previous.db2user == db2user && previous.db2password == db2password && previous.isValid)
          previous
        else
          previous.close()
          val connectionPool = new ConnectionPool(db2cs, db2user, db2password)
          previousOption = Some(connectionPool)
          connectionPool
      case None => new ConnectionPool(db2cs, db2user, db2password)

  def memo(db2user: String, db2password: String, previousOption: Option[ConnectionPool]): ConnectionPool =
    previousOption match
      case Some(previous) =>
        if (previous.db2cs == db2cs && previous.db2user == db2user && previous.db2password == db2password && previous.isValid)
          previous
        else
          previous.close()
          new ConnectionPool(db2cs, db2user, db2password)
      case None => new ConnectionPool(db2cs, db2user, db2password)
