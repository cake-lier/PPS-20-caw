package it.unibo.pps.caw.common

import play.api.libs.json.Json

import java.io.{File, FileNotFoundException, FileWriter}
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

/** Represents storage for the game settings: it allows to load and save settings to a file so as to memorize the
  * settings between game sessions.
  */
trait SettingsStorage {

  /** Returns default settings. */
  val defaultSettings: Settings

  /** Loads settings from disk. If the settings file does not exist, it is created.
    *
    * The settings file is a .json that corresponds to the json representation of case class [[Settings]].
    *
    * @return
    *   the requested [[Settings]] or an exception from file reading/writing
    */
  def load(): Try[Settings]

  /** Saves settings to disk. The settings file will be overwritten with the new settings.
    *
    * @param settings
    *   the [[Settings]] to be saved
    * @return
    *   an exception if it occurs during IO operations
    */
  def save(settings: Settings): Try[Unit]
}

/** Companion object to the [[SettingsStorage]] trait */
object SettingsStorage {

  private class SettingsStorageImpl(fileStorage: FileStorage) extends SettingsStorage {
    val defaultSettings = Settings(0.3, 0.7, Set())
    private val defaultSettingsJson = Json.toJson(defaultSettings)(Json.writes[Settings])
    private val filePath = System.getProperty("user.home") + File.separator + ".settings.json"

    override def load(): Try[Settings] = {
      fileStorage.loadFile(filePath) match {
        case Success(jsonString: String) => {
          val json = Json.parse(jsonString)
          val volumeMusic = (json \ "volumeMusic").as[Double]
          val volumeSFX = (json \ "volumeSFX").as[Double]
          val solvedLevels = (json \ "solvedLevels").as[Set[Int]]
          Success(Settings(volumeMusic, volumeSFX, solvedLevels))
        }
        case Failure(e: FileNotFoundException) =>
          fileStorage.writeFile(filePath, defaultSettingsJson.toString) match {
            case Failure(e) => Failure(e)
            case _          => Success(defaultSettings)
          }
        case Failure(e) => Failure(e)
      }
    }

    override def save(settings: Settings): Try[Unit] = {
      val jsonSettings = Json.toJson(settings)(Json.writes[Settings])
      fileStorage.writeFile(filePath, jsonSettings.toString)
    }
  }

  /** Returns a new instance of the [[SettingsStorage]] trait.
    *
    * @return
    * a new [[SettingsStorage]] instance
    */
  def apply(fileStorage: FileStorage): SettingsStorage = SettingsStorageImpl(fileStorage)
}
