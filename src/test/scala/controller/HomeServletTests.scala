package controller

import org.scalatra.test.scalatest._

class HomeServletTests extends ScalatraFunSuite {

  addServlet(classOf[HomeServlet], "/*")

  test("GET / on HomeServlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
