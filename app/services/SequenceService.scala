package services

import repositories.SequenceRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class SequenceService @Inject() (sequenceRepository: SequenceRepository) {
  def nextVal: Future[Option[Long]] = sequenceRepository.nextVal
}
