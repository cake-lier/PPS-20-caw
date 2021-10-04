package it.unibo.pps.caw.common.view.sounds

import scalafx.scene.media.{Media, MediaPlayer}
import scala.util.Success

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

    override def play(track: Track): Unit = track.audioType match {
      case AudioType.Music => {
        (musicPlayers - track).foreach(_._2.stop())
        musicPlayers(track).play()
      }
      case AudioType.Sound => {
        val soundPlayer: MediaPlayer = createPlayer(track)
        soundPlayer.volume = volumes(AudioType.Sound)
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
