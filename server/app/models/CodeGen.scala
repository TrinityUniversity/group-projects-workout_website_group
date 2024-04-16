package models

object CodeGen extends App {
  slick.codegen.SourceCodeGenerator.run(
    "slick.jdbc.PostgresProfile", 
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/workoutapp?user=tgreen&password=password",
    "/Users/wg1911/Desktop/Comp Sci/Web Apps/group-projects-workout_website_group/server/app", 
    "models", None, None, true, false
  )
}


