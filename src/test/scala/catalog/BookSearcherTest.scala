package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatra.test.scalatest.ScalatraFunSuite
import util.BrowserParser

class BookSearcherTest extends ScalatraFunSuite with BrowserParser {

  private val underTest = new BookSearcher()

  test("maps <table> to a Book") {
    val tableStandard = """<table><tr><td>1</td><td><a href="http://br-hip.pfsl.poznan.pl/">W godzinie apelu</a></td><td>&nbsp;</td><td>Jabski, Zachariasz S. (1940-2015).</td><td>Czochowa</td><td>1999.</td><td>&nbsp;</td></tr></table>"""
    val place = underTest.toBook(parseTable(tableStandard, "tr").head)

    place.get should be (Book("W godzinie apelu", "Jabski, Zachariasz S. (1940-2015).", IncorrectISBN, "1999.", "http://br-hip.pfsl.poznan.pl/"))
  }

  test("parses correct pages") {
    val correctHtml =
      """<table class="tableBackground" cellpadding="3"><tr></tr>
        |<tr><td></td><td><a href="books1.com">one</a></td><td></td><td>author1</td><td></td><td>1991</td><td></td></tr>
        |<tr><td></td><td><a href="books2.com">two</a></td><td></td><td>author2</td><td></td><td>1992</td><td></td></tr>
        |<tr><td></td><td>three</td><td></td><td>author3</td><td></td><td>1993</td><td></td></tr>
        |</table>""".stripMargin
    val htmlISBN1 =
      """<table><tr><td><a class="normalBlackFont1">ISBN:&nbsp;</a></td>
        |<td><table><tr><td valign="top"><a class="normalBlackFont1">9788380620438</a></td>
        |</tr></table></td></tr></table>""".stripMargin
    val htmlISBN2 =
      """<table><tr><td><a class="normalBlackFont1">ISBN:&nbsp;</a></td>
        |<td><table><tr><td valign="top"><a class="normalBlackFont1">9788380620410</a></td>
        |</tr></table></td></tr></table>""".stripMargin

    val availableBookHtml = prepareStubBrowser(correctHtml, ("books1.com", htmlISBN1), ("books2.com", htmlISBN2))
    val places = getStubbedBookSearcher(availableBookHtml).searchByName("")

    places.length should be (2)
    places.head should be (Book("one", "author1", ISBN("9788380620438"), "1991", "books1.com"))
    places(1) should be (Book("two", "author2", ISBN("9788380620410"), "1992", "books2.com"))
  }

  test("parses pages with ISBN") {
    val aBook = Book("title", "author", IncorrectISBN, "1997", "link.com")
    val htmlISBN =
      """<table><tr><td><a class="normalBlackFont1">ISBN:&nbsp;</a></td>
        |<td><table><tr><td valign="top"><a class="normalBlackFont1">9788380620438</a></td>
        |</tr></table></td></tr></table>""".stripMargin

    val book = getStubbedBookSearcher(prepareStubBrowser(htmlISBN)).findIsbn(Some(aBook))

    book.get should be (aBook.copy(isbn = ISBN("9788380620438")))
  }

  private def getStubbedBookSearcher(stubBrowser: JsoupBrowser) = new BookSearcher(stubBrowser)
}
