package it.unibo.pps.caw

import play.api.libs.json.Json

import java.io.{File, FileNotFoundException, FileWriter}
import java.nio.file.{Files, Path}
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

/** Representation of game settings: the music volume, the SFX volume and the indexes of completed default levels.
  *
  * @param volumeMusic
  *   the volume of the music
  * @param volumeSFX
  *   the volume of special effects
  * @param solvedLevels
  *   a set of indexes of the default levels already solved by the player
  */
case class Settings(volumeMusic: Double, volumeSFX: Double, solvedLevels: Set[Int])

/** The manager for the game settings: it allows to load and save settings to a file so as to memorize the settings
  * between game sessions.
  */
trait SettingsManager {

  /** Load settings from disk. If the settings file does not exist, it is created.
    *
    * @return
    *   the requested [[Settings]] or an exception from file reading/writing
    */
  def load(): Try[Settings]

  /** Save settings to disk. The settings file will be overwritten with the new settings.
    *
    * @param settings
    *   the [[Settings]] to be saved
    * @return
    *   an exception if it occurs during file writing
    */
  def save(settings: Settings): Try[Unit]

}

/** Companion object to the [[SettingsManager]] trait */
object SettingsManager {

  private class SettingsManagerImpl() extends SettingsManager {

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

  /** Returns a new instance of the [[SettingsManager]] trait.
    *
    * @return
    *   a new [[SettingsManager]] instance
    */
  def apply(): SettingsManager = SettingsManagerImpl()
}

object Test extends App{
  val sm = SettingsManager()
  sm.load() match {
    case Success(s) => println(s)
    case Failure(exception) => println("failed : " + exception.toString)
  }
  sm.save(Settings(0.2, 0.5, Set(1, 9, 8, 4)))

  sm.load() match {
    case Success(s) => println(s)
    case _ => println("fail")
  }
}