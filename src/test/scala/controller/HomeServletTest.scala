package controller

import catalog.{Book, BookSearcher, IncorrectISBN}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatra.test.scalatest._

class HomeServletTest extends ScalatraFunSuite with MockitoSugar {

  private val bookSearcherMock = mock[BookSearcher]

  addServlet(new HomeServlet(bookSearcherMock), "/*")

  test("GET / on HomeServlet should return status 200") {
    get("/") {
      status should be (200)
    }
  }

  test("GET /search/foo on HomeServlet should display book names") {
    when(bookSearcherMock.searchByName(anyString()))
      .thenReturn(List(Book("title","author",IncorrectISBN,"1997","")))

    get("/search/foo") {
      status should be (200)
      verify(bookSearcherMock).searchByName("foo")
      body should (include ("title") and include ("author") and include("1997"))
    }
  }

}
