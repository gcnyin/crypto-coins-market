package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.pipe
import services.SequenceService

import java.util
import scala.concurrent.ExecutionContextExecutor
import scala.util.control.Breaks
import scala.util.control.Breaks.break

object SequenceActor {
  def props(sequenceService: SequenceService): Props = Props(new SequenceActor(sequenceService))

  object Initialize

  case class NextStart(start: Long)

  object NextValue

  case class NextValueResult(v: Long)

  object ProcessWaitingList
}

/**
 * Caller需要传递NextValue消息来获取下一个ID，生成的ID单调递增。
 *
 * @param sequenceService Sequence服务，单调递增地返回ID
 */
class SequenceActor(sequenceService: SequenceService) extends Actor {

  import SequenceActor._

  private implicit val ec: ExecutionContextExecutor = context.dispatcher

  private val step = 1000L

  private var start = 0L
  private var end = 0L
  private var current = 0L
  private val waitingList = new util.LinkedList[ActorRef]
  private var newSequenceIsBeingGenerated = false

  /**
   * sequenceService.nextStart: () => NextStart
   *
   * @return 返回一个单调递增的ID
   */
  def receive: Receive = {
    case Initialize => sequenceService.nextStart.pipeTo(sender())
    case NextStart(start) =>
      this.start = start
      end = start + step
      current = start
      newSequenceIsBeingGenerated = false
      self ! ProcessWaitingList
    case NextValue =>
      if (current == end) {
        waitingList add sender
        if (!newSequenceIsBeingGenerated) {
          newSequenceIsBeingGenerated = true
          sequenceService.nextStart.pipeTo(self)
        }
      } else {
        sender ! NextValueResult(current)
        current += 1
      }
    case ProcessWaitingList =>
      val iter = waitingList.iterator()
      val loop = new Breaks;
      loop.breakable {
        while (iter.hasNext) {
          val ref = iter.next()
          ref ! NextValueResult(current)
          current += 1
          if (current == end) {
            sequenceService.nextStart.pipeTo(self)
            break
          }
        }
      }
  }
}
