name := "PPS-20-caw"

version := "0.1"

scalaVersion := "3.0.2"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "io.vertx" % "vertx-json-schema" % "4.1.3",
  "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0"
)

idePackagePrefix := Some("it.unibo.pps.caw")
