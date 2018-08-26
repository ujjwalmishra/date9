package authentication.api

import play.api.mvc.Request
import commons.models.{Email}
import slick.dbio.DBIO

trait PasswordResetter {
  def reset(email: Email): DBIO[String]
}