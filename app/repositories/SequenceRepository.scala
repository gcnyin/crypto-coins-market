package repositories

import actors.SequenceActor.NextStart
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SequenceRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def nextVal: Future[NextStart] = db
    .run {
      sql"select nextval('default_sequence')".as[Long]
    }
    .map(it => NextStart(it.head))
}
