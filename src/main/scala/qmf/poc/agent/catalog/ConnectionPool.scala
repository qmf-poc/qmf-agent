package qmf.poc.agent.catalog

import java.sql.Connection

class ConnectionPool(val db2cs: String, val db2user: String, val db2password: String) extends AutoCloseable:
  private var connectionOpt: Option[Connection] = None

  override def close(): Unit =
    connectionOpt match
      case Some(c) => c.close()
      case _ => ()

  private def updateConnection(): Unit =
    try
      connectionOpt = Option(java.sql.DriverManager.getConnection(db2cs, db2user, db2password))
    catch
      case e =>
        println(e)
        connectionOpt = None

  // TODO: error
  def connection: Option[Connection] =
    connectionOpt.filter(_.isValid(100)).orElse
      updateConnection()
      connectionOpt

  def isValid: Boolean =
    connectionOpt match
      case Some(c) => c.isValid(100)
      case None => false

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
  private val db2cs = "jdbc:db2://qmfpoc.s4y.solutions:50000/sample"
  def memo(db2user: String, db2password: String): ConnectionPool =
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
      
  def memo( db2user: String, db2password: String, previousOption: Option[ConnectionPool]): ConnectionPool =
    previousOption match
      case Some(previous) =>
        if (previous.db2cs == db2cs && previous.db2user == db2user && previous.db2password == db2password && previous.isValid)
          previous
        else
          previous.close()
          new ConnectionPool(db2cs, db2user, db2password)
      case None => new ConnectionPool(db2cs, db2user, db2password)
