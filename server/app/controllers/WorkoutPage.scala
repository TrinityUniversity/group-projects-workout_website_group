package controllers

import javax.inject._

import shared.SharedMessages
import play.api.mvc._
import play.api.i18n._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.Tables._ 
import models._
import scala.concurrent.Future
import models.ReadsAndWrites._
import scala.concurrent.ExecutionContext
import javax.inject._
import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext





case class WorkoutPayload(
    Intensity: String,
    Sweat: String,
    Length: String,
    WorkoutType: String
)

object WorkoutPayload {
  // Define a Reads instance for WorkoutPayload
  implicit val workoutPayloadReads: Reads[WorkoutPayload] = (
    (JsPath \ "Intensity").read[String] and
      (JsPath \ "Sweat").read[String] and
      (JsPath \ "Length").read[String] and
      (JsPath \ "WorkoutType").read[String]
  )(WorkoutPayload.apply _)
}

@Singleton
class WorkoutPage @Inject()(cc: ControllerComponents, dbConfigProvider: play.api.db.slick.DatabaseConfigProvider)(implicit ec: ExecutionContext) extends AbstractController(cc) {
    val dbConfig = dbConfigProvider.get[slick.jdbc.JdbcProfile]
  import dbConfig._
  import models._
  import models.ReadsAndWrites._
  import play.api.libs.json._

  private val model = new WorkoutDatabaseModel(db)


    // Action to display and search workouts
 def search = Action.async { implicit request =>
    val searchQuery = request.getQueryString("searchInput").map(_.toLowerCase)
    model.getWorkouts.map { workouts =>
      val filteredWorkouts = searchQuery match {
        case Some(query) => workouts.filter(_.name.toLowerCase.contains(query))
        case None => workouts
      }
      Ok(views.html.search(filteredWorkouts.map(_.name), filteredWorkouts.map(_.videoUrl)))
    }
  }


    println("within the form")

def form(): Action[JsValue] = Action.async(parse.json) { implicit request =>
  request.body.validate[WorkoutPayload] match {
    
    case JsSuccess(payload, _) =>
      println("Received payload:", payload)
      model.getWorkouts().map { allWorkouts =>
        if (allWorkouts.isEmpty) {
          Ok(Json.obj("message" -> "No workouts available"))
        } else {
          val bestWorkout = findMatch(allWorkouts, payload)
          Ok(Json.obj(
            "message" -> "Payload received and processed successfully",
            "bestWorkout" -> Json.toJson(bestWorkout)  
          ))
        }
      }
    case JsError(errors) =>
      Future.successful(BadRequest(Json.obj("message" -> "Invalid payload format", "errors" -> JsError.toJson(errors))))
  }
}





  def findMatch(allWorkouts: Seq[WorkoutsRow], formInputs: WorkoutPayload): WorkoutsRow = {
    var bestWorkout = allWorkouts.head
    var bestScore = score(bestWorkout, formInputs)
    for (workout <- allWorkouts) {
      val score_i = score(workout, formInputs)
      if (score_i > bestScore) {
        bestScore = score_i
        bestWorkout = workout
      }
    }
    bestWorkout
  }


def score(workout: WorkoutsRow, input: WorkoutPayload): Int = {
    var x = 0
    if(workout.sweatLevel == input.Sweat.toInt) x = x + 1
    if(workout.intensity == input.Intensity.toInt) x = x + 1
    if(workout.workLength == input.Length.toInt) x = x + 1
    if(workout.workoutType == input.WorkoutType.toInt) x = x + 1
    return x
}


}
