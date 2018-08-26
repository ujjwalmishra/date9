package authentication.models

import play.api.libs.json.{Format, Json}
import commons.models.Email;

case class PasswordResetWrapper(email: Email)

object PasswordResetWrapper {

  implicit val PasswordResetWrapper: Format[PasswordResetWrapper] = Json.format[PasswordResetWrapper]

}