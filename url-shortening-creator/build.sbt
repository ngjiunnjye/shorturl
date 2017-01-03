name := """url-shortening-creator"""

organization  := "com.github.ngjiunnjye"

version := "0.0.1"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaStreamVersion = "1.0"
  val akkaVersion = "2.4.8"

  Seq(
    "com.typesafe.akka" %% "akka-actor"                           % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-experimental"             % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-http-experimental"               % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-http-core-experimental"          % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-http-testkit-experimental"       % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-cluster"                         % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools"                   % akkaVersion,
    "org.apache.kafka"  %  "kafka_2.11"                           % "0.10.1.1", 
    "io.spray"          %% "spray-json"	                          % "1.3.2",
    "org.scalatest"     %% "scalatest"                            % "2.2.5" % "test",
    "com.typesafe.akka" %% "akka-testkit"                         % akkaVersion % "test"
  )
}


Revolver.settings


fork in run := true
