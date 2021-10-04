package it.unibo.pps.caw.common.view.sounds

/** Track types. Each [[Track]] has audio file path and an [[AudioType]]. */
enum Track(fileName: String, val audioType: AudioType) {

  /** The music to be played during the main menu. */
  case MenuMusic extends Track("menu_music", AudioType.Music)

  /** The music to be played during the game. */
  case GameMusic extends Track("game_music", AudioType.Music)

  /** The music to be played when the editor is open. */
  case EditorMusic extends Track("editor_music", AudioType.Music)

  /** The sound to be played when an enemy explodes. */
  case Explosion extends Track("explosion", AudioType.Sound)

  /** The sound to be played when a game step is taken. */
  case Step extends Track("step", AudioType.Sound)

  /** The sound to be played when the player wins a level. */
  case Victory extends Track("victory", AudioType.Sound)

  /** Returns the path of the file containg with this [[Track]]. */
  val filePath: String = s"sounds/$fileName.mp3"
}
