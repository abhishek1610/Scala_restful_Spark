# Scala_restful_Spark
Scala Spray based Spark restful app

basically we ar using some train.csv data to calculate the various queries

Sparkservice is the main Spray routes applcation of the various endpoint

It ask data from SparkServiceActor which is the Spark application which does all the processing

In trait CoreActors we intialize -val sparkManager = system.actorOf(Props[SparkServiceActor], "spark")  actor
In trait Api we pass the above ActorRef to Spray app - 
val routes = pathPrefix("v1") {
    new SparkService(sparkManager).route }
    
In BootedCore we create the handler of the application passing the above routes

val rootService = system.actorOf(Props(new RoutedHttpService(routes )))

RoutedHttpService is basically another actor which routes the request to specific route in SparkService.

The rest endpoints are -


