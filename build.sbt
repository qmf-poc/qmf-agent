scalaVersion := "3.6.4"
version := "0.1.0-SNAPSHOT"
organization := "qmf.poc.service"
organizationName := "qmf"

val luceneVersion = "10.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "agent",
    assembly / mainClass := Some("qmf.poc.agent.main"),
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.6",
      "com.ibm.db2" % "jcc" % "12.1.0.0",
      "org.slf4j" % "slf4j-api" % "2.0.17",
      "ch.qos.logback" % "logback-classic" % "1.5.17",
      // "org.slf4j" % "slf4j-simple" % "2.0.17", - does not work with virtual threads
      "org.scalameta" %% "munit" % "1.1.0" % Test
    )
  )
//unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "resources"
Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources"
ThisBuild / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.first
  case x                             => (ThisBuild / assemblyMergeStrategy).value(x)
}
