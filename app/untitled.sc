import scala.concurrent.{ExecutionContext, Future}

def getCache: Future[Option[String]] = ???

def getDb: Future[Option[String]] = ???

def main()(implicit ec: ExecutionContext): Future[Option[String]] =
  getCache.flatMap {
    case Some(value) => Future.successful(Some(value))
    case None => getDb
  }
