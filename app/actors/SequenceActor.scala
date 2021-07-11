package actors

import actors.SequenceActor.NextValue
import akka.actor.{Actor, Props}
import akka.pattern.pipe
import services.SequenceService

import scala.concurrent.ExecutionContextExecutor

object SequenceActor {
  def props(sequenceService: SequenceService): Props = Props(new SequenceActor(sequenceService))

  object NextValue
}

/**
 * Caller需要传递NextValue消息来获取下一个ID，生成的ID单调递增。
 *
 * @param sequenceService Sequence服务，单调递增地返回ID
 */
class SequenceActor(sequenceService: SequenceService) extends Actor {
  private implicit val ec: ExecutionContextExecutor = context.dispatcher

  /**
   * sequenceService.nextStart: () => NextStart
   *
   * @return 返回一个单调递增的ID
   */
  def receive: Receive = { case NextValue =>
    sequenceService.nextStart.pipeTo(sender)
  }
}
