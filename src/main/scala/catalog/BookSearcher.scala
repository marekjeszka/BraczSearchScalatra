package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

class BookSearcher(browser: JsoupBrowser) extends Browser[Book] {

  override protected val catalogLink: String =
    "http://br-hip.pfsl.poznan.pl/ipac20/ipac.jsp?menu=search&aspect=subtab24&npp=10&ipp=50&spp=20&profile=br-mar&ri=&index=ALTITLE&term=%s&aspect=subtab24"

  override protected def getBrowser: JsoupBrowser = browser

  def this() = this(JsoupBrowser.typed())

  def searchByName(bookName: String): Seq[Book] = {
    val formattedLink = formatLink(bookName)
    getElements(formattedLink)(toBook).flatMap(findIsbn)
  }

  def toBook(el: Element): Option[Book] = {
    val elementsTd = el >> elementList("td")
    val title = elementsTd(1).text
    val author = elementsTd(3).text
    val year = elementsTd(5).text
    val link = elementsTd(1) >> elementList("a") match {
      case Nil => ""
      case l => l.head.attr("href")
    }
    Some(Book(title, author, IncorrectISBN, year, link))
  }

  def findIsbn(bookOption: Option[Book]): Option[Book] = {
    bookOption.flatMap { book =>
      val elements = parseLink(book.link).extract(elementList(":containsOwn(ISBN:)"))
      val siblings: Option[Iterable[Element]] = for {
        head <- elements.headOption
        parent <- head.parent
      } yield parent.siblings

      siblings flatMap {
        s => {
          val isbn: Option[String] = for (h <- s.headOption) yield h.text
          isbn flatMap { i => Some(book.copy(isbn = ISBN(i))) }
        }
      }
    }
  }
}
