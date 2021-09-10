name := "PPS-20-caw"

version := "0.1"

scalaVersion := "3.0.2"

idePackagePrefix := Some("it.unibo.pps.caw")

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.9",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "com.typesafe.play" %% "play-json" % "2.10.0-RC5"
)
