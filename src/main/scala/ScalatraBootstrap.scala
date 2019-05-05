import catalog.BookSearcher
import controller.HomeServlet
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new HomeServlet(new BookSearcher()), "/*")
  }
}
