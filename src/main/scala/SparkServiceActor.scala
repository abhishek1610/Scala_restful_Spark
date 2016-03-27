//package services

import akka.actor.Actor
import org.apache.spark.graphx.lib.PageRank
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.hashing.MurmurHash3


/**
 * Define all the message objects to be passed around by the actors
 */
object SparkServiceActor {

  case class GetTrains(size: Int)
  case class GetTrain(id: Int)

  case class GetStations(size: Int)
  case class GetStation(id: String)

  case class TrainsBetween(depart: String, arrive: String)
  case class StopsBetween(depart: String, arrive: String)
  case class TrainRoute(train: String)


}

class SparkServiceActor extends Actor with Configuration {

  import SparkServiceActor._
  lazy val sparkConf:SparkConf = new SparkConf().setAppName(name).setMaster(master)

  lazy val sc: SparkContext = new SparkContext(sparkConf)
  lazy val sqlContext = new SQLContext(sc)

  // Loading the data from csv into SQL
  lazy val df = sqlContext.read.format(format).option("header", "true").option("inferSchema", "true").load(data)
  //df.show()
  def getTrainsBetweenStations(from: String, to: String) = {
    df.select("train_no", "train_name", "station_code", "source_station_code", "source_station_name", "destination_station_code", "destination_station_name", "distance").where(s"source_station_code = '${from.padTo(4, ' ')}' and destination_station_code = '${to.padTo(4, ' ')}' ").distinct()
      .collect().map(row => Train(row.getString(0).split("'")(1).toInt, row.getString(1).trim, Station(row.getString(3).trim, row.getString(4).trim), Station(row.getString(5).trim, row.getString(6).trim), row.getInt(7)))



  }

  def getStopsBetweenStations(from: String, to: String) = {
    df.select("train_no", "train_name", "source_station_code", "destination_station_code", "station_code", "station_name", "isl_no", "distance").where(s"source_station_code = '${from.padTo(4, ' ')}' and destination_station_code = '${to.padTo(4, ' ')}'")
      .collect().groupBy(row => (row.getString(0), row.getString(1))).map(row => Journey(row._1._1.split("'")(1).toInt, row._1._2.trim, row._2.map(stop => Stop(stop.getString(4).trim, stop.getString(5).trim, stop.getInt(6), stop.getInt(7)))))
  }


  lazy val trains = df.select("train_no", "train_name", "station_code", "source_station_code", "source_station_name", "destination_station_code", "destination_station_name", "distance").where("station_code = destination_station_code")
    .map(row => Train(row.getString(0).split("'")(1).toInt, row.getString(1).trim, Station(row.getString(3).trim, row.getString(4).trim), Station(row.getString(5).trim, row.getString(6).trim), row.getInt(7))).distinct(numPartitions = 1)
  lazy val stations = trains.flatMap(train => Array(train.source, train.destination)).distinct(numPartitions = 1)


  def receive: Receive = {
    case GetTrains(size) =>
      sender ! trains.take(size).toSeq
    case GetTrain(id) =>
      sender ! trains.filter(train => train.id == id).collect().head
    case GetStations(size) =>
      sender ! stations.take(size).toSeq
    case GetStation(id) =>
      sender ! stations.filter(station => station.id == id).collect().head
    case TrainsBetween(depart, arrive) =>
      sender ! getTrainsBetweenStations(depart, arrive).toSeq
       // System.out.println(test)
    case StopsBetween(depart, arrive) =>
      sender ! getStopsBetweenStations(depart, arrive).toSeq

  }


}
