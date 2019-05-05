package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

class BookSearcher(browser: JsoupBrowser) {

  def this() = this(JsoupBrowser.typed())

  def searchByName(bookName: String): Seq[Book] = List(Book("Title", "Marek", IncorrectISBN, "1990", "http"))

}
