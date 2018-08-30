package commons

import com.softwaremill.macwire.wire
import commons.repositories.{DateTimeProvider, DbConfigHelper}
import commons.services.{ActionRunner, InstantProvider}
import notifiers._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.mailer._
import commons.config.WithControllerComponents

trait CommonsComponents extends WithControllerComponents with MailerComponents{
  lazy val actionRunner: ActionRunner = wire[ActionRunner]
  lazy val dbConfigHelper: DbConfigHelper = wire[DbConfigHelper]
  lazy val mailer: MailerComponent = new MailerComponent(mailerClient)

  def databaseConfigProvider: DatabaseConfigProvider

  lazy val dateTimeProvider: DateTimeProvider = wire[InstantProvider]
}
