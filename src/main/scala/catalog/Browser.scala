package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

trait Browser[A] {
  private val CELL_PADDING = "cellpadding"

  protected def catalogLink: String

  protected def getBrowser: JsoupBrowser

  protected def formatLink(command: String): String = catalogLink.format(command)

  protected def parseLink(link: String): Document = getBrowser.get(link)

  protected def getElements(link: String)(rowExtractor: Element => Option[A]): List[Option[A]] = {
    val table = parseLink(link)
      .extract(elementList(".tableBackground"))
      .filter(_.hasAttr(CELL_PADDING))
      .filter(a => "3".equals(a.attr(CELL_PADDING)))

    table match {
      case Nil => List()
      case t =>
        val locationsTr = t.head >> elementList("tr")
        locationsTr match {
          case Nil => List()
          case _ :: tail => tail.map(rowExtractor)
        }
    }
  }
}

