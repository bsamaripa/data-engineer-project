package com.bsamaripa

import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import io.circe.parser._

import scala.util.{Failure, Success, Try}

case class Company(
    id: Int,
    name: String
)

case class Genre(
    id: Int,
    name: String
)

case class Movie(
    id: Int,
    title: String,
    releaseDate: String,
    budget: Long,
    revenue: Long,
    profit: Long,
    genres: List[Genre],
    companies: List[Company]
)

case class Rating(
    userId: Int,
    movieId: Int,
    rating: Float,
    timestamp: String
)

object Movie extends LazyLogging {
  def fromMap(m: Map[String, String]): Option[Movie] = {
    Try(
      Movie(
        id = m("id").toInt,
        title = m("title"),
        releaseDate = m("release_date"),
        budget = m("budget").toLong,
        revenue = m("revenue").toLong,
        profit = m("revenue").toLong - m("budget").toLong,
        genres = Genre.parseGenres(m("genres")).getOrElse(List.empty[Genre]),
        companies = Company.parseCompanies(m("production_companies")).getOrElse(List.empty[Company])
      )) match {
      case Success(movie) => Some(movie)
      case Failure(e) =>
        logger.error(s"${e.toString} for movie $m")
        None
    }
  }
}

object Rating extends LazyLogging {
  def fromMap(r: Map[String, String]): Option[Rating] = {
    Try(
      Rating(
        userId = r("userId").toInt,
        movieId = r("movieId").toInt,
        rating = r("rating").toFloat,
        timestamp = r("timestamp")
      )) match {
      case Success(rating) => Some(rating)
      case Failure(e) =>
        logger.warn(s"${e.toString} for rating $r")
        None
    }
  }
}

object Genre extends LazyLogging {
  def parseGenres(s: String): Option[List[Genre]] = {
    parse(s.replace("\'", "\"")) match {
      case Left(failure) =>
        logger.error(s"${failure.message} - $s")
        None
      case Right(json) =>
        json.as[List[Genre]] match {
          case Left(failure) =>
            logger.error(failure.toString)
            None
          case Right(genres) => Some(genres)
        }
    }
  }
}

// TODO: Sanitize data more prior to parsing.
//  Parsing of names is fragile because non-valid json characters are part of the value.
object Company extends LazyLogging {
  def parseCompanies(s: String): Option[List[Company]] = {
    parse(s.replace("\'", "\"")) match {
      case Left(failure) =>
        logger.error(s"${failure.message} - $s")
        None
      case Right(json) =>
        json.as[List[Company]] match {
          case Left(failure) =>
            logger.error(s"${failure.message} - $s")
            None
          case Right(companies) => Some(companies)
        }
    }
  }
}
