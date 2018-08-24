package users.services

import commons.validations.PropertyViolation
import authentication.models.{PasswordResetWrapper}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class PasswordResetValidator(emailValidator: EmailValidator,
                          implicit private val ec: ExecutionContext) {

  def validate(passwordReset: PasswordResetWrapper): DBIO[Seq[PropertyViolation]] = {
    for {
      usernameEmail <- validateEmail(passwordReset)
    } yield usernameEmail
  }

  private def validateEmail(passwordReset: PasswordResetWrapper) = {
    emailValidator.validate(passwordReset.email)
      .map(violations => violations.map(violation => PropertyViolation("email", violation)))
  }

}
