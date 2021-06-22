package actors

import akka.actor.Actor
import akka.pattern.pipe
import services.SequenceService

import scala.concurrent.ExecutionContextExecutor

object SequenceActor {
  object NextValue
}

class SequenceActor(sequenceService: SequenceService) extends Actor {

  import SequenceActor._

  private implicit val ec: ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = { case NextValue =>
    sequenceService.nextVal
      .pipeTo(sender())
  }
}
