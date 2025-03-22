scalaVersion := "3.6.4"
version := "0.2.0-SNAPSHOT"
organization := "qmf.poc.service"
organizationName := "qmf"

val luceneVersion = "10.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "agent",
    assembly / mainClass := Some("qmf.poc.agent.main"),
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.6", // json library
      "com.ibm.db2" % "jcc" % "12.1.0.0", // db2 jdbc driver
      "org.slf4j" % "slf4j-api" % "2.0.17", // logging
      "ch.qos.logback" % "logback-classic" % "1.5.17", // logging
      // "org.slf4j" % "slf4j-simple" % "2.0.17", - does not work with virtual threads
      "org.scalameta" %% "munit" % "1.1.0" % Test // testing
    )
  )
//unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "resources"
Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources"
ThisBuild / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.first
  case x                             => (ThisBuild / assemblyMergeStrategy).value(x)
}

Compile / unmanagedJars += file("lib/QMFLib.jar")
Compile / unmanagedJars += file("lib/js.jar")

ThisBuild / javacOptions ++= Seq("--release", "11")
ThisBuild / scalacOptions ++= Seq("-java-output-version", "11")
