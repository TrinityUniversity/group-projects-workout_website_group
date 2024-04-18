package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._
import models.Tables._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt

@Singleton
class WorkoutController @Inject()(cc: ControllerComponents, dbConfigProvider: play.api.db.slick.DatabaseConfigProvider) extends AbstractController(cc) {
  val dbConfig = dbConfigProvider.get[slick.jdbc.JdbcProfile]

  import dbConfig._
  import profile.api._

  /*def load = Action { implicit request =>
    Ok(views.html.version5Main())
  }*/

  // JSON serialization for our case classes
   implicit val userDataRead = Json.reads[UserData]
  implicit val usersRowRead = Json.reads[UsersRow]
  implicit val usersRowWrite = Json.writes[UsersRow]
  implicit val workoutsRowReads = Json.reads[WorkoutsRow]

  def withJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson.map { body =>
      Json.fromJson[A](body) match {
        case JsSuccess(a, path) => f(a)
        case e @ JsError(_) => Future.successful(Redirect(routes.TaskList3.load))
      }
    }.getOrElse(Future.successful(Redirect(routes.TaskList3.load)))
  }

  def withSessionUsername(f: String => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    request.session.get("username").map(f).getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
  }

  def withSessionUserid(f: Int => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    request.session.get("userid").map(userid => f(userid.toInt)).getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
  }
   // API endpoint for user login
  def validate = Action.async { implicit request =>
    withJsonBody[UserData] { ud =>
      model.validateUser(ud.username, ud.password).map { ouserId =>
        ouserId match {
          case Some(userid) =>
            Ok(Json.toJson(true))
              .withSession("username" -> ud.username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.map(_.value).getOrElse(""))
          case None =>
            Ok(Json.toJson(false))
        }
      }
    }
  }

  // API endpoint for user registration
  def createUser = Action.async { implicit request =>
    withJsonBody[UserData] { ud => model.createUser(ud.username, ud.password).map { ouserId =>   
      ouserId match {
        case Some(userid) =>
          Ok(Json.toJson(true))
            .withSession("username" -> ud.username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.map(_.value).getOrElse(""))
        case None =>
          Ok(Json.toJson(false))
      }
    } }
  }

  def workoutList = Action.async { implicit request =>
      model.getTasks().map(workouts => Ok(Json.toJson(workouts)))
  }

 def logout = Action { implicit request =>
    Ok(Json.toJson(true)).withSession(request.session - "username")
  }
 /* def loginUser(username: String, password: String) = Action.async {
    val query = Users.filter(u => u.username === username && u.password === password).result.headOption
    db.run(query).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => Unauthorized("Invalid credentials")
    }
  }*/

  // API endpoint to get workouts based on user preferences
  /*def getWorkouts() = Action.async {
    val query = Workouts.filter(w => w.sweatLevel === sweatLevel && w.intensity === intensity && w.workLength === workLength).result
    db.run(query).map(workouts => Ok(Json.toJson(workouts)))
  }*/

  /*def getAllWorkouts(): Future[Seq[Workouts]] = {
    db.run(
      (for {
        workout <- Workouts 
      } yield {
        workout
      }).result
    ).map(workouts => workouts.map(workout => Workout(workout.itemId, workout.text)))
  }

  // Helper method to parse body as JSON and run a query
  private def processQuery[A: Reads](query: A => DBIO[Seq[WorkoutsRow]])(implicit request: Request[JsValue]): Future[Result] = {
    request.body.validate[A].fold(
      errors => Future.successful(BadRequest("Invalid input")),
      input => db.run(query(input)).map(result => Ok(Json.toJson(result)))
    )
  }*/

}

/*private val model = new TaskListDatabaseModel(db)

  def load = Action { implicit request =>
    Ok(views.html.version5Main())
  }

  implicit val userDataReads = Json.reads[UserData]
  implicit val taskItemWrites = Json.writes[TaskItem]
  implicit val newMessReads = Json.reads[NewMessage]
  implicit val newMessWrites = Json.writes[NewMessage]

  def withJsonBody[A](f: A => Future[Result])(implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
    request.body.asJson.map { body =>
      Json.fromJson[A](body) match {
        case JsSuccess(a, path) => f(a)
        case e @ JsError(_) => Future.successful(Redirect(routes.TaskList3.load))
      }
    }.getOrElse(Future.successful(Redirect(routes.TaskList3.load)))
  }

  def withSessionUsername(f: String => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    request.session.get("username").map(f).getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
  }

  def withSessionUserid(f: Int => Future[Result])(implicit request: Request[AnyContent]): Future[Result] = {
    request.session.get("userid").map(userid => f(userid.toInt)).getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
  }

  def validate = Action.async { implicit request =>
    withJsonBody[UserData] { ud =>
      model.validateUser(ud.username, ud.password).map { ouserId =>
        ouserId match {
          case Some(userid) =>
            Ok(Json.toJson(true))
              .withSession("username" -> ud.username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.map(_.value).getOrElse(""))
          case None =>
            Ok(Json.toJson(false))
        }
      }
    }
  }

  def createUser = Action.async { implicit request =>
    withJsonBody[UserData] { ud => model.createUser(ud.username, ud.password).map { ouserId =>   
      ouserId match {
        case Some(userid) =>
          Ok(Json.toJson(true))
            .withSession("username" -> ud.username, "userid" -> userid.toString, "csrfToken" -> play.filters.csrf.CSRF.getToken.map(_.value).getOrElse(""))
        case None =>
          Ok(Json.toJson(false))
      }
    } }
  }

  def taskList = Action.async { implicit request =>
    withSessionUsername { username =>
      println("!!! Getting tasks")
      model.getTasks(username).map(tasks => Ok(Json.toJson(tasks)))
    }
  }

  def generalTask = Action.async { implicit request =>
    withSessionUsername { username =>
      println("!!! Getting general tasks")
      model.getTasks("general").map(tasks => Ok(Json.toJson(tasks)))
    }
  }

  def addTask = Action.async { implicit request =>
    withSessionUserid { userid =>
        withSessionUsername { username => 
            withJsonBody[NewMessage] { data =>
                if(data.to == "general") {
                    val mess1 = "General Message from " + username + ": " + data.message;
                    val generalId = (model.getUserId("general"))
                    generalId.flatMap(ids => model.addTask(ids.head, mess1).map(count => Ok(Json.toJson(count > 0))))
                } else if (username == data.to) {
                    val mess2 = "Direct Message To/From " + data.to + ": " + data.message;
                    model.addTask(userid, mess2).map(count => Ok(Json.toJson(count > 0)));
                } else {
                    val mess3 = "Direct Message From " + username + ": " + data.message;
                    val mess4 = "Direct Message To " + data.to + ": " + data.message;
                    val recipientId = (model.getUserId(data.to))
                    val f1 = recipientId.flatMap(ids => model.addTask(ids.head, mess3).map(count => count ))
                    val f2 = model.addTask(userid, mess4).map(count => count)
                    Future.sequence(Seq(f1,f2)).map(counts => Ok(Json.toJson(counts.sum > 1)))
                }
            }
        }
    }
  }

  def logout = Action { implicit request =>
    Ok(Json.toJson(true)).withSession(request.session - "username")
  }
}*/