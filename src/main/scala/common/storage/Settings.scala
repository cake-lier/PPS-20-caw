package it.unibo.pps.caw.common.storage

/** Representation of game settings: the music volume, the SFX volume and the indexes of completed default levels.
  *
  * @param musicVolume
  *   the volume of the music
  * @param soundVolume
  *   the volume of special effects
  * @param solvedLevels
  *   a set of indexes of the default levels already solved by the player
  */
case class Settings(musicVolume: Double, soundVolume: Double, solvedLevels: Set[Int])
