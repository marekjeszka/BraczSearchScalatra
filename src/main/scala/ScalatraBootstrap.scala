import catalog.{BookLocator, BookSearcher}
import controller.HomeServlet
import javax.servlet.ServletContext
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new HomeServlet(new BookSearcher(), new BookLocator()), "/*")
  }
}
