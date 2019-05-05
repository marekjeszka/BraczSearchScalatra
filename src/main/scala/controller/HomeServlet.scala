package controller

import catalog._
import org.scalatra._
import views.html.{Books, Locations}

class HomeServlet(bookSearcher: BookSearcher, bookLocator: BookLocator) extends ScalatraServlet {

  get("/") {
    views.html.index()
  }

  get("/search/:inputText") {
    val inputText = params("inputText")
    ISBN(inputText) match {
      case CorrectISBN(isbn) =>
        val multipleEntries = bookLocator.isMultipleEntries(isbn)
        if (multipleEntries._1)
          Ok(Books.render(multipleEntries._2))
        else {
          val places = bookLocator.getPlacesGrouped(isbn)
          Ok(Locations.render(places, bookLocator.getBookName(isbn)))
        }
      case IncorrectISBN =>
        val books = bookSearcher.searchByName(inputText)
        Ok(Books.render(books))
    }
  }

  post("/searchByLink") {
    val link = request.body
    val places = bookLocator.getPlacesGroupedViaLink(link)
    Ok(Locations.render(places, bookLocator.getBookNameViaLink(link)))
  }

}
