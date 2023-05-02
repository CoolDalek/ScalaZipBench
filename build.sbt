ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .enablePlugins(JmhPlugin)
  .settings(
    name := "ScalaZipBench",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "3.6.1",
      "co.fs2" %% "fs2-io" % "3.6.1",
      "org.typelevel" %% "cats-effect" % "3.4.10",
    )
  )
