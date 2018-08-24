package authentication.pac4j.services

import authentication.api._
import authentication.exceptions.InvalidPasswordException
import authentication.models._
import authentication.repositories.SecurityUserRepo
import commons.services.ActionRunner
import org.mindrot.jbcrypt.BCrypt
import play.api.mvc.Request
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

private[authentication] class PasswordReset(tokenGenerator: TokenGenerator[SecurityUserIdProfile, JwtToken],
                                                               actionRunner: ActionRunner,
                                                               securityUserRepo: SecurityUserRepo)
                                                              (implicit private val ec: ExecutionContext)
  extends PasswordResetter[PasswordResetWrapper] {

  override def reset(request: Request[PasswordResetWrapper]): DBIO[String] = {
    require(request != null)

    val (email) = request.body.email

    securityUserRepo.findByEmail(email)
      .map(user => {
        tokenGenerator.generate(SecurityUserIdProfile(user.id)).token
      })
  }

}