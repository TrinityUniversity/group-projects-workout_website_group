package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.Tables._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class WorkoutController @Inject()(cc: ControllerComponents, dbConfigProvider: play.api.db.slick.DatabaseConfigProvider) extends AbstractController(cc) {
  val dbConfig = dbConfigProvider.get[slick.jdbc.JdbcProfile]

  import dbConfig._
  import profile.api._

  // JSON serialization for our case classes
  implicit val usersRowFormat = Json.format[UsersRow]
  implicit val workoutsRowFormat = Json.format[WorkoutsRow]

  // API endpoint for user registration
  def registerUser = Action.async(parse.json) { request =>
    request.body.validate[UsersRow].fold(
      errors => Future.successful(BadRequest("Invalid User Data")),
      user => db.run(Users += user).map(_ => Ok("User registered successfully"))
    )
  }


  /*def createUser(username: String, password: String): Future[Option[Int]] = {
    val matches = db.run(Users.filter(userRow => userRow.username === username).result)
    matches.flatMap { userRows =>
      if (userRows.isEmpty) {
        db.run(Users += UsersRow(-1, username, BCrypt.hashpw(password, BCrypt.gensalt())))
          .flatMap { addCount => 
            if (addCount > 0) db.run(Users.filter(userRow => userRow.username === username).result)
              .map(_.headOption.map(_.id))
            else Future.successful(None)
          }
      } else Future.successful(None)
    }
  }*/

  // API endpoint for user login
  def loginUser(username: String, password: String) = Action.async {
    val query = Users.filter(u => u.username === username && u.password === password).result.headOption
    db.run(query).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => Unauthorized("Invalid credentials")
    }
  }

  // API endpoint to get workouts based on user preferences
  def getWorkouts(sweatLevel: Int, intensity: Int, workLength: Int) = Action.async {
    val query = Workouts.filter(w => w.sweatLevel === sweatLevel && w.intensity === intensity && w.workLength === workLength).result
    db.run(query).map(workouts => Ok(Json.toJson(workouts)))
  }

  // Helper method to parse body as JSON and run a query
  private def processQuery[A: Reads](query: A => DBIO[Seq[WorkoutsRow]])(implicit request: Request[JsValue]): Future[Result] = {
    request.body.validate[A].fold(
      errors => Future.successful(BadRequest("Invalid input")),
      input => db.run(query(input)).map(result => Ok(Json.toJson(result)))
    )
  }
}
