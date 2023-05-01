ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaZipBench"
  )

enablePlugins(JmhPlugin)

libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-core" % "3.6.1",
  "co.fs2" %% "fs2-io" % "3.6.1",
  "org.typelevel" %% "cats-effect" % "3.4.10",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.23.0",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.23.0" % "compile-internal",
)
