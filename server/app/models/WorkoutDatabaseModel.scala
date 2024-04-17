package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import models.Tables._
import org.mindrot.jbcrypt.BCrypt

class WorkoutDatabaseModel(db: Database)(implicit ec: ExecutionContext) {

  // Validates user credentials and returns user ID if successful
  def validateUser(username: String, password: String): Future[Option[Int]] = {
    val query = Users.filter(_.username === username).result.headOption
    db.run(query).map {
      case Some(user) if BCrypt.checkpw(password, user.password) => Some(user.userId)
      case _ => None
    }
  }

  // Creates a new user with a hashed password, returns user ID if creation is successful
  def createUser(username: String, password: String): Future[Option[Int]] = {
    val checkExists = Users.filter(_.username === username).exists.result
    db.run(checkExists).flatMap { exists =>
      if (!exists) {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val action = (Users returning Users.map(_.userId)) += UsersRow(-1, username, hashedPassword)
        db.run(action).map(Some(_))
      } else {
        Future.successful(None)
      }
    }
  }

  // Retrieves all workouts matching specific criteria
  def getWorkouts(userId: Int, workoutType: String): Future[Seq[WorkoutsRow]] = {
    val query = Workouts.filter(w => w.userId === userId && w.workoutType === workoutType).result
    db.run(query)
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
