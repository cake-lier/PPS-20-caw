package it.unibo.pps.caw
import jdk.dynalink.linker.support.Lookup
import scalafx.scene.media.{Media, MediaPlayer, MediaView}

import java.util.concurrent.{Executors, TimeUnit}

/** Audio tipes, [[AudioType.Loop]] for looping tracks, [[AudioType.Once]] for play-once tracks */
enum AudioType {
  case Music, Sound
}

/** Player types. Each [[Track]] has audio file path and an [[AudioType]] */
enum Track(val filePath: String, val audioType: AudioType) {
  case Menu extends Track("sounds/menu_music.mp3", AudioType.Music)
  case Game extends Track("sounds/game_music.mp3", AudioType.Music)
  case Button extends Track("sounds/button_click.mp3", AudioType.Sound)
}

/** Singleton for audio management */
object AudioManager {
  private val players = Track.values.map(t => t -> createPlayer(t)).toMap

  /** Reproduce the specified track
    * @param track:
    *   track to be reproduced
    */
  def play(track: Track): Unit = {
    if (track.audioType == AudioType.Music) players.filter(_._1.audioType == AudioType.Music).foreach(_._2.stop())
    players(track).play()
  }

  /** set volume of all media to the specified value
    * @param volume:
    *   volume to be set
    */
  def globalVolume(volume: Double): Unit = players.foreach(_._2.setVolume(volume))

  /** release all resources used by the player */
  def disposeAll(): Unit = players.map(_._2).foreach(_.dispose())

  private def createPlayer(track: Track): MediaPlayer = {
    val mediaPlayer = MediaPlayer(Media(ClassLoader.getSystemResource(track.filePath).toExternalForm))
    track.audioType match {
      case AudioType.Music => mediaPlayer.setCycleCount(MediaPlayer.Indefinite)
      case AudioType.Sound => mediaPlayer.onEndOfMedia = () => mediaPlayer.stop()
    }
    mediaPlayer
  }
}
