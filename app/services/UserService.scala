package services

import models.{Token, User}
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import play.api.Configuration
import repositories.UserRepository
import requests.{CreateTokenRequest, CreateUserRequest}
import io.jsonwebtoken.{Claims, Header, Jws, Jwt, JwtException, Jwts, SignatureAlgorithm}

import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.{Base64, Date}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject() (userRepository: UserRepository, conf: Configuration)(implicit ec: ExecutionContext) {
  private val passwordEncoder = new BCryptPasswordEncoder()

  private val key = Base64.getEncoder
    .encodeToString(conf.get[String]("jwt.key").getBytes(StandardCharsets.UTF_8))

  def createUser(request: CreateUserRequest): Future[User] =
    userRepository.create(request.username, passwordEncoder.encode(request.password))

  def findByName(name: String): Future[Option[User]] = userRepository.findByName(name)

  def validatePassword(src: String, tar: String): Boolean = passwordEncoder.matches(src, tar)

  def createToken(request: CreateTokenRequest): Future[Option[Token]] = {
    val expiration = Date.from(Instant.now().plus(7, ChronoUnit.DAYS))
    findByName(request.username)
      .map {
        _.filter(user => validatePassword(request.password, user.password))
          .map(user =>
            Token(
              Jwts.builder
                .setSubject(user.username)
                .signWith(SignatureAlgorithm.HS256, key)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .compact
            )
          )
      }
  }

  def extractUsernameFromJws(token: String) = {
    validateJwt(token).map { jws =>
      jws.getBody.getSubject
    }
  }

  def validateJwt(token: String): Option[Jws[Claims]] = {
    try {
      Option(Jwts.parser().setSigningKey(key).parseClaimsJws(token))
    } catch {
      case _: JwtException => Option.empty
    }
  }
}
