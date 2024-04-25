package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import models.Tables._
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.Json


class WorkoutDatabaseModel(db: Database)(implicit ec: ExecutionContext) {
  
  // Validates user credentials and returns user ID if successful
  def validateUser(username: String, password: String): Future[Option[Int]] = {
    val query = Users.filter(_.username === username).result.headOption
    db.run(query).map {
      case Some(user) if BCrypt.checkpw(password, user.password) => Some(user.userId)
      case _ => None
    }
  }

  // Method to update user favorites
def updateUserFavorites(userId: Int, favoriteWorkouts: List[Int]): Future[Boolean] = {
  val favoritesString = Json.toJson(favoriteWorkouts).toString()  // Convert List[Int] to JSON string
  val updateAction = Users.filter(_.userId === userId).map(u => u.favorites).update(Some(favoritesString))
  db.run(updateAction).map(_ == 1)
}



  // Creates a new user with a hashed password, returns user ID if creation is successful
def createUser(username: String, password: String): Future[Either[String, Int]] = {
  val checkExists = Users.filter(_.username === username).exists.result
  db.run(checkExists).flatMap { exists =>
    if (!exists) {
      val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
      val action = (Users returning Users.map(_.userId)) += UsersRow(-1, username, None, hashedPassword)
      db.run(action).map(id => Right(id))  
    } else {
      Future.successful(Left("Username already exists"))  
    }
  }.recover {
    case ex: Exception => Left(s"An error occurred: ${ex.getMessage}")
  }
}



def getWorkouts(): Future[Seq[WorkoutsRow]] = {
  db.run(Workouts.result)
}


  // Adds a workout for a specific user
  def addWorkout(workout: WorkoutsRow): Future[Int] = {
    val action = (Workouts returning Workouts.map(_.id)) += workout
    db.run(action)
  }

  // Updates workout details such as likes or views
  def updateWorkoutLikesViews(workoutId: Int, likes: Int, views: Int): Future[Boolean] = {
    val query = Workouts.filter(_.id === workoutId).map(w => (w.likes, w.views)).update((likes, views))
    db.run(query).map(_ > 0)
  }
}

