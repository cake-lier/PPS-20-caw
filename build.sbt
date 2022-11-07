name := "PPS-20-caw"

version := "0.1"

scalaVersion := "3.2.1"

scalacOptions ++= Seq("-language:implicitConversions")

idePackagePrefix := Some("it.unibo.pps.caw")

Compile / resourceGenerators += Def.task {
  val outputFile: File = (Compile / resourceManaged).value / "cellmachine.pl"
  IO.write(
    outputFile,
    IO.readLines((Compile / resourceDirectory).value / "cellmachine.pl").filterNot(_.startsWith("%")).mkString("\n")
  )
  Seq(outputFile)
}.taskValue

Compile / excludeFilter := "*.pl"

assembly / mainClass := Some("it.unibo.pps.caw.app.Main")
assembly / assemblyJarName := "caw.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.discard
  case "module-info.class"                                  => MergeStrategy.discard
  case v                                                    => (ThisBuild / assemblyMergeStrategy).value(v)
}

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
}

ThisBuild / resolvers += Resolver.jcenterRepo
Test / fork := true

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.14" % Test,
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "org.typelevel" %% "cats-core" % "2.8.0",
  "org.typelevel" %% "cats-kernel" % "2.8.0",
  "com.typesafe.play" %% "play-json" % "2.10.0-RC7",
  "org.scalafx" %% "scalafx" % "16.0.0-R25",
  "io.vertx" % "vertx-json-schema" % "4.3.4",
  "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0",
  "org.testfx" % "testfx-core" % "4.0.16-alpha" % Test,
  "org.junit.jupiter" % "junit-jupiter" % "5.9.1" % Test,
  "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
  "org.testfx" % "testfx-junit5" % "4.0.16-alpha" % Test,
  "org.assertj" % "assertj-core" % "3.23.1" % Test,
  "org.testfx" % "openjfx-monocle" % "jdk-12.0.1+2" % Test,
  "org.openjfx" % "javafx-base" % "19" classifier osName,
  "org.openjfx" % "javafx-controls" % "19" classifier osName,
  "org.openjfx" % "javafx-fxml" % "19" classifier osName,
  "org.openjfx" % "javafx-graphics" % "16" classifier osName,
  "org.openjfx" % "javafx-media" % "16" classifier osName
)
