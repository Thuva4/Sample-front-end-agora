package controllers

import play.api.mvc.{Action, Controller}

/**
  * A very small controller that renders a home page.
  */
class HomeController extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def vote = Action { implicit request=>
    Ok(views.html.vote())
  }

  def login = Action{ implicit request =>
    Ok(views.html.login())
  }
}
