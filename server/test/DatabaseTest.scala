import org.junit.runner._
import org.specs2.runner._
import play.api.test._
import scala.concurrent.ExecutionContext
import models.WorkoutDatabaseModel
import slick.jdbc.JdbcBackend.Database
import models.Tables._

@RunWith(classOf[JUnitRunner])
class DatabaseTest extends PlaySpecification {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  def withMySlickDatabase[T](block: Database => T): T = {
    val database = Database.forURL(
      url = "jdbc:postgresql://localhost/workoutapp",
      user = "tgreen",
      password = "password",
      driver = "org.postgresql.Driver"
    )
    try {
      block(database)
    } finally {
      database.close()
    }
  }

  "WorkoutDatabaseModel" should {

    "create a new user" in {
      val username = "testuser"
      val password = "password"
      withMySlickDatabase { database =>
        val model = new WorkoutDatabaseModel(database)
        val result = model.createUser(username, password).map(_.isRight must beTrue)
        await(result)
      }
    }

    "retrieve workouts" in {
      withMySlickDatabase { database =>
        val model = new WorkoutDatabaseModel(database)
        val result = model.getWorkouts().map(workouts => workouts must not be empty)
        await(result)
      }
    }

    "add a workout" in {
      val workout = WorkoutsRow(0, "Yoga Session", "http://video.url", 3, 5, 30, "Yoga", 100, 150)
      withMySlickDatabase { database =>
        val model = new WorkoutDatabaseModel(database)
        val result = model.addWorkout(workout).map(id => id must beGreaterThan(0))
        await(result)
      }
    }

    "update workout details" in {
      val workoutId = 1  
      val newLikes = 101
      val newViews = 201
      withMySlickDatabase { database =>
        val model = new WorkoutDatabaseModel(database)
        val result = model.updateWorkoutLikesViews(workoutId, newLikes, newViews).map(success => success must beTrue)
        await(result)
      }
    }

    "update user favorites" in {
      val userId = 1  
      val favorites = List(1, 2, 3)  
      withMySlickDatabase { database =>
        val model = new WorkoutDatabaseModel(database)
        val result = model.updateUserFavorites(userId, favorites).map(success => success must beTrue)
        await(result)
      }
    }

  }
}
