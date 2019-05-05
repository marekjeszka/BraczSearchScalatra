package controller

import org.scalatra._

class HomeServlet extends ScalatraServlet {

  get("/") {
    views.html.hello()
  }

}
