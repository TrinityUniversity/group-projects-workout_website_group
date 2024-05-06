/*package controllers

import javax.inject._

import shared.SharedMessages
import play.api.mvc._
import play.api.i18n._

@Singleton
class WorkoutController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
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
        Ok(views.html.profile("mlewis"))
    }
    def search = Action {
        Ok(views.html.search(Seq("15 min STANDING ARM WORKOUT | With Dumbbells | Shoulders, Biceps and Triceps","20 Minute Full Body Cardio HIIT Workout [NO REPEAT]"), Seq("https://www.youtube.com/watch?v=d7j9p9JpLaE", "https://www.youtube.com/watch?v=M0uO8X3_tEA&t=1512s"),Seq("💪","🤾")))
    }
    def myVideos = Action {
        Ok(views.html.myVideos(Seq("15 min STANDING ARM WORKOUT | With Dumbbells | Shoulders, Biceps and Triceps","20 Minute Full Body Cardio HIIT Workout [NO REPEAT]"), Seq("https://www.youtube.com/watch?v=d7j9p9JpLaE", "https://www.youtube.com/watch?v=M0uO8X3_tEA&t=1512s"),Seq("💪","🤾")))
    }
    def video = Action {
        Ok(views.html.video())
    }


}*/