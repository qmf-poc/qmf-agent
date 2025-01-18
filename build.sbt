scalaVersion := "3.6.2"
version := "0.1.0-SNAPSHOT"
organization := "qmf.poc.service"
organizationName := "qmf"

val zioVersion = "2.1.14"
val zioConfigVersion = "4.0.3"
val zioHttpVersion = "3.0.1"
val luceneVersion = "10.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "agent",
    assembly / mainClass := Some("mf.poc.agent.Main"),
    libraryDependencies ++= Seq(
      "io.spray" %%  "spray-json" % "1.3.6",
      "com.ibm.db2" % "jcc" % "12.1.0.0",
      "org.scalameta" %% "munit" % "1.0.4" % Test
    )
  )
