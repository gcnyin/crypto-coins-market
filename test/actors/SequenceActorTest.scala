package actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar.mock
import services.SequenceService

import scala.concurrent.Future

class SequenceActorTest
    extends TestKit(ActorSystem("MySpec"))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {
  "A SequenceActor" must {
    "return 1 when first call " in {
      import SequenceActor._

      val sequenceService = mock[SequenceService]
      when(sequenceService.nextStart).thenReturn(Future.successful(NextStart(1)))
      val sequenceActorRef = system.actorOf(props(sequenceService))
      sequenceActorRef ! NextValue
      expectMsg(NextValueResult(1))
      sequenceActorRef ! NextValue
      expectMsg(NextValueResult(2))
    }
  }
}
