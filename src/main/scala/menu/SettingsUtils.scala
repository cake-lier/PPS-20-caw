package it.unibo.pps.caw
package menu

import play.api.libs.json.Json

import java.io.{File, FileNotFoundException, FileWriter}
import java.nio.file.{Files, Path}
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

case class Settings(volumeMusic: Double, volumeSFX: Double, solvedLevels: Set[Int])

object SettingsUtils {
  val defaultSettings = "{\"volumeMusic\":0.3,\"volumeSFX\":0.7,\"solvedLevels\":[]}"
  val filePath = System.getProperty("user.home") + File.separator + ".settings.json"

  def load(): Try[Settings] = {
    Loader.loadAbsolute(filePath) match {
      case Success(jsonString: String) => {
        val json = Json.parse(jsonString)
        val volumeMusic = (json \ "volumeMusic").as[Double]
        val volumeSFX = (json \ "volumeSFX").as[Double]
        val solvedLevels = (json \ "solvedLevels").as[Set[Int]]
        Success(Settings(volumeMusic, volumeSFX, solvedLevels))
      }
      case Failure(e: FileNotFoundException) => writeSettings(defaultSettings) match {
                                                  case Failure(e) => Failure(e)
                                                  case _ => load()
                                                }
      case Failure(e) => Failure(e)
    }
  }

  def save(settings: Settings): Try[Unit] = {
    val jsonSettings = Json.toJson(settings)(Json.writes[Settings])
    writeSettings(jsonSettings.toString)
  }

  /* Creates or overwrites file, managing exceptions */
  private def writeSettings(body: String): Try[Unit] = Using(new FileWriter(File(filePath)))(_.write(body))

}

object Test extends App{
  SettingsUtils.load() match {
    case Success(s) => println(s)
    case Failure(exception) => println("failed : " + exception.toString)
  }
  SettingsUtils.save(Settings(0.2, 0.5, Set(1, 9, 8, 4)))

  SettingsUtils.load() match {
    case Success(s) => println(s)
    case _ => println("fail")
  }
}