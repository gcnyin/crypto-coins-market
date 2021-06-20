package models

import play.api.libs.json.{Json, Writes}

case class User(id: Int, username: String, password: String, totalAmount: Int, frozenAmount: Int)

object User {
  implicit val writer: Writes[User] = Json.writes[User]
}
