package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.scalatest._
import util.BrowserParser

class BookLocatorSpec extends FunSuite with Matchers with BrowserParser {

  private val bookLocator = new BookLocator()
  private val page: String = "http://test1.html"

  test("maps <table> to Place") {
    val tableStandard = """<table><tr><td><a title="Egzemplarze">Muszkowska</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Blokada</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place1 = bookLocator.toPlace(parseTable(tableStandard, "tr").head)

    place1.get should be (IncompleteBookLocation("Muszkowska"))

    val tableReadingRoom = """<table><tr><td><a title="Egzemplarze">Rolna</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Dostępny</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place2 = bookLocator.toPlace(parseTable(tableReadingRoom, "tr").head)

    place2.get should be (IncompleteBookLocation("Rolna"))

    val tableAvailable = """<table><tr><td><a title="Egzemplarze">Rolna</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Na półce</a></td><td>&nbsp;</td><td><a>Dodaj</a></td></tr></table>"""
    val place3 = bookLocator.toPlace(parseTable(tableAvailable, "tr").head)

    place3.get should be (CurrentBookLocation("Rolna"))

    val tableTaken = """<table><tr><td><a title="Egzemplarze">Błękitna</a></td><td><a title="Egzemplarze">Literatura</a></td><td><a title="Egzemplarze">16F/29912</a></td><td><a title="Egzemplarze">821.111-3</a></td><td><a title="Egzemplarze">wypo&#380;yczane</a></td><td><a title="Egzemplarze">Wypo&#380;yczony</a></td><td>04/01/2017</td><td><a>Dodaj</a></td></tr></table>"""
    val place4 = bookLocator.toPlace(parseTable(tableTaken, "tr").head)

    place4.get should be (FutureBookLocation("Błękitna", "04/01/2017"))
  }

  private def getStubbedBookLocator(stubBrowser: JsoupBrowser) = {
    new BookLocator(stubBrowser)
  }

  test("handles incorrect pages") {
    val emptyHtml = prepareStubBrowser("<html></html>")
    getStubbedBookLocator(emptyHtml).getAllPlaces(page) should be (List())

    val emptyPage = prepareStubBrowser("")
    getStubbedBookLocator(emptyPage).getAllPlaces(page) should be (List())

    val emptyTable = prepareStubBrowser("""<table class="tableBackground" cellpadding="3"></table>""")
    getStubbedBookLocator(emptyTable).getAllPlaces(page) should be (List())

    val partialTable = prepareStubBrowser("""<table class="tableBackground" cellpadding="3"><tr><td></td></tr><tr></tr></table>""")
    getStubbedBookLocator(partialTable).getAllPlaces(page) should be (List())
  }

  test("parses correct page") {
    val correctHtml =
      """<table class="tableBackground" cellpadding="3"><tr></tr>
        |<tr><td>F10 Robocza</td><td></td><td></td><td></td><td>Wypożyczane na 30 dni</td><td>Na półce</td><td></td></tr>
        |<tr><td>F11 Marcinkowskiego</td><td></td><td></td><td></td><td>Czytelnia</td><td>%s</td><td></td></tr>
        |<tr><td>F12 Rolna</td><td></td><td></td><td></td><td>Czytelnia</td><td>Dostępny</td><td></td></tr>
        |</table>""".stripMargin

    val availableBookHtml = prepareStubBrowser(correctHtml)
    val places = getStubbedBookLocator(availableBookHtml).getPlacesGrouped(page)
    places.available.length should be (1)
    places.incomplete.length should be (2)
    places.available.head should be (CurrentBookLocation("F10 Robocza"))
    places.incomplete.head should be (IncompleteBookLocation("F11 Marcinkowskiego"))
    places.incomplete(1) should be (IncompleteBookLocation("F12 Rolna"))
  }

  test("finds book name") {
    val html =
      """<table class="tableBackground" cellpadding="3"><tr>
        |<td><a class="largeAnchor" title="Book name" href="test123.com">test123</a></td>
        |</tr></table>"""

    val book = getStubbedBookLocator(prepareStubBrowser(html)).getBookName(page)

    book.get should be ("Book name")
  }

  test("handles multiple entries") {
    val html =
      """<table class="tableBackground" cellpadding="3"><tr>
        |<td><a class="mediumBoldAnchor" href="javascript:buildNewList('http%3A%2F%2Flink1.com','true')">book123</a></td>
        |<td><a class="mediumBoldAnchor" href="javascript:buildNewList('http%3A%2F%2Flink2.com','true')">book456</a></td>
        |</tr></table>"""

    val books = getStubbedBookLocator(prepareStubBrowser(html)).isMultipleEntries("9788374800808")

    books._1 should be (true)
    books._2.length should be (2)
    books._2.head should be (Book("book123","",ISBN("9788374800808"),"","http://link1.com"))
    books._2(1)   should be (Book("book456","",ISBN("9788374800808"),"","http://link2.com"))
  }

  test("handles single entries") {
    val html =
      """<table class="tableBackground" cellpadding="3"><tr>
        |<td><a href="javascript:buildNewList('http%3A%2F%2Flink1.com','true')">book123</a></td>
        |<td><a href="javascript:buildNewList('http%3A%2F%2Flink2.com','true')">book456</a></td>
        |</tr></table>"""

    val books = getStubbedBookLocator(prepareStubBrowser(html)).isMultipleEntries("9788374800808")

    books._1 should be (false)
    books._2 should be (Nil)
  }
}
