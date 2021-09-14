name := "PPS-20-caw"

version := "0.1"

scalaVersion := "3.0.2"

scalacOptions ++= Seq("-language:implicitConversions")

idePackagePrefix := Some("it.unibo.pps.caw")

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
}

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.9",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "org.typelevel" %% "cats-core" % "2.6.1",
  "org.typelevel" %% "cats-kernel" % "2.6.1",
  "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
  "org.scalafx" %% "scalafx" % "16.0.0-R24",
  "io.vertx" % "vertx-json-schema" % "4.1.3"
) ++ Seq("base", "controls", "fxml", "graphics", "media").map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
