/*
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification with OneServerPerSuite with OneBrowserPerTest with HtmlUnitFactory {

  "Application" should {
    "work from within a browser" in {
      go to ("http://localhost:" + port)
      pageTitle must contain("Home")

      // Simulate user registration
      click on "registerLink"
      textField("username").value = "newUser"
      pwdField("password").value = "newPassword"
      submit()

      // Check login functionality
      textField("username").value = "newUser"
      pwdField("password").value = "newPassword"
      submit()
      
      // Navigate to workouts page and verify content
      go to ("http://localhost:" + port + "/workouts")
      pageSource must contain("Workouts")
    }
  }
}
*/
