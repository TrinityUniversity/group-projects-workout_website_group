package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models._
import slick.jdbc.PostgresProfile.api._
import play.api.data._
import play.api.data.Forms._

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
// def updateFavorites = Action.async(parse.json) { implicit request =>
//   withSessionUserid { userId =>
//     withJsonBody[List[Int]] { favoriteWorkouts =>
//       model.updateUserFavorites(userId, favoriteWorkouts).map { success =>
//         if (success) Ok(Json.obj("status" -> "success", "message" -> "Favorites updated successfully"))
//         else BadRequest(Json.obj("status" -> "error", "message" -> "Failed to update favorites"))
//       }
//     }
//   }
// }


def getUsername = Action.async(parse.json) { implicit request =>
    withJsonBody[UserData] { ud =>
      model.validateUser(ud.username, ud.password).map {
        case Some(userid) => Ok(Json.toJson(true))
          .withSession("username" -> ud.username, "userid" -> userid.toString)
        case None => Ok(Json.toJson(false))
      }
    }
}

  // Validate user credentials
  def validate = Action.async { implicit request =>
        val username = request.body.asFormUrlEncoded.get("username").head
        val password = request.body.asFormUrlEncoded.get("password").head
      model.validateUser(username, password).map {
        case Some(userid) => Ok(views.html.home(username)).withSession("username" ->username)
          .withSession("username" -> username)
        case None => Redirect(routes.WorkoutController.login)
      }
  }


  // Create a new user
def createUser = Action.async { implicit request =>
  val username = request.body.asFormUrlEncoded.get("username").head
  val password = request.body.asFormUrlEncoded.get("password").head

      model.createUser(username, password).map {
        case Right(userid) =>
           Ok(views.html.home(username)).withSession("username" ->username)
        case Left(error) =>
          BadRequest(Json.obj("error" -> error))
      }
    }
<<<<<<< HEAD
def favoriteWorkout2 = Action.async(parse.json) { implicit request =>
=======


def favoriteWorkout = Action.async(parse.json) { implicit request =>
>>>>>>> 2b135605af2aa48261978c7a68ae55e20c1382ed
  withSessionUserid { userId =>
    withJsonBody[String] { workoutName =>
      model.favorite(workoutName, userId).map { success =>
        if (success > 0) Ok(Json.obj("status" -> "success"))
        else BadRequest(Json.obj("status" -> "error", "message" -> "Failed to favorite workout"))
      }
    }
  }
}
def favoriteWorkout = Action.async { implicit request =>
  val workoutOption = request.session.get("workoutInput").head
  val userId = request.session.get("userid").head.toInt
      model.favorite(workoutOption, userId).map {
        case success =>  Ok(views.html.myVideos(Seq("15 min STANDING ARM WORKOUT | With Dumbbells | Shoulders, Biceps and Triceps","20 Minute Full Body Cardio HIIT Workout [NO REPEAT]"), Seq("https://www.youtube.com/watch?v=d7j9p9JpLaE", "https://www.youtube.com/watch?v=M0uO8X3_tEA&t=1512s"),Seq("💪","🤾")))
        case _ => BadRequest(Json.obj("status" -> "error", "message" -> "Failed to favorite workout"))
      }
  }

// def favoriteWorkout2 = Action.async(parse.json) { implicit request =>
//   withSessionUserid { userId =>
//     val workoutOption = request.session.get("workoutInput")
//       workoutOption.map{ workoutName =>
//       model.favorite(workoutName, userId).map { success =>
//         if (success > 0) Ok(views.html.home("hi"))
//       }
//     }
//   }
// }

  // Endpoint for user logout
  def logout = Action { implicit request =>
    Ok(Json.toJson(true)).withNewSession
  }

  def home = Action { implicit request =>
        Ok(views.html.home("hi"))
    }
    def login = Action { implicit request =>
        Ok(views.html.login())
    }
    def signUp = Action { implicit request =>
        Ok(views.html.signUp())
    }
    def userprofile = Action { implicit request =>
      val usernameOption = request.session.get("username")
      usernameOption.map{ username =>
        Ok(views.html.profile(username))
      }.getOrElse(Redirect(routes.WorkoutController.login))
    }



    def myVideos = Action.async { implicit request =>
  withSessionUserid { userId =>
    model.getFavoriteWorkouts(userId).map { favoriteWorkouts =>
      if (favoriteWorkouts.isEmpty) {
        Ok(views.html.myVideos(Seq.empty, Seq.empty, Seq.empty)) // Adjust parameters as per your view's requirements
      } else {
        Ok(views.html.myVideos(
          favoriteWorkouts.map(_.name),
          favoriteWorkouts.map(_.videoUrl),
          favoriteWorkouts.map(_.workoutType) // Assuming workoutType is a descriptive field or similar
        ))
      }
    }
  }
}

    /*def video = Action { implicit request =>
        Ok(views.html.video())
    }*/

  



}
