package authentication.api

import play.api.mvc.Request
import slick.dbio.DBIO

trait PasswordResetter[RequestBodyType] {
  def reset(request: Request[RequestBodyType]): DBIO[String]
}