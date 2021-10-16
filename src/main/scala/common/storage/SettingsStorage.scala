package it.unibo.pps.caw.common.storage

import play.api.libs.json.{Json, JsValue}

import java.io.{File, FileNotFoundException, FileWriter}
import scala.util.{Failure, Success, Try, Using}

/** Represents a storage for the game [[Settings]].
  *
  * This component allows to load from and save settings to a file so as to memorize them between game sessions. It must be
  * constructed through its companion object.
  */
trait SettingsStorage {

  /** Returns the default settings. */
  val defaultSettings: Settings

  /** Loads settings from disk. If the settings file does not exist, it is created.
    *
    * The settings file is a .json that corresponds to the json representation of case class [[Settings]].
    *
    * @return
    *   the requested [[Settings]] or an exception if it occurs during IO operations
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

/** Companion object to the [[SettingsStorage]] trait, containing its factory method. */
object SettingsStorage {

  /* Default implementation of the SettingsStorage trait. */
  private class SettingsStorageImpl(fileStorage: FileStorage) extends SettingsStorage {
    override val defaultSettings: Settings = Settings(0.5, 0.5, Set())

    private val defaultSettingsJson: JsValue = Json.toJson(defaultSettings)
    private val filePath: String = System.getProperty("user.home") + File.separator + ".settings_caw.json"

    override def load(): Try[Settings] = {
      fileStorage.loadFile(filePath) match {
        case Success(jsonString: String) =>
          Try {
            val json = Json.parse(jsonString)
            Settings((json \ "musicVolume").as[Double], (json \ "soundVolume").as[Double], (json \ "solvedLevels").as[Set[Int]])
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
      val jsonSettings = Json.toJson(settings)
      fileStorage.writeFile(filePath, jsonSettings.toString)
    }
  }

  /** Returns a new instance of the [[SettingsStorage]] trait given the storage from which retrieving and storing files on disk to
    * be used in settings file manipulation operations.
    *
    * @param fileStorage
    *   the file storage to be used for retrieving from and storing to disk the settings file
    * @return
    *   a new [[SettingsStorage]] instance
    */
  def apply(fileStorage: FileStorage): SettingsStorage = SettingsStorageImpl(fileStorage)
}
