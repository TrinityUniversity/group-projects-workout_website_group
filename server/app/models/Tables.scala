package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends Tables {
  val profile = slick.jdbc.PostgresProfile
}

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Users.schema ++ Workouts.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Users
   *  @param userId Database column user_id SqlType(serial), AutoInc, PrimaryKey
   *  @param username Database column username SqlType(varchar), Length(20,true)
   *  @param favorites Database column favorites SqlType(_int4), Length(10,false), Default(None)
   *  @param password Database column password SqlType(varchar), Length(200,true) */
  case class UsersRow(userId: Int, username: String, favorites: Option[String] = None, password: String)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[Int], <<[String], <<?[String], <<[String]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, "users") {
    def * = (userId, username, favorites, password).<>(UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(userId), Rep.Some(username), favorites, Rep.Some(password))).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(serial), AutoInc, PrimaryKey */
    val userId: Rep[Int] = column[Int]("user_id", O.AutoInc, O.PrimaryKey)
    /** Database column username SqlType(varchar), Length(20,true) */
    val username: Rep[String] = column[String]("username", O.Length(20,varying=true))
    /** Database column favorites SqlType(_int4), Length(10,false), Default(None) */
    val favorites: Rep[Option[String]] = column[Option[String]]("favorites", O.Length(10,varying=false), O.Default(None))
    /** Database column password SqlType(varchar), Length(200,true) */
    val password: Rep[String] = column[String]("password", O.Length(200,varying=true))
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))

  /** Entity class storing rows of table Workouts
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param videoUrl Database column video_url SqlType(varchar), Length(255,true)
   *  @param sweatLevel Database column sweat_level SqlType(int4)
   *  @param intensity Database column intensity SqlType(int4)
   *  @param workLength Database column work_length SqlType(int4)
   *  @param workoutType Database column workout_type SqlType(varchar)
   *  @param likes Database column likes SqlType(int4)
   *  @param views Database column views SqlType(int4) */
  case class WorkoutsRow(id: Int, name: String, videoUrl: String, sweatLevel: Int, intensity: Int, workLength: Int, workoutType: String, likes: Int, views: Int)
  /** GetResult implicit for fetching WorkoutsRow objects using plain SQL queries */
  implicit def GetResultWorkoutsRow(implicit e0: GR[Int], e1: GR[String]): GR[WorkoutsRow] = GR{
    prs => import prs._
    WorkoutsRow.tupled((<<[Int], <<[String], <<[String], <<[Int], <<[Int], <<[Int], <<[String], <<[Int], <<[Int]))
  }
  /** Table description of table workouts. Objects of this class serve as prototypes for rows in queries. */
  class Workouts(_tableTag: Tag) extends profile.api.Table[WorkoutsRow](_tableTag, "workouts") {
    def * = (id, name, videoUrl, sweatLevel, intensity, workLength, workoutType, likes, views).<>(WorkoutsRow.tupled, WorkoutsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name), Rep.Some(videoUrl), Rep.Some(sweatLevel), Rep.Some(intensity), Rep.Some(workLength), Rep.Some(workoutType), Rep.Some(likes), Rep.Some(views))).shaped.<>({r=>import r._; _1.map(_=> WorkoutsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column video_url SqlType(varchar), Length(255,true) */
    val videoUrl: Rep[String] = column[String]("video_url", O.Length(255,varying=true))
    /** Database column sweat_level SqlType(int4) */
    val sweatLevel: Rep[Int] = column[Int]("sweat_level")
    /** Database column intensity SqlType(int4) */
    val intensity: Rep[Int] = column[Int]("intensity")
    /** Database column work_length SqlType(int4) */
    val workLength: Rep[Int] = column[Int]("work_length")
    /** Database column workout_type SqlType(varchar) */
    val workoutType: Rep[String] = column[String]("workout_type")
    /** Database column likes SqlType(int4) */
    val likes: Rep[Int] = column[Int]("likes")
    /** Database column views SqlType(int4) */
    val views: Rep[Int] = column[Int]("views")
  }
  /** Collection-like TableQuery object for table Workouts */
  lazy val Workouts = new TableQuery(tag => new Workouts(tag))
}
