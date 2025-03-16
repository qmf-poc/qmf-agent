package qmf.poc.agent.runner

import com.ibm.qmf.api.{QMF, Query, QueryResults, SaveResultsToFileOptions}

import java.io.File
import java.nio.file.{Files, Path, Paths}
import scala.util.{Success, Try, Failure}

class QMFObjectRunnerTest extends munit.FunSuite:
  test("run query"):
    val ts = System.currentTimeMillis()
    val f = QMFObjectRunner.retrieveObjectHTML("db2inst1", "password", "DB2INST1", "ORG_TO_QMF", "html")
    println(System.currentTimeMillis() - ts)
    f match
      case Success(file) =>
        assertEquals(
          file,
          """﻿<html><meta http-equiv="content-type" content="text/html; charset=UTF-8"><body ><table border=1><tr><th>DEPTNUMB</th><th>DEPTNAME</th><th>MANAGER</th><th>DIVISION</th><th>LOCATION</th></tr>
                             |<tr><td>10</td><td>Head Office</td><td>160</td><td>Corporate</td><td>New York</td></tr>
                             |<tr><td>15</td><td>New England</td><td>50</td><td>Eastern</td><td>Boston</td></tr>
                             |<tr><td>20</td><td>Mid Atlantic</td><td>10</td><td>Eastern</td><td>Washington</td></tr>
                             |<tr><td>38</td><td>South Atlantic</td><td>30</td><td>Eastern</td><td>Atlanta</td></tr>
                             |<tr><td>42</td><td>Great Lakes</td><td>100</td><td>Midwest</td><td>Chicago</td></tr>
                             |<tr><td>51</td><td>Plains</td><td>140</td><td>Midwest</td><td>Dallas</td></tr>
                             |<tr><td>66</td><td>Pacific</td><td>270</td><td>Western</td><td>San Francisco</td></tr>
                             |<tr><td>84</td><td>Mountain</td><td>290</td><td>Western</td><td>Denver</td></tr>
                             |</table></body></html>""".stripMargin
        )
      case Failure(exception) =>
        fail(s"Query failed with exception: $exception")
  test("run query with error"):
    val ts = System.currentTimeMillis()
    val f = QMFObjectRunner.retrieveObjectHTML("db2inst1", "password", "DB2INST1", "null", "html")
    println(System.currentTimeMillis() - ts)
    f match
      case Success(file) =>
        assertEquals(
          file,
          """﻿<html><meta http-equiv="content-type" content="text/html; charset=UTF-8"><body ><table border=1><tr><th>DEPTNUMB</th><th>DEPTNAME</th><th>MANAGER</th><th>DIVISION</th><th>LOCATION</th></tr>
            |<tr><td>10</td><td>Head Office</td><td>160</td><td>Corporate</td><td>New York</td></tr>
            |<tr><td>15</td><td>New England</td><td>50</td><td>Eastern</td><td>Boston</td></tr>
            |<tr><td>20</td><td>Mid Atlantic</td><td>10</td><td>Eastern</td><td>Washington</td></tr>
            |<tr><td>38</td><td>South Atlantic</td><td>30</td><td>Eastern</td><td>Atlanta</td></tr>
            |<tr><td>42</td><td>Great Lakes</td><td>100</td><td>Midwest</td><td>Chicago</td></tr>
            |<tr><td>51</td><td>Plains</td><td>140</td><td>Midwest</td><td>Dallas</td></tr>
            |<tr><td>66</td><td>Pacific</td><td>270</td><td>Western</td><td>San Francisco</td></tr>
            |<tr><td>84</td><td>Mountain</td><td>290</td><td>Western</td><td>Denver</td></tr>
            |</table></body></html>""".stripMargin
        )
      case Failure(exception) =>
        fail(s"Query failed with exception: $exception")
