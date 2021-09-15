package it.unibo.pps.caw
import jdk.dynalink.linker.support.Lookup
import scalafx.scene.media.{Media, MediaPlayer, MediaView}

import java.util.concurrent.{Executors, TimeUnit}

/** Volume values for music and sound */
enum Volume(value: Double) {
  case Mute extends Volume(0)
  case Low extends Volume(0.25)
  case Medium extends Volume(0.5)
  case High extends Volume(0.75)
  case Max extends Volume(1)

  def getVolume = value
}

/** Audio tipes, [[AudioType.Loop]] for looping tracks, [[AudioType.Once]] for play-once tracks */
enum AudioType {
  case Loop, Once
}

/** Player types. Each [[Track]] has audio file path and an [[AudioType]] */
enum Track(filePath: String, audioType: AudioType, volume: Volume) {
  case Menu extends Track("sounds/menu_music.mp3", AudioType.Loop, Volume.Max)
  case Game extends Track("sounds/game_music.mp3", AudioType.Loop, Volume.Max)
  case Button extends Track("sounds/button_click.mp3", AudioType.Once, Volume.Max)

  def getAudioType = audioType
  def getFilePath = filePath
  def getVolume = volume
}

/*sealed trait AudioManager {
  def setVolume(volume: Volume): Unit
  def play(player: Player, stopAll: Boolean = false): Unit
  def disposeAll(): Unit
}*/

/** Singleton for audio management */
object AudioManager {
  private val players = Track.values.map(t => t -> createPlayer(t)).toMap

  /** Reproduce the specified track
    * @param track:
    *   track to be reproduced
    * @param stopAll:
    *   optional param, if set to true stops all tracks befor starting the track
    */
  def play(track: Track, stopAll: Boolean = false): Unit = {
    if (stopAll) players.foreach(_._2.stop())
    players(track).setVolume(track.getVolume.getVolume)
    players(track).play()
  }

  /** release all resources used by the player */
  def disposeAll(): Unit = players.map(_._2).foreach(_.dispose())

  private def createPlayer(track: Track): MediaPlayer = {
    val mediaPlayer = MediaPlayer(Media(ClassLoader.getSystemResource(track.getFilePath).toExternalForm))
    if (track.getAudioType == AudioType.Loop) mediaPlayer.setCycleCount(MediaPlayer.Indefinite)
    if (track.getAudioType == AudioType.Once) mediaPlayer.onEndOfMedia = () => mediaPlayer.stop()
    mediaPlayer
  }
}
