

import akka.actor.{ActorLogging, Actor}
import spray.http.{HttpEntity, StatusCode, StatusCodes}
import spray.routing._
import spray.util.LoggingContext

import scala.util.control.NonFatal


class RoutedHttpService(route: Route) extends Actor with HttpService with ActorLogging {

  implicit def actorRefFactory = context


  def receive: Receive =
    runRoute(route)
}

