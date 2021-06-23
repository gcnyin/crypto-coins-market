package actors

import akka.actor.Actor
import services.SequenceService

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object SequenceActor {
  object NextValue
}

class SequenceActor(sequenceService: SequenceService) extends Actor {

  import SequenceActor._

  private val step = 10000L

  private val start = -1L
  private val end = 0L
  private val current = 0L

  def receive: Receive = onMessage(start, end, current)

  private def onMessage(start: Long, end: Long, current: Long): Receive = { case NextValue =>
    if (start == -1) {
      val nextVal = Await.result(sequenceService.nextVal, 500.millis)
      context.become(onMessage(0, step * nextVal, current))
    }
    if (current == (end - 1)) {
      val nextVal = Await.result(sequenceService.nextVal, 500.millis)
      val newStart = (nextVal - 1) * step
      context.become(onMessage(newStart, nextVal * step, newStart))
    }
    sender ! current
    context.become(onMessage(start, end, current + 1))
  }
}
