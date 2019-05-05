package catalog

sealed abstract class ISBN

final case class CorrectISBN(isbn: String) extends ISBN
case object IncorrectISBN extends ISBN

object ISBN {
  def apply(isbn: String): ISBN = if (isIsbn(isbn)) CorrectISBN(isbn) else IncorrectISBN

  def isIsbn(isbn: String): Boolean = {
    if (isbn.matches("\\d{13}|\\d{10}|\\d{9}X")) {
      val isbnX = isbn.endsWith("X")
      val digits = if (isbnX) isbn.substring(0,9) else isbn
      val ints = (Stream.iterate(BigInt(digits))(_ / 10) takeWhile (_ != 0) map (_ % 10)).toList.reverse
      if (isbn.length == 10) {
        val sum = Range.inclusive(10, 1, -1).toList.zip(ints).map(a => a._1 * a._2).sum + (if (isbnX) 10 else 0)
        sum % 11 == 0
      } else {
        (List.fill(7)(1,3).flatMap(a => List(a._1,a._2)) :+ 1)
          .zip(ints)
          .map(a => a._1 * a._2)
          .sum % 10 == 0
      }
    }
    else
      false
  }
}
