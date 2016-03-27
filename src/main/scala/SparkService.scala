

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import SparkServiceActor._
import spray.http.MediaTypes._
import spray.routing.{Directives, Route}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._


class SparkService(actor: ActorRef)(implicit executionContext: ExecutionContext) extends DefaultJsonFormats with Directives  {

  implicit val timeout = Timeout(30.seconds)

  def route: Route = pathPrefix("spark") {
          pathPrefix("train") {
       /* path(Segment) { id =>
          pathEndOrSingleSlash {
            get {

                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? GetTrain(id.toInt)).mapTo[Train]
                  }
                }
              }

          }
        } ~*/
         pathEndOrSingleSlash {
          get {

              // More info: http://spray.io/documentation/1.2.2/spray-routing/parameter-directives/parameters/#signature
              parameter('size.as[Int].?) { (size) =>
                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? GetTrains(size.getOrElse(10))).mapTo[Seq[Train]]
                  }
                }
              }
            }
          }

      } ~ pathPrefix("station") {
        path(Segment) { id =>
          pathEndOrSingleSlash {
            get {

                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                    (actor ? GetStation(id)).mapTo[Station]
                  }
                }
              }

          }
        } ~ pathEndOrSingleSlash {
          get {

              // More info: http://spray.io/documentation/1.2.2/spray-routing/parameter-directives/parameters/#signature
              parameter('size.as[Int].?) { (size) =>
                respondWithMediaType(`application/json`) { res =>
                  res.complete {
                   val test = (actor ? GetStations(size.getOrElse(10))).mapTo[Seq[Station]]
                    val load = Await.result(test, 60.seconds)
                    test

                  }
                }
              }

          }
        }
      } ~ pathPrefix("calculate") {

            // More info: http://spray.io/documentation/1.2.2/spray-routing/parameter-directives/parameters/#signature
            parameter('depart.as[String], 'arrive.as[String]) { (depart, arrive) =>
              path("train") {
                get {
                  respondWithMediaType(`application/json`) { res =>
                    res.complete {
                      (actor ? TrainsBetween(depart, arrive)).mapTo[Seq[Train]]
                    }
                  }
                }
              } ~ path("station") {
                get {
                  respondWithMediaType(`application/json`) { res =>
                    res.complete {
                      (actor ? StopsBetween(depart, arrive)).mapTo[Seq[Journey]]
                    }
                  }
                }
              }
            }
          }


    }


}


