package authentication.pac4j.services

import authentication.api._
import authentication.exceptions.InvalidPasswordException
import authentication.models._
import authentication.repositories.SecurityUserRepo
import commons.services.ActionRunner
import commons.utils.DbioUtils
import users.services.PasswordResetValidator
import commons.exceptions.ValidationException
import commons.models.Email
import notifiers._
import org.mindrot.jbcrypt.BCrypt
import play.api.mvc.Request
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

private[authentication] class PasswordReset(tokenGenerator: TokenGenerator[SecurityUserIdProfile, JwtToken],
                                                               actionRunner: ActionRunner,
                                                               passwordResetValidator: PasswordResetValidator,
                                                               securityUserRepo: SecurityUserRepo,
                                                               mailer: MailerComponent)
                                                              (implicit private val ec: ExecutionContext)
  extends PasswordResetter {

  override def reset(email: Email): DBIO[String] = {
      validate(email)
      securityUserRepo.findByEmail(email)
      .map(user => generateToken(user))
      .map(token => sendEmail(token, email))
  }

  private def validate(email: Email) = {
    passwordResetValidator.validate(email)
      .flatMap(violations => DbioUtils.fail(violations.isEmpty, new ValidationException(violations)))
  }

//   private def doRegister(userRegistration: UserRegistration) = {
//     val newSecurityUser = NewSecurityUser(userRegistration.email, userRegistration.password)
//     for {
//       securityUser <- securityUserCreator.create(newSecurityUser)
//       now = dateTimeProvider.now
//       user = User(UserId(-1), userRegistration.username, userRegistration.email, null, defaultImage, now, now)
//       savedUser <- userRepo.insertAndGet(user)
//     } yield (savedUser, securityUser.id)
//   }

  private def generateToken(user: SecurityUser) = {
    tokenGenerator.generate(SecurityUserIdProfile(user.id)).token
  }

  private def sendEmail(token: String, email: Email ): String = {
      println(s"Sending email with token $token to email $email")
      mailer.sendEmail
      return "Done"
  }  

}