package controllers

import actors.CounterSocketActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.Logging
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import services.UserService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WsController @Inject() (cc: ControllerComponents, userService: UserService)(implicit
    system: ActorSystem,
    mat: Materializer,
    ec: ExecutionContext
) extends AbstractController(cc)
    with Logging {

  def socket: WebSocket = WebSocket.acceptOrResult[String, String] { request =>
    val handler = request
      .getQueryString("token")
      .flatMap(userService.extractUsernameFromJws) match {
      case Some(username) =>
        Right(ActorFlow.actorRef { out =>
          CounterSocketActor.props(out, username)
        })
      case None =>
        logger.info("Unauthorized")
        Left(Unauthorized)
    }
    Future.successful(handler)
  }
}
