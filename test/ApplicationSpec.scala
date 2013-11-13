import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {
  

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Your new application is ready.")
    }

    "get new token for unknown user" in new WithApplication{
      val data = route(FakeRequest(GET, "/token?userId=123&hardSecret=noSecret")).get

      status(data) must equalTo(OK)
      contentType(data) must beSome.which(_ == "application/json")
      contentAsJson(data).as[String] must equalTo("s1")
    }
    "get short code for long internet link" in new WithApplication{
      val data = route(FakeRequest(POST, "/link?url=www.computerra.ru&token=s1")).get

      status(data) must equalTo(OK)
      contentAsJson(data).as[String].length must equalTo(2)
    }

  }
}
