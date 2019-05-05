package catalog

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import org.scalatest.mockito.MockitoSugar

trait BrowserParser extends MockitoSugar {

  protected def parseTable(html: String, htmlElement: String): List[Element] = {
    JsoupBrowser().parseString(html) >> elementList(htmlElement)
  }

  protected def prepareStubBrowser(defaultHtml: String, urlHtml: (String,String)*): JsoupBrowser = {
    import org.mockito.ArgumentMatchers.anyString
    import org.mockito.Mockito.when

    val stubBrowser = mock[JsoupBrowser]

    when(stubBrowser.get(anyString())).thenReturn(JsoupBrowser.typed().parseString(defaultHtml))
    urlHtml.foreach(u => when(stubBrowser.get(u._1)).thenReturn(JsoupBrowser.typed().parseString(u._2)))
    stubBrowser
  }
}
