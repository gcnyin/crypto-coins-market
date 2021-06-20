package controllers

import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, BaseController, ControllerComponents}
import requests.CreateTokenRequest
import services.UserService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TokenController @Inject() (val controllerComponents: ControllerComponents, userService: UserService)(implicit
    ec: ExecutionContext
) extends BaseController {
  val invalidMessage: JsObject = Json.obj("message" -> "invalid username or password")

  def createToken: Action[CreateTokenRequest] = Action.async(parse.json[CreateTokenRequest]) { request =>
    userService
      .createToken(request.body)
      .map(_.fold(Unauthorized(invalidMessage))(token => Ok(Json.toJson(token))))
  }
}
