name := "Spray"

version := "1.0"

scalaVersion := "2.10.4"



scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")


libraryDependencies ++= {
  val akkaVersion = "2.3.9"
  val sprayVersion = "1.3.3"
  val excludeconfig = ExclusionRule(organization = "com.typesafe", artifact= "config")
  Seq(
    "io.spray"            %%  "spray-can"     % sprayVersion excludeAll(excludeconfig),
    "io.spray"            %%  "spray-routing" % sprayVersion excludeAll(excludeconfig),
    "io.spray"            %%  "spray-json"    % "1.3.2" excludeAll(excludeconfig),
    "com.typesafe.akka"   %%  "akka-actor"    % akkaVersion excludeAll(excludeconfig)  )
}


libraryDependencies ++= {
  val sparkVersion = "1.5.1"
  val sparkCsvVersion = "1.2.0"
  val config = "1.2.1"
  val excludeconfig = ExclusionRule(organization = "com.typesafe", artifact= "config")
  Seq(
    "org.apache.spark"  %%  "spark-core"    % sparkVersion excludeAll(excludeconfig),
    "org.apache.spark"  %%  "spark-sql"     % sparkVersion excludeAll(excludeconfig),
    "org.apache.spark"  %%  "spark-graphx"  % sparkVersion excludeAll(excludeconfig),
    "com.databricks"    %%  "spark-csv"     % sparkCsvVersion excludeAll(excludeconfig)
  )
}



dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value