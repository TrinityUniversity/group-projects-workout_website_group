import org.junit.runner._
import org.specs2.runner._
import play.api.test._
import play.api.db.Database
import play.api.db.Databases
import models.WorkoutDatabaseModel
import org.scalatestplus.play.PlaySpec


/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */


@RunWith(classOf[JUnitRunner])
class DatabaseTest() extends PlaySpecification {
  import models.WorkoutDatabaseModel

    

def withMyDatabase[T](block: Database => T) = {
  Databases.withDatabase(
  driver = "org.postgresql.Driver",
  url = "jdbc:postgresql://localhost/workoutapp?user=tgreen&password=password",
  name = "mydatabase",
  config = Map(
    "username" -> "tgreen",
    "password" -> "password"
    )
  )(block)
}


"WorkoutDatabseModel" should
{
    "create a new user" in {
    import models.WorkoutDatabaseModel

    // val isValid = WorkoutDatabaseModel.createUser("testuser", "1")
    // isValid mustBe 1

    withMyDatabase { database =>
    val connection = database.getConnection()
    
    connection
        .prepareStatement("select * from test where id = 10")
        .executeQuery()
        .next() must_== true
        }
    }

    



}







    /*
  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(app, FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithApplication {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("shouts out")
    }
  }
  */
  
}