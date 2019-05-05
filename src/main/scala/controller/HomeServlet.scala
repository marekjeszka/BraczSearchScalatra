package controller

import catalog.BookSearcher
import org.scalatra._

class HomeServlet(bookSearcher: BookSearcher) extends ScalatraServlet {

  get("/") {
    views.html.index()
  }

  get("/search/:inputText") {
    val books = bookSearcher.searchByName(params("inputText"))
    Ok(views.html.Books(books))
  }

}
