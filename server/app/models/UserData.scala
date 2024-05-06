package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class UserData(username: String, favorites: Option[List[Int]] = None, password: String)
case class WorkoutData(name: String, videoUrl: String, sweatLevel: Int, intensity: Int, workLength: Int, workoutType: String, likes: Int, views: Int)

object ReadsAndWrites {
  // Existing serializers
  implicit val userDataFormat: Format[UserData] = (
  (__ \ "username").format[String] and
  (__ \ "favorites").formatNullable[List[Int]] and
  (__ \ "password").format[String]
  )(UserData.apply, unlift(UserData.unapply))

  implicit val workoutDataFormat: Format[WorkoutData] = Json.format[WorkoutData]

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
