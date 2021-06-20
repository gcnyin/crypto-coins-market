package actors

import akka.actor.{Actor, ActorRef, ActorSystem, Cancellable, Props}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object CounterSocketActor {
  def props(out: ActorRef, username: String)(implicit
      executionContext: ExecutionContext,
      actorSystem: ActorSystem
  ): Props = Props(new CounterSocketActor(out, username))
}

class CounterSocketActor(out: ActorRef, username: String)(implicit
    executionContext: ExecutionContext,
    actorSystem: ActorSystem
) extends Actor {
  private val scheduleTask: Cancellable =
    actorSystem.scheduler.scheduleAtFixedRate(initialDelay = 5.seconds, interval = 10.seconds) { () =>
      out ! Json.obj("message" -> "ping").toString()
    }

  def receive: Receive = { case msg: String =>
    out ! Json.obj("message" -> s"$username message received", "data" -> msg).toString()
  }

  override def postStop(): Unit = {
    scheduleTask.cancel()
    out ! Json.obj("message" -> "disconnected").toString()
  }
}
