

//import SparkService
import spray.routing.HttpService
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by ajay on 17/10/15.
 */
trait Api extends HttpService with CoreActors with Core {

  /**
   * Lifted from here: http://www.tecnoguru.com/blog/2014/07/07/implementing-http-basic-authentication-with-spray/
   * Good info here: http://www.staticapps.org/articles/authentication-and-authorization
   */
  val routes = pathPrefix("v1") {
    new SparkService(sparkManager).route
  }

}