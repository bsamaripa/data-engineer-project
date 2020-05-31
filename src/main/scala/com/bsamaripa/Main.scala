package com.bsamaripa

import java.io.File

import better.files.{File => ScalaFile, _}
import cats.effect.{ExitCode, IO, IOApp}
import com.github.tototoshi.csv._
import com.typesafe.scalalogging.LazyLogging

object Main extends IOApp with LazyLogging {
  def run(args: List[String]): IO[ExitCode] = {
    for {
      movies  <- readCSVFile("temp/movies_metadata.csv", Movie.fromMap)
      ratings <- readCSVFile("temp/ratings_small.csv", Rating.fromMap)
      genres         = movies.flatMap(_.genres).toSet
      companies      = movies.flatMap(_.companies).toSet
      movieToGenre   = movies.flatMap(m => m.genres.map(g => (m.id, g.id)))
      movieToCompany = movies.flatMap(m => m.companies.map(c => (m.id, c.id)))
      _ <- writeToFile(movies, "movies")
      _ <- writeToFile(genres, "genres")
      _ <- writeToFile(companies, "companies")
      _ <- writeToFile(ratings, "ratings")
      _ <- writeToFile(movieToGenre, "moviesToGenre")
      _ <- writeToFile(movieToCompany, "moviesToCompany")
    } yield ExitCode.Success
  }

  def readCSVFile[A](file: String, f: Map[String, String] => Option[A]): IO[List[A]] = {
    for {
      reader <- IO(CSVReader.open(new File(file)))
      res    <- IO(reader.allWithHeaders.flatMap(f))
      _ = reader.close()
    } yield res
  }

  def writeToFile[A](lines: Iterable[A], filename: String): IO[Unit] = {

    for {
      _    <- IO(ScalaFile("temp/output").createDirectoryIfNotExists())
      file <- IO(ScalaFile(s"temp/output/$filename.log"))
      _    <- IO(file.createFileIfNotExists())
      res  <- IO(lines.foreach(m => file.appendLine(m.toString)("UTF-8")))
    } yield res
  }
}
