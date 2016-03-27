

import spray.http.{StatusCode, StatusCodes}
import spray.routing.{RequestContext, ExceptionHandler, RejectionHandler, HttpService}
import spray.util.LoggingContext


trait FailureHandling {
  this: HttpService =>

  def rejectionHandler: RejectionHandler = RejectionHandler.Default

  def exceptionHandler(implicit log: LoggingContext) = ExceptionHandler {
    case e: IllegalArgumentException => ctx =>
      loggedFailureResponse(ctx, e,
        message = "The server was asked a question that didn't make sense: " + e.getMessage,
        error = StatusCodes.NotAcceptable)

    case e: NoSuchElementException => ctx =>
      loggedFailureResponse(ctx, e,
        message = "The server is missing some information. Try again in a few moments.",
        error = StatusCodes.NotFound)

    case t: Throwable => ctx =>
      loggedFailureResponse(ctx, t)

  }

  private def loggedFailureResponse(ctx: RequestContext,
                                    thrown: Throwable,
                                    message: String = "The server is having problems",
                                    error: StatusCode = StatusCodes.InternalServerError)
                                   (implicit log: LoggingContext): Unit = {
    log.error(thrown, ctx.request.toString)
    ctx.complete((error, message))
  }
}
