package it.unibo.pps.caw

import scalafx.scene.media.AudioClip
import scalafx.scene.media.{Media, MediaPlayer}

/** The possible audio types for a given [[Track]]. */
enum AudioType {

  /** The type for looping, long-playing music tracks. */
  case Music

  /** The type for one-shot, short-playing sound tracks. */
  case Sound
}

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

/** The audio player, the view component responsible for the audio of the game.
  *
  * This component is responsible for managing everything related to music and sounds triggered by events in-game. This means
  * playing a given [[Track]] or simply managing the volume at which is played.
  */
trait AudioPlayer {

  /** Plays the specified [[Track]].
    * @param track
    *   the [[Track]] to be played
    */
  def play(track: Track): Unit

  /** Returns the volume at which all [[Track]] of a given [[AudioType]] are currently being played.
    *
    * @param audioType
    *   the [[AudioType]] for which getting the volume
    * @return
    *   the volume at which all [[Track]] of a given [[AudioType]] are currently being played
    */
  def getVolume(audioType: AudioType): Double

  /** Sets the volume of all [[Track]] of a given [[AudioType]] to the specified value.
    * @param volume
    *   the value for the volume to be set
    * @param audioType
    *   the [[AudioType]] to which the specified volume is set
    */
  def setVolume(volume: Double, audioType: AudioType): Unit
}

/** Companion object to the [[AudioPlayer]] trait, containing its factory method. */
object AudioPlayer {

  /* Default implementation of the AudioPlayer trait. */
  private class AudioPlayerImpl extends AudioPlayer {
    private val musicPlayers: Map[Track, MediaPlayer] =
      Track.values.filter(_.audioType == AudioType.Music).map(t => t -> createPlayer(t)).toMap
    private var soundPlayers: Map[Track, Set[MediaPlayer]] = Map()
    private var volumes: Map[AudioType, Double] = Map()

    setVolume(0.3, AudioType.Music)
    setVolume(0.7, AudioType.Sound)

    override def play(track: Track): Unit = track.audioType match {
      case AudioType.Music => {
        (musicPlayers - track).foreach(_._2.stop())
        musicPlayers(track).play()
      }
      case AudioType.Sound => {
        val soundPlayer: MediaPlayer = createPlayer(track)
        soundPlayers += (track -> (soundPlayers.getOrElse(track, Set()) + soundPlayer))
      }
    }

    override def getVolume(audioType: AudioType): Double = volumes(audioType)

    override def setVolume(volume: Double, audioType: AudioType): Unit = {
      volumes += (audioType -> volume)
      audioType match {
        case AudioType.Music => musicPlayers.foreach(_._2.setVolume(volume))
        case AudioType.Sound => soundPlayers.values.flatten.foreach(_.setVolume(volume))
      }
    }

    private def createPlayer(track: Track): MediaPlayer = {
      val mediaPlayer = MediaPlayer(Media(ClassLoader.getSystemResource(track.filePath).toExternalForm))
      track.audioType match {
        case AudioType.Music => mediaPlayer.setCycleCount(MediaPlayer.Indefinite)
        case AudioType.Sound => {
          mediaPlayer.onReady = {
            mediaPlayer.stop()
            mediaPlayer.play()
          }
          mediaPlayer.onEndOfMedia = soundPlayers += (track -> (soundPlayers(track) - mediaPlayer))
        }
      }
      mediaPlayer
    }
  }

  /** Returns a new instance of the [[AudioPlayer]] trait. */
  def apply(): AudioPlayer = AudioPlayerImpl()
}
