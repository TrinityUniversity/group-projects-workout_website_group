package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class UserData(username: String, favorites: Option[String] = None, password: String)
case class WorkoutData(name: String, videoUrl: String, sweatLevel: Int, intensity: Int, workLength: Int, workoutType: String, likes: Int, views: Int)

object ReadsAndWrites {
  // Existing serializers
  implicit val userDataFormat: Format[UserData] = Json.format[UserData]
  implicit val workoutDataFormat: Format[WorkoutData] = Json.format[WorkoutData]

  // Serializer for WorkoutsRow
  implicit val workoutsRowWrites: Writes[Tables.WorkoutsRow] = (
    (__ \ "id").write[Int] and
    (__ \ "name").write[String] and
    (__ \ "videoUrl").write[String] and
    (__ \ "sweatLevel").write[Int] and
    (__ \ "intensity").write[Int] and
    (__ \ "workLength").write[Int] and
    (__ \ "workoutType").write[String] and
    (__ \ "likes").write[Int] and
    (__ \ "views").write[Int]
  )(unlift(Tables.WorkoutsRow.unapply))
}
