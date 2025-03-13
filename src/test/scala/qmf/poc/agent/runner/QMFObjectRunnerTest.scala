package qmf.poc.agent.runner

import com.ibm.qmf.api.{QMF, Query, QueryResults, SaveResultsToFileOptions}

import java.io.File
import java.nio.file.{Files, Path, Paths}
import scala.util.{Success, Try, Failure}

class QMFObjectRunnerTest extends munit.FunSuite:
  test("run query"):
    val ts = System.currentTimeMillis()
    val f = QMFObjectRunner.runObject("db2inst1", "password", "DB2INST1", "ORG_TO_QMF")
    println(System.currentTimeMillis() - ts)
    f match
      case Success(file) =>
        assert(file.exists(), "The file should exist")
        println(file.getAbsoluteFile)
        file.delete()
      case Failure(exception) =>
        fail(s"Query failed with exception: $exception")
