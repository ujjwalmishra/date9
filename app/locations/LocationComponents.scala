package locations

import com.softwaremill.macwire.wire
import commons.config.{WithControllerComponents, WithExecutionContextComponents}
import commons.models.Username
import authentication.AuthenticationComponents
import play.api.routing.Router
import play.api.routing.sird._
import users.controllers.{LoginController, ProfileController, UserController}
import users.repositories.{FollowAssociationRepo, ProfileRepo, UserRepo}
import users.services._

trait LocationComponents extends WithControllerComponents with WithExecutionContextComponents {

  private lazy val defaultRadius = 100L

  lazy val userLocationController: UserLocationController = wire[UserLocationController]
  lazy val userLocationService: UserLocationService = wire[UserLocationService]
  lazy val userLocationRepo: UserLocationRepo = wire[UserLocationRepo]

  lazy val locationController: LocationController = wire[LocationController]
  lazy val locationService: LocationService = wire[LocationService]
  lazy val locationRepo: LocationRepo = wire[LocationRepo]

  lazy val userLocationValidator: UserLocationValidator = wire[UserLocationValidator]

  lazy val locationValidator: LocationValidator =  wire[LocationValidator]


  val locationRoutes: Router.Routes = {
    case POST(p"/location/update") =>
      userLocationController.update
    case GET(p"/locations" ? q_o"radius={long(maybeRadius)}") =>
      val radius = maybeRadius.getOrElse(defaultRadius)
      locationController.getHotLocations(radius)

  }
}