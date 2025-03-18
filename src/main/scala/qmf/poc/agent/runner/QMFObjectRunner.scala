package qmf.poc.agent.runner

import com.ibm.qmf.api.{QMF, Query, QueryResults, SaveResultsToFileOptions}

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import scala.util.Try

object QMFObjectRunner:
  val db2cs: String =
    Option(System.getProperty("agent.db2cs")).getOrElse("jdbc:db2://qmfdb2.s4y.solutions:50000/sample")

  private val qmfFolder: String =
    Option(System.getProperty("qmf.folder")).getOrElse(
      Paths
        .get(
          Option(System.getenv("HOME")).getOrElse(System.getProperty("user.home")),
          "Application Data",
          "IBM",
          "QMF for WebSphere"
        )
        .toString
    )
    // `/Users/dsa/Application Data/IBM/QMF for WebSphere`

  private val qmfConnection: String =
    Option(System.getProperty("qmf.connection")).getOrElse("Connection to Test1")

  private val qmfUser: String =
    Option(System.getProperty("qmf.user")).getOrElse("admin")

  private val qmfPassword: String =
    Option(System.getProperty("qmf.password")).getOrElse("password")

  private val qmfDatasource: String =
    Option(System.getProperty("qmf.datasource")).getOrElse("Test1 ds")

  def retrieveObjectHTML(user: String, password: String, owner: String, name: String, format: String): Try[String] = Try {
    val api = new QMF(qmfFolder)
    api.setActiveRepository(qmfConnection, qmfUser, qmfPassword, user, password)
    val session = api.createSession(qmfDatasource, user, password)

    session.retrieveObject(owner, name) match {
      case query: Query =>
        val options = SaveResultsToFileOptions(session)
        val tempFile = Files.createTempFile("qmf_run", ".html")
        try {
          options.setFileName(tempFile.toAbsolutePath.toString)
          query.run()
          val results = query.getResults()
          results.saveToFile(options, QueryResults.DATA_HTML, "")
          Files.readString(tempFile, StandardCharsets.UTF_8)
        } finally {
          Files.deleteIfExists(tempFile)
        }
      case null =>
        throw Exception(s"object (owner=$owner, name=$name) not found")
      case other =>
        throw Exception(s"Unsupported object type $other")
    }
  }
