package requests

import play.api.libs.json.{Json, Reads}

case class CreateUserRequest(username: String, password: String)

object CreateUserRequest {
  implicit val reads: Reads[CreateUserRequest] = Json.reads[CreateUserRequest]
}
