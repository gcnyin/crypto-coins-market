package actors

import akka.actor.{Actor, ActorRef}
import services.SequenceService

import java.util
import scala.concurrent.ExecutionContextExecutor

object SequenceActor {
  object NextValue

  case class NewNextValue(v: Long)
}

class SequenceActor(sequenceService: SequenceService) extends Actor {

  import SequenceActor._

  private implicit val ec: ExecutionContextExecutor = context.dispatcher

  private val step = 10000L

  private var start = -1L
  private var end = 0L
  private var current = 0L
  private var fetchingFlag = false
  private val waitingList = new util.LinkedList[ActorRef]()

  def receive: Receive = {
    case NewNextValue(newNextVal) =>
      start = newNextVal * step
      end = newNextVal * (step + 1)
      current = start
      waitingList.forEach { it =>
        it ! current
        current += 1
      }
      waitingList.clear()
      fetchingFlag = false
    case NextValue =>
      if (current == (end - 1)) {
        if (!fetchingFlag) {
          fetchingFlag = true
          sequenceService.nextVal.onComplete { it =>
            it.map(nextVal => NewNextValue(nextVal))
              .map(self ! _)
          }
        } else {
          waitingList.add(sender())
        }
      }
      sender ! current
      current += 1
  }
}
