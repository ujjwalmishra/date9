package commons

import com.softwaremill.macwire.wire
import commons.repositories.{DateTimeProvider, DbConfigHelper}
import commons.services.{ActionRunner, InstantProvider}
import notifiers._
import play.api.db.slick.DatabaseConfigProvider
import commons.config.WithControllerComponents

trait CommonsComponents extends WithControllerComponents {
  lazy val actionRunner: ActionRunner = wire[ActionRunner]
  lazy val dbConfigHelper: DbConfigHelper = wire[DbConfigHelper]
  lazy val mailer: MailerComponent = wire[MailerComponent] 

  def databaseConfigProvider: DatabaseConfigProvider

  lazy val dateTimeProvider: DateTimeProvider = wire[InstantProvider]
}
