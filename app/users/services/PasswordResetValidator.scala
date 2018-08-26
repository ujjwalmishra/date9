package users.services

import commons.validations.PropertyViolation
import commons.models.{Email}
import users.services.EmailValidator
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class PasswordResetValidator(emailValidator: EmailValidator,
                          implicit private val ec: ExecutionContext) {

  def validate(email: Email): DBIO[Seq[PropertyViolation]] = {
    for {
      usernameEmail <- validateEmail(email)
    } yield usernameEmail
  }

  private def validateEmail(email: Email) = {
    emailValidator.validate(email)
      .map(violations => violations.map(violation => PropertyViolation("email", violation)))
  }

}
