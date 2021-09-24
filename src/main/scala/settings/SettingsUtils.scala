package it.unibo.pps.caw
package settings

import play.api.libs.json.Json

import java.io.{FileWriter, File}
import java.nio.file.{Files, Path, Paths}
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

case class Settings(volumeMusic: Int, volumeSFX: Int, solvedLevels: Set[Int])

object SettingsUtils {

  def load(): Try[Settings] = {
    Using(Source.fromFile(ClassLoader.getSystemResource("settings.json").toURI))
      (_.getLines().mkString) match {
      case Success(jsonString: String) => {
        val json = Json.parse(jsonString)
        val volumeMusic = (json \ "volumeMusic").as[Int]
        val volumeSFX = (json \ "volumeSFX").as[Int]
        val solvedLevels = (json \ "solvedLevels").as[Set[Int]]
        Success(Settings(volumeMusic, volumeSFX, solvedLevels))
      }
      case Failure(e) => Failure(e)
    }
  }

  def save(settings: Settings): Unit = {
    val jsonSettings = Json.toJson(settings)(Json.writes[Settings])
    Using(new FileWriter(File(ClassLoader.getSystemResource("settings.json").toURI)))
      (_.write(jsonSettings.toString)) match {
      case Failure(e) => System.err.println("Error writing settings file.")
      case _ => println("Settings file updated.")
    }
  }
}

object Test extends App{
  SettingsUtils.load() match {
    case Success(s) => println(s)
    case _ => println("fail")
  }
  SettingsUtils.save(Settings(100, 200, Set(4, 0, 7)))

  SettingsUtils.load() match {
    case Success(s) => println(s)
    case _ => println("fail")
  }

//  println(ClassLoader.getSystemResource("settings.json").toURI)
//  PPS-20-caw/target/scala-3.0.2/classes/settings.json

}