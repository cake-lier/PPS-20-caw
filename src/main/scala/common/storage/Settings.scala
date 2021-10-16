package it.unibo.pps.caw.common.storage

import play.api.libs.json.{Json, Writes, JsValue}

/** Representation of game settings: the music volume, the SFX volume and the indexes of completed default levels.
  *
  * It must be constructed through its companion object.
  */
sealed trait Settings {

  /** The volume of the game music, ranging from 0 to 1. */
  val musicVolume: Double

  /** The volume of the special effects, ranging from 0 to 1. */
  val soundVolume: Double

  /** The indexes of completed default levels. */
  val solvedLevels: Set[Int]
}

/** Companion object to the [[Settings]] trait. */
object Settings {

  /* Default implementation of the Settings trait. */
  private case class SettingsImpl(musicVolume: Double, soundVolume: Double, solvedLevels: Set[Int]) extends Settings

  /** Returns a new instance of the [[Settings]] trait, given the values that makes up the settings themselves.
    *
    * @param musicVolume
    *   the value of the music volume which is part of the current settings
    * @param soundVolume
    *   the value of the sound volume which is part of the current settings
    * @param solvedLevels
    *   the indexes of the solved levels which are part of the current settings
    * @return
    *   a new [[Settings]] instance
    */
  def apply(musicVolume: Double, soundVolume: Double, solvedLevels: Set[Int]): Settings =
    SettingsImpl(musicVolume, soundVolume, solvedLevels)

  /** Contains extension methods for the [[Settings]] trait. */
  extension (settings: Settings) {

    /** Copy constructor of the [[Settings]] trait which creates a new instance which is the exact clone of this instance, except
      * for the given values which will be assigned as new values for the corresponding properties.
      *
      * @param musicVolume
      *   the value of the music volume which is part of the current settings
      * @param soundVolume
      *   the value of the sound volume which is part of the current settings
      * @param solvedLevels
      *   the indexes of the solved levels which are part of the current settings
      * @return
      *   a clone of this [[Settings]] instance
      */
    def copy(
      musicVolume: Double = settings.musicVolume,
      soundVolume: Double = settings.soundVolume,
      solvedLevels: Set[Int] = settings.solvedLevels
    ): Settings = SettingsImpl(musicVolume, soundVolume, solvedLevels)
  }

  /** Allows for a [[Settings]] value to be converted to a [[JsValue]]. */
  implicit val settingsWrites: Writes[Settings] = new Writes[Settings] {
    override def writes(settings: Settings) = Json.obj(
      "musicVolume" -> settings.musicVolume,
      "soundVolume" -> settings.soundVolume,
      "solvedLevels" -> settings.solvedLevels
    )
  }
}
