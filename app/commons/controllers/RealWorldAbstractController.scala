package commons.controllers

import com.softwaremill.macwire.wire
import commons.exceptions.ValidationException
import commons.models.ValidationResultWrapper
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc.{AbstractController, BodyParser, ControllerComponents, Result}
import javax.inject.Inject
import play.api.libs.mailer.MailerClient

import scala.concurrent.ExecutionContext

abstract class RealWorldAbstractController (controllerComponents: ControllerComponents)
  extends AbstractController(controllerComponents) {

  implicit protected val executionContext: ExecutionContext = defaultExecutionContext

  protected def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  protected def handleFailedValidation: PartialFunction[Throwable, Result] = {
    case e: ValidationException =>
      val errors = e.violations
        .groupBy(_.property)
        .mapValues(_.map(propertyViolation => propertyViolation.violation.message))

      val wrapper: ValidationResultWrapper = ValidationResultWrapper(errors)
      UnprocessableEntity(Json.toJson(wrapper))
  }

}