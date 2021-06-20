package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, BaseController, ControllerComponents}
import requests.CreateUserRequest
import services.UserService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject() (val controllerComponents: ControllerComponents, userService: UserService)(implicit
    ec: ExecutionContext
) extends BaseController {
  def createUser: Action[CreateUserRequest] = Action.async(parse.json[CreateUserRequest]) { request =>
    userService
      .createUser(request.body)
      .map(u => Ok(Json.toJson(u)))
  }
}
