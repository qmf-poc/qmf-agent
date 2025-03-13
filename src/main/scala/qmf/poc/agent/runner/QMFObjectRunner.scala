package qmf.poc.agent.runner

import com.ibm.qmf.api.{QMF, Query, QueryResults, SaveResultsToFileOptions}

import java.io.File
import java.nio.file.{Files, Path, Paths}
import scala.util.Try

object QMFObjectRunner:
  val db2cs: String =
    Option(System.getProperty("agent.db2cs")).getOrElse("jdbc:db2://qmfpoc.s4y.solutions:50000/sample")

  val qmfFolder: String =
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

  val qmfConnection: String =
    Option(System.getProperty("qmf.connection")).getOrElse("Connection to Test1")

  val qmfUser: String =
    Option(System.getProperty("qmf.user")).getOrElse("admin")

  val qmfPassword: String =
    Option(System.getProperty("qmf.password")).getOrElse("password")

  val qmfDatasource: String =
    Option(System.getProperty("qmf.datasource")).getOrElse("Test1 ds")

  def runObject(user: String, password: String, owner: String, name: String): Try[File] = Try {
    val api = new QMF(qmfFolder)
    api.setActiveRepository(qmfConnection, qmfUser, qmfPassword, user, password)
    val session = api.createSession(qmfDatasource, user, password)

    session.retrieveObject(owner, name) match {
      case query: Query =>
        val options = SaveResultsToFileOptions(session)
        val tempFile = Files.createTempFile("qmf_run", ".html")
        options.setFileName(tempFile.toAbsolutePath.toString)

        query.run()
        val results = query.getResults()
        results.saveToFile(options, QueryResults.DATA_HTML, "")
        tempFile.toFile
      case other =>
        throw Exception(s"Unsupported object type $other")
    }
  }
