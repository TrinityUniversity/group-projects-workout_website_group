package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import models.Tables._
import org.mindrot.jbcrypt.BCrypt
import scala.util.Try
import play.api.libs.json.Json



class WorkoutDatabaseModel(db: Database)(implicit ec: ExecutionContext) {
  

  def validateUser(username: String, password: String): Future[Option[Int]] = {
    val matches = db.run(Users.filter(_.username === username).result)
    matches.map(userRows => userRows.headOption.flatMap {
      userRow => if (BCrypt.checkpw(password, userRow.password)) Some(userRow.userId) else None
    })
  }




  // def favorite(workoutName: String, userId: Int): Future[Boolean] = {
  //   val action = for {
  //     workoutMatch <- Workouts.filter(_.name === workoutName).result.headOption
  //     userOption <- Users.filter(_.userId === userId).result.headOption
  //     workouts <- userOption match {
  //       case Some(user) => user.favorites match {
  //         case Some(favoritesString) =>
  //           val favoriteIds = parseFavorites(favoritesString)
  //           val newFavorites = favoriteIds :+ workoutMatch.get.id
  //       }
  //     }
  //   } yield workouts

  //   db.run(action).map(_ > 0)
  // }
  def favorite(workoutName: String, userId: Int): Future[Boolean] = {
  val action = for {
    workoutOption <- Workouts.filter(_.name === workoutName).result.headOption
    userOption <- Users.filter(_.userId === userId).result.headOption
    result <- (workoutOption, userOption) match {
      case (Some(workout), Some(user)) =>
        val newFavorites = user.favorites match {
          case Some(favoritesString) =>
            val favoriteIds = parseFavorites(favoritesString)
            if (favoriteIds.contains(workout.id)) favoriteIds
            else favoriteIds :+ workout.id
          case None => List(workout.id)
        }
        val newFavoritesString = s"{${newFavorites.mkString(",")}}"
        Users.filter(_.userId === userId)
          .map(_.favorites).update(Some(newFavoritesString)).map(_ == 1)
      case _ => DBIO.successful(false)
    }
  } yield result

  db.run(action.transactionally)
}




// def favorite(workoutName: String, userId: Int): Future[Int] = {
//   val user = db.run(Users.filter(_.userId === userId).result.head)
//   val workoutMatches = db.run(Workouts.filter(_.name === workoutName).result)
//   val newFavs= user.favorites += workoutMatches
//   db.run(newFavs)
// }

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


def searchWorkouts(query: String): Future[Seq[WorkoutsRow]] = {
    db.run(Workouts.filter(workout => workout.name like s"%$query%").result)
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

    def getFavoriteWorkouts(userId: Int): Future[Seq[WorkoutsRow]] = {
    val action = for {
      userOption <- Users.filter(_.userId === userId).result.headOption
      workouts <- userOption match {
        case Some(user) => user.favorites match {
          case Some(favoritesString) =>
            val favoriteIds = parseFavorites(favoritesString)
            Workouts.filter(_.id inSet favoriteIds).result
          case None => DBIO.successful(Seq.empty[WorkoutsRow])
        }
        case None => DBIO.successful(Seq.empty[WorkoutsRow])
      }
    } yield workouts

    db.run(action)
  }

// Had to make this to parse things since tables.scala is being weird.
  private def parseFavorites(favoritesString: String): List[Int] = {
    favoritesString.trim.stripPrefix("{").stripSuffix("}").split(",").toList.flatMap(s => Try(s.toInt).toOption)
  }
  private def parseFavoritesToString(favoritesString: String): List[String] = {
    favoritesString.trim.stripPrefix("{").stripSuffix("}").split(",").toList
  }

}
