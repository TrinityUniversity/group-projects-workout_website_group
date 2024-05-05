package controllers

import javax.inject._

import shared.SharedMessages
import play.api.mvc._
import play.api.i18n._
import play.api.libs.json._
import play.api.libs.functional.syntax._

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
class WorkoutPage @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {
  def home = Action {
    Ok(views.html.home())
  }
  def login = Action {
    Ok(views.html.login())
  }
  def signUp = Action {
    Ok(views.html.signUp())
  }
  def profile = Action {
    Ok(views.html.profile())
  }
  def search = Action {
    // form();
    Ok(views.html.search())
  }
  def myVideos = Action {
    Ok(views.html.myVideos())
  }
  def video = Action {
    Ok(views.html.video())
  }

  def form(): Action[JsValue] = Action(parse.json) { implicit request =>
    println("within the form");
    // Parse the JSON body into WorkoutPayload case class
    val payloadResult = request.body.validate[WorkoutPayload]

    payloadResult.fold(
      // If JSON validation fails, return a BadRequest response
      errors => {
        BadRequest(Json.obj("message" -> "Invalid payload format"))
      },
      // If JSON validation succeeds, handle the payload
      payload => {
        // Here you would insert the payload data into the database
        // For demonstration purposes, let's just print the payload to console
        println("Received payload:", payload)

        // Return a success response
        Ok(Json.obj("message" -> "Payload received successfully"))
      }
    )

  }

}
