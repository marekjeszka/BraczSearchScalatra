package catalog

import com.github.nscala_time.time.Imports.{DateTime, DateTimeFormat}
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success, Try}

sealed trait BookLocation {
  val address: String
}

case class CurrentBookLocation(address: String) extends BookLocation

case class FutureBookLocation(address: String, returnDate: DateTime)
extends BookLocation {
  override def toString: String = s"$address ${FutureBookLocation.dateAsString(returnDate)}"
}

object FutureBookLocation {
  private lazy val formatter = DateTimeFormat.forPattern(ConfigFactory.load().getString("braczsearch.dateFormat"))

  def dateAsString(date: DateTime): String = formatter.print(date)

  def apply(address: String, returnDate: String): BookLocation = {
    Try(formatter.parseDateTime(returnDate)) match {
      case Failure(_) => IncompleteBookLocation(address)
      case Success(t) => FutureBookLocation(address, t)
    }
  }
}

case class IncompleteBookLocation(address: String) extends BookLocation
