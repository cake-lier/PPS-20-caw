package it.unibo.pps.caw.common.storage

import play.api.libs.json.{Json, Writes, JsValue}

/** Representation of game settings: the music volume, the SFX volume and the indexes of completed default levels. */
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
  private case class SettingsImpl(musicVolume: Double, soundVolume: Double, solvedLevels: Set[Int]) extends Settings

  /** Returns a new instance of the [[Settings]] trait. */
  def apply(musicVolume: Double, soundVolume: Double, solvedLevels: Set[Int]): Settings =
    SettingsImpl(musicVolume, soundVolume, solvedLevels)

  /** Allows for a [[Settings]] value to be converted to a [[JsValue]]. */
  implicit val settingsWrites: Writes[Settings] = new Writes[Settings] {
    def writes(settings: Settings) = Json.obj(
      "musicVolume" -> settings.musicVolume,
      "soundVolume" -> settings.soundVolume,
      "solvedLevels" -> settings.solvedLevels
    )
  }
}
