

import spray.httpx.SprayJsonSupport
import spray.json.{JsString, JsObject, RootJsonWriter, DefaultJsonProtocol}


trait DefaultJsonFormats extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val stationJsonFormat = jsonFormat5(Station)
  implicit val trainJsonFormat = jsonFormat5(Train)
  implicit val stopJsonFormat = jsonFormat4(Stop)
  implicit val routeJsonFormat = jsonFormat3(Journey)

}