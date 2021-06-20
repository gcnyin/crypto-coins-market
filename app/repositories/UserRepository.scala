package repositories

import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.

  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of people
   */
  private class user(tag: Tag) extends Table[User](tag, "user") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    /** The name column, which is unique */
    def username = column[String]("username", O.Unique)

    def password = column[String]("password")

    def totalAmount = column[Int]("total_amount")

    def frozenAmount = column[Int]("frozen_amount")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Person object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Person case classes
     * apply and unapply methods.
     */
    def * = (id, username, password, totalAmount, frozenAmount) <> ((User.apply _).tupled, User.unapply)
  }

  /**
   * The starting point for all queries on the people table.
   */
  private val user = TableQuery[user]

  /**
   * Create a person with the given name and age.
   *
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that person.
   */
  def create(name: String, password: String): Future[User] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (user.map((p: user) => (p.username, p.password, p.totalAmount, p.frozenAmount))
    // Now define it to return the id, because we want to know what id was generated for the person
      returning user.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((info, id) => User(id, info._1, info._2, info._3, info._4))
    // And finally, insert the person into the database
    ) += (name, password, 0, 0)
  }

  /**
   * List all the people in the database.
   */
  def list(): Future[Seq[User]] = db.run {
    user.result
  }

  def findByName(name: String): Future[Option[User]] = db.run {
    user.filter(_.username === name).result.headOption
  }
}
