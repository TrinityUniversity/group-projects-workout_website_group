package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import models.Tables._
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.Json



class WorkoutDatabaseModel(db: Database)(implicit ec: ExecutionContext) {
  
  /*def validateUser(username: String, password: String): Future[Option[Int]] = {
    val query = Users.filter(_.username === username).result.headOption
    db.run(query).map {
      case Some(user) if BCrypt.checkpw(password, user.password) => Some(user.userId)
      case _ => None
    }
  }*/

  def validateUser(username: String, password: String): Future[Option[Int]] = {
    val matches = db.run(Users.filter(_.username === username).result)
    matches.map(userRows => userRows.headOption.flatMap {
      userRow => if (BCrypt.checkpw(password, userRow.password)) Some(userRow.userId) else None
    })
  }

  // Method to update user favorites
def updateUserFavorites(userId: Int, favoriteWorkouts: List[Int]): Future[Boolean] = {
  val favoritesArrayString = s"{${favoriteWorkouts.mkString(",")}}"  // This creates a string like "{1,2,3}"
  val updateAction = sqlu"UPDATE users SET favorites = $favoritesArrayString::integer[] WHERE user_id = $userId"
  db.run(updateAction).map(_ == 1)
}


def getWorkoutById(id: Int): Future[Option[WorkoutsRow]] = {
  db.run(Workouts.filter(_.id === id).result.headOption)
}




def createUser(username: String, password: String): Future[Either[String, Int]] = {
  val checkExists = Users.filter(_.username === username).exists.result
  db.run(checkExists).flatMap { exists =>
    if (!exists) {
      val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
      val emptyIntArray = "{}"  
      val action = sql"INSERT INTO users (username, favorites, password) VALUES ($username, $emptyIntArray::integer[], $hashedPassword) RETURNING user_id".as[Int].head
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

def getUsers(): Future[Seq[UsersRow]] = {
  db.run(Users.result)
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

