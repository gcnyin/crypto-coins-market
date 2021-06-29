package services

import actors.SequenceActor.NextStart
import repositories.SequenceRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class SequenceService @Inject() (sequenceRepository: SequenceRepository) {
  def nextStart: Future[NextStart] = sequenceRepository.nextVal
}
