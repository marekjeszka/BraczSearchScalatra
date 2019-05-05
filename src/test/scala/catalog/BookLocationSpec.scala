package catalog

import org.scalatest._

class BookLocationSpec extends FunSuite with Matchers {
  test("orders book locations by name") {
    val place1 = FutureBookLocation("street", "01/05/2017")
    val place2 = FutureBookLocation("street", "01/02/2017")
    val place3 = IncompleteBookLocation("street")
    val sorted = List(place1, place2, place3).sorted(AvailabilityOrdering)

    sorted.head should be (place2)
    sorted(1)   should be (place1)
    sorted(2)   should be (place3)
  }

  test("prints book location") {
    FutureBookLocation("street", "01/01/2017").toString should be ("street 01/01/2017")
  }
}
