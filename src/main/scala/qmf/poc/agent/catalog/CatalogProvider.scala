package qmf.poc.agent.catalog

import org.slf4j.LoggerFactory
import qmf.poc.agent.catalog.models.{Catalog, ObjectData, ObjectDirectory, ObjectRemarks}

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import scala.util.Using

class CatalogProvider(connectionPool: ConnectionPool) {

  def catalog: Option[Catalog] =
    import CatalogProvider.*

    val connection = connectionPool.connection match
      case Some(c) => c
      case _       => return None

    val directories = new ArrayBuffer[ObjectDirectory]()
    val remarks = new ArrayBuffer[ObjectRemarks]()
    val data = new ArrayBuffer[ObjectData]()

    logger.debug("Getting ObjectDirectory")
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
        val created = rs.getString("CREATED")
        val modified = rs.getString("MODIFIED")
        val lastUsed = rs.getString("LAST_USED")
        directories += (new ObjectDirectory(
          owner,
          name,
          `type`,
          subtype,
          objectlevel,
          restricted,
          model,
          created,
          modified,
          lastUsed
        ))
    }.get

    logger.debug("Getting ObjectRemarks")
    Using.Manager { use =>
      val stmt = use(connection.createStatement)
      val rs = use(stmt.executeQuery(CatalogProvider.queryRemarks))
      while (rs.next)
        val owner = rs.getString("OWNER")
        val name = rs.getString("NAME")
        val `type` = rs.getString("TYPE")
        val rem = rs.getString("REMARKS")
        remarks += new ObjectRemarks(owner, name, `type`, rem)
    }.get

    logger.debug("Getting ObjectData")
    try {
      Using.Manager { use =>
        val stmt = use(connection.createStatement)
        val rs = use(stmt.executeQuery(CatalogProvider.queryData))
        while (rs.next)
          val owner = rs.getString("OWNER")
          val name = rs.getString("NAME")
          val `type` = rs.getString("TYPE")
          // val seq = rs.getShort("SEQ")
          // val bytes = rs.getBytes("APPLDATA")
          val bytes = rs.getBytes("CONCATENATED_APPLDATA")
          data += ObjectData(owner, name, `type`, 1, bytes)
      }.get
    } catch case e: Exception => println(e)

    logger.debug("Construct catalog")
    Some(Catalog(data.toSeq, remarks.toSeq, directories.toSeq))
  end catalog

  /** {@inheritDoc } */
  def close(): Unit = {
    // TODO: log
  }
}

object CatalogProvider {
  private val logger = LoggerFactory.getLogger("catalog")
  // TODO: hardcoded schema
  private val queryDirectory = "SELECT * FROM Q.OBJECT_DIRECTORY"
  private val queryRemarks = "SELECT * FROM Q.OBJECT_REMARKS"
  private val queryData = """SELECT
                            |  OWNER,
                            |  NAME,
                            |  "TYPE",
                            |  LISTAGG(APPLDATA, '') WITHIN GROUP (ORDER BY SEQ) AS CONCATENATED_APPLDATA
                            |FROM
                            |  Q.OBJECT_DATA
                            |GROUP BY
                            |  OWNER, NAME, "TYPE"""".stripMargin

  /// "SELECT * FROM Q.OBJECT_DATA"

  def apply(connectionPool: ConnectionPool) = new CatalogProvider(connectionPool)
}
