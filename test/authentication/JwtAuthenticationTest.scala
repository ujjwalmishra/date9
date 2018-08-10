package authentication

import com.softwaremill.macwire.wire
import authentication.api.AuthenticatedActionBuilder
import authentication.exceptions.MissingOrInvalidCredentialsCode
import authentication.models.{AuthenticatedUser, HttpExceptionResponse}
import users.test_helpers.{UserRegistrationTestHelper, UserRegistrations}
import play.api.http.HeaderNames
import play.api.libs.json._
import play.api.libs.ws.WSResponse
import play.api.mvc._
import play.api.routing.Router
import play.api.routing.sird._
import testhelpers.{ProgrammaticDateTimeProvider, RealWorldWithServerBaseTest}

import scala.concurrent.ExecutionContext

class JwtAuthenticationTest extends RealWorldWithServerBaseTest {

  val fakeApiPath: String = "test"

  val accessTokenJsonAttrName: String = "access_token"

  val programmaticDateTimeProvider = new ProgrammaticDateTimeProvider

  def userRegistrationTestHelper(implicit testComponents: AppWithTestComponents): UserRegistrationTestHelper =
    testComponents.userRegistrationTestHelper

  "Authentication" should "allow everyone to public API" in {
    // when
    val response: WSResponse = await(wsUrl(s"/$fakeApiPath/public").get())

    // then
    response.status.mustBe(OK)
  }

  it should "block request without jwt token" in {
    // when
    val response: WSResponse = await(wsUrl(s"/$fakeApiPath/authenticationRequired").get())

    // then
    response.status.mustBe(UNAUTHORIZED)
    response.json.as[HttpExceptionResponse].code.mustBe(MissingOrInvalidCredentialsCode)
  }

  it should "block request with invalid jwt token" in {
    // given
    val token = "invalidJwtToken"

    // when
    val response: WSResponse = await(wsUrl(s"/$fakeApiPath/authenticationRequired")
      .addHttpHeaders(HeaderNames.AUTHORIZATION -> s"Token $token")
      .get())

    // then
    response.status.mustBe(UNAUTHORIZED)
    response.json.as[HttpExceptionResponse].code.mustBe(MissingOrInvalidCredentialsCode)
  }

  it should "allow authenticated user to secured API" in {
    // given
    val registration = UserRegistrations.petycjaRegistration
    val user = userRegistrationTestHelper.register(registration)
    val tokenResponse = userRegistrationTestHelper.getToken(registration.email, registration.password)

    // when
    val response: WSResponse = await(wsUrl(s"/$fakeApiPath/authenticationRequired")
      .addHttpHeaders(HeaderNames.AUTHORIZATION -> s"Token ${tokenResponse.token}")
      .get())

    // then
    response.status.mustBe(OK)
    response.json.as[AuthenticatedUser].email.mustBe(user.email)
  }

  override def createComponents: AppWithTestComponents = new AppWithTestComponents {

    lazy val authenticationTestController: AuthenticationTestController = wire[AuthenticationTestController]

    override lazy val router: Router = {
      val testControllerRoutes: PartialFunction[RequestHeader, Handler] = {
        case GET(p"/test/public") => authenticationTestController.public
        case GET(p"/test/authenticationRequired") => authenticationTestController.authenticated
      }

      Router.from(routes.orElse(testControllerRoutes))
    }

    override lazy val dateTimeProvider: ProgrammaticDateTimeProvider = programmaticDateTimeProvider
  }

  class AuthenticationTestController(authenticatedAction: AuthenticatedActionBuilder,
                                     components: ControllerComponents,
                                     implicit private val ex: ExecutionContext)
    extends AbstractController(components) {

    def public: Action[AnyContent] = Action { _ =>
      Results.Ok
    }

    def authenticated: Action[AnyContent] = authenticatedAction { request =>
      Ok(Json.toJson(request.user))
    }

  }

}