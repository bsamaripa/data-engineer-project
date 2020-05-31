lazy val root = (project in file("."))
  .settings(
    name := "data-engineer-project",
    organization := "com.bsamaripa",
    version := "1.0.0",
    scalaVersion := "2.13.2",
    libraryDependencies ++= Seq(
      "com.github.pathikrit"       %% "better-files"    % "3.9.1",
      "org.typelevel"              %% "cats-effect"     % "2.1.3",
      "io.circe"                   %% "circe-core"      % "0.13.0",
      "io.circe"                   %% "circe-generic"   % "0.13.0",
      "io.circe"                   %% "circe-parser"    % "0.13.0",
      "ch.qos.logback"             % "logback-classic"  % "1.2.3",
      "com.github.tototoshi"       %% "scala-csv"       % "1.3.6",
      "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2",
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "utf-8",
      "-explaintypes",
      "-feature"
    ),
    fetchData := {
      IO.unzipURL(
        new URL(
          "https://s3-us-west-2.amazonaws.com/com.guild.us-west-2.public-data/project-data/the-movies-dataset.zip"),
        new File("temp"))
    }
  )

lazy val fetchData = taskKey[Unit]("Download the movie database to ./temp")
