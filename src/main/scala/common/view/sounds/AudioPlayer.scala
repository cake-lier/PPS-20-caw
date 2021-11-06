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
  private class AudioPlayerImpl(musicVolume: Double, private var soundsVolume: Double) extends AudioPlayer {
    private val musicPlayers: Map[Track, MediaPlayer] =
      Track
        .values
        .filter(_.audioType == AudioType.Music)
        .map(t => {
          val mediaPlayer = MediaPlayer(Media(getClass.getResource(t.filePath).toString))
          mediaPlayer.cycleCount = MediaPlayer.Indefinite
          mediaPlayer.volume = musicVolume
          t -> mediaPlayer
        })
        .toMap
    private var soundsPlayers: Map[Track, Set[MediaPlayer]] = Map()

    override def play(track: Track): Unit = track.audioType match {
      case AudioType.Music => {
        (musicPlayers - track).foreach(_._2.stop())
        musicPlayers(track).play()
      }
      case AudioType.Sound => {
        val mediaPlayer = MediaPlayer(Media(getClass.getResource(track.filePath).toString))
        mediaPlayer.onReady = {
          mediaPlayer.stop()
          mediaPlayer.play()
        }
        mediaPlayer.onEndOfMedia = soundsPlayers += (track -> (soundsPlayers(track) - mediaPlayer))
        mediaPlayer.volume = soundsVolume
        soundsPlayers += (track -> (soundsPlayers.getOrElse(track, Set()) + mediaPlayer))
      }
    }

    override def setVolume(volume: Double, audioType: AudioType): Unit = audioType match {
      case AudioType.Music => musicPlayers.foreach(_._2.setVolume(volume))
      case AudioType.Sound => {
        soundsPlayers.values.flatten.foreach(_.setVolume(volume))
        soundsVolume = volume
      }
    }
  }

  /** Returns a new instance of the [[AudioPlayer]] trait. */
  def apply(musicVolume: Double, soundsVolume: Double): AudioPlayer = AudioPlayerImpl(musicVolume, soundsVolume)
}
