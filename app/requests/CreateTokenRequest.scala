package requests

import play.api.libs.json.{Json, Reads}

case class CreateTokenRequest(username: String, password: String)

object CreateTokenRequest {
  implicit val reads: Reads[CreateTokenRequest] = Json.reads[CreateTokenRequest]
}
