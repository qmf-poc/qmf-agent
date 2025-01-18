package qmf.poc.agent.catalog

import java.sql.Connection

class ConnectionPool(db2user: String, db2password: String, db2cs: String) extends  AutoCloseable:
  private var connectionOpt: Option[Connection] = None

  override def close(): Unit =
    connectionOpt match
      case Some(c) => c.close()
      case _ => ()

  // TODO: error
  def connection: Connection =
    connectionOpt match
      case Some(c) => c
      case _ =>
        connectionOpt = Option(java.sql.DriverManager.getConnection(db2cs, db2user, db2password))
        connection

  def test(): Boolean =
    try
      connection
      true
    catch
      case _ => false

