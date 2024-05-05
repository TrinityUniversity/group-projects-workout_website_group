package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models._
import slick.jdbc.PostgresProfile.api._

@Singleton
class WorkoutController @Inject()(cc: ControllerComponents, dbConfigProvider: play.api.db.slick.DatabaseConfigProvider) extends AbstractController(cc) {
  val dbConfig = dbConfigProvider.get[slick.jdbc.JdbcProfile]
  import dbConfig._
  import profile.api._
  import models._
  import models.ReadsAndWrites._
  import play.api.libs.json._

  private val model = new WorkoutDatabaseModel(db)

  // Helper function to process JSON body
  def withJsonBody[A](f: A => Future[Result])(implicit request: Request[JsValue], reads: Reads[A]): Future[Result] = {
    request.body.validate[A] match {
        case JsSuccess(a, _) => f(a)
        case JsError(errors) => Future.successful(BadRequest("JSON validation error: " + JsError.toJson(errors)))
    }
}



def withSessionUserid(f: Int => Future[Result])(implicit request: Request[_]): Future[Result] = {
  request.session.get("userid").map(userid => f(userid.toInt))
    .getOrElse(Future.successful(BadRequest("User not found")))
}


  // Example endpoint to view workouts
  def viewWorkouts = Action.async { implicit request =>
    withSessionUserid { userId =>
      model.getWorkouts().map(workouts => Ok(Json.toJson(workouts)))
    }
  }

  // Endpoint to add a workout
def addWorkout = Action.async(parse.json) { implicit request =>
  withSessionUserid { userId =>
    withJsonBody[WorkoutData] { workoutData =>
      val workoutsRow = Tables.WorkoutsRow(
        id = 0,
        name = workoutData.name,
        videoUrl = workoutData.videoUrl,
        sweatLevel = workoutData.sweatLevel,
        intensity = workoutData.intensity,
        workLength = workoutData.workLength,
        workoutType = workoutData.workoutType,
        likes = workoutData.likes,
        views = workoutData.views
      )
      model.addWorkout(workoutsRow).map { workoutId =>
        if (workoutId > 0) Ok(Json.obj("status" -> "success", "workoutId" -> workoutId))
        else BadRequest(Json.obj("status" -> "error", "message" -> "Failed to add workout"))
      }
    }
  }
}




  // Endpoint to update workout details
def updateWorkout = Action.async(parse.json) { implicit request =>
  withSessionUserid { userId =>
    withJsonBody[(Int, Int, Int)] { case (workoutId, likes, views) =>
      model.updateWorkoutLikesViews(workoutId, likes, views).map { success =>
        if (success) Ok(Json.obj("status" -> "success"))
        else BadRequest(Json.obj("status" -> "error", "message" -> "Failed to update workout"))
      }
    }
  }
}

  // Endpoint to update favorites
def updateFavorites = Action.async(parse.json) { implicit request =>
  withSessionUserid { userId =>
    withJsonBody[List[Int]] { favoriteWorkouts =>
      model.updateUserFavorites(userId, favoriteWorkouts).map { success =>
        if (success) Ok(Json.obj("status" -> "success", "message" -> "Favorites updated successfully"))
        else BadRequest(Json.obj("status" -> "error", "message" -> "Failed to update favorites"))
      }
    }
  }
}


  // Validate user credentials
  def validate = Action.async(parse.json) { implicit request: Request[JsValue] =>
  withSessionUserid { userId =>
    withJsonBody[UserData] { ud =>
      model.validateUser(ud.username, ud.password).map {
        case Some(userid) => Ok(Json.toJson(true))
          .withSession("username" -> ud.username, "userid" -> userid.toString)
        case None => Ok(Json.toJson(false))
      }
    }
  }
}


  // Create a new user
def createUser = Action.async(parse.json) { implicit request: Request[JsValue] =>
  println(s"Received request to create user: ${request.body}")
  withSessionUserid { userId =>
    withJsonBody[UserData] { ud =>
      println(s"Parsed UserData: $ud")
      model.createUser(ud.username, ud.password).map {
        case Right(userid) =>
          println(s"User creation successful: $userid")
          Ok(Json.toJson(true)).withSession("username" -> ud.username, "userid" -> userid.toString)
        case Left(error) =>
          println(s"User creation failed: $error")
          BadRequest(Json.obj("error" -> error))
      }
    }
  }
}


  // Endpoint for user logout
  def logout = Action { implicit request =>
    Ok(Json.toJson(true)).withNewSession
  }
}
