package it.unibo.pps.caw
package settings

import play.api.libs.json.Json

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

case class Settings(volumeMusic: Int, volumeSFX: Int, solvedLevels: Set[Int])

object SettingsUtils {

  def load(): Try[Settings] = {
    Using(Source.fromResource("settings.json"))(_.getLines().mkString) match {
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

  def save(): Unit = ???

}

object Test extends App{
  SettingsUtils.load() match {
    case Success(s) => println(s)
    case _ => println("fail")
  }
}