package qmf.poc.agent.catalog

import qmf.poc.agent.catalog.models.{Catalog, ObjectData, ObjectDirectory, ObjectRemarks}

import java.sql.Statement
import scala.collection.mutable.ArrayBuffer
import scala.util.Using

object Provider {
  // TODO: hardcoded schema
  private val queryDirectory = "SELECT * FROM Q.OBJECT_DIRECTORY"
  private val queryRemarks = "SELECT * FROM Q.OBJECT_REMARKS"
  private val queryData = "SELECT * FROM Q.OBJECT_DATA"
}

class Provider (connectionPool: ConnectionPool){

  def getCatalog: Catalog =
    import Provider.*

    val connection = connectionPool.connection
    val directories = new ArrayBuffer[ObjectDirectory]()
    val remarks = new ArrayBuffer[ObjectRemarks]()
    val data = new ArrayBuffer[ObjectData]()

    Using.Manager { use =>
      val stmt = use(connection.createStatement)
      val rs = use(stmt.executeQuery(queryDirectory))
      while (rs.next)
        val owner = rs.getString("OWNER")
        val name = rs.getString("NAME")
        val `type` = rs.getString("TYPE")
        val subtype = rs.getString("SUBTYPE")
        val objectlevel = rs.getInt("OBJECTLEVEL")
        val restricted = rs.getString("RESTRICTED")
        val model = rs.getString("MODEL")
        val created = rs.getDate("CREATED")
        val modified = rs.getDate("MODIFIED")
        val lastUsed = rs.getDate("LAST_USED")
        directories += (new ObjectDirectory(owner, name, `type`, subtype, objectlevel, restricted, model, created, modified, lastUsed))
    }.get

    Using.Manager { use =>
      val stmt = use(connection.createStatement)
      val rs = use(stmt.executeQuery(Provider.queryRemarks))
      while (rs.next)
        val owner = rs.getString("OWNER")
        val name = rs.getString("NAME")
        val `type` = rs.getString("TYPE")
        val rem = rs.getString("REMARKS")
        remarks += new ObjectRemarks(owner, name, `type`, rem)
    }.get

    Using.Manager { use =>
      val stmt = use(connection.createStatement)
      val rs = use(stmt.executeQuery(Provider.queryData))
      while (rs.next)
        val owner = rs.getString("OWNER")
        val name = rs.getString("NAME")
        val `type` = rs.getString("TYPE")
        val seq = rs.getShort("SEQ")
        val bytes = rs.getBytes("APPLDATA")
        data += ObjectData(owner, name, `type`, seq, bytes)
    }.get

    Catalog(data, remarks, directories)
  end getCatalog


  /** {@inheritDoc } */
  def close(): Unit = {
    // TODO: log
  }
}
