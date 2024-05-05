package controllers

import javax.inject._

import shared.SharedMessages
import play.api.mvc._
import play.api.i18n._

//added comment to test push to test if we can push from vscode with ssh repo
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
    Ok(views.html.search())
  }
  def myVideos = Action {
    Ok(views.html.myVideos())
  }
  def video = Action {
    Ok(views.html.video())
  }

}
