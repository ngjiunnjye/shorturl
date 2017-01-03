name := """url-shortening-common"""

organization  := "com.github.ngjiunnjye"

version := "0.0.1"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaStreamVersion = "1.0"
  val akkaVersion = "2.3.11"

  Seq(
    "org.apache.kafka"  %  "kafka_2.11"                           % "0.10.1.1",
    "org.scalatest"     %% "scalatest"                            % "2.2.5" % "test",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamVersion,
    "io.spray"          %% "spray-json"                           % "1.3.2",
    "com.typesafe.akka" %% "akka-testkit"                         % akkaVersion % "test"
  )
}

//Revolver.settings


fork in run := true
