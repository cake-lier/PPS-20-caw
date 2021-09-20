package it.unibo.pps.caw
import jdk.dynalink.linker.support.Lookup
import scalafx.scene.media.{Media, MediaPlayer, MediaView}

import java.util.concurrent.{Executors, TimeUnit}

/** Audio tipes, [[AudioType.Loop]] for looping tracks, [[AudioType.Once]] for play-once tracks */
enum AudioType {
  case Music
  case Sound
}

/** Player types. Each [[Track]] has audio file path and an [[AudioType]] */
enum Track(fileName: String, val audioType: AudioType) {
  case MenuMusic extends Track("menu_music", AudioType.Music)
  case GameMusic extends Track("game_music", AudioType.Music)
  case EditorMusic extends Track("editor_music", AudioType.Music)
  case Explosion extends Track("explosion", AudioType.Sound)
  case Step extends Track("step", AudioType.Sound)
  case Victory extends Track("victory", AudioType.Sound)

  val filePath: String = s"sounds/$fileName.mp3"
}

trait AudioPlayer {

  /** Reproduce the specified track
    * @param track
    *   track to be reproduced
    */
  def play(track: Track): Unit

  /** set volume of all media to the specified value
    * @param volume
    *   volume to be set
    * @param audioType:
    *   the type to which the specified volume is set
    */
  def setVolume(volume: Double, audioType: AudioType): Unit
}

object AudioPlayer {

  private class AudioPlayerImpl extends AudioPlayer {
    private val players = Track.values.map(t => t -> createPlayer(t)).toMap

    override def play(track: Track): Unit = {
      if (track.audioType == AudioType.Music) players.filter(_._1.audioType == AudioType.Music).foreach(_._2.stop())
      players(track).play()
    }

    override def setVolume(volume: Double, audioType: AudioType): Unit =
      players.filter(_._1.audioType == audioType).foreach(_._2.setVolume(volume))

    private def createPlayer(track: Track): MediaPlayer = {
      val mediaPlayer = MediaPlayer(Media(ClassLoader.getSystemResource(track.filePath).toExternalForm))
      track.audioType match {
        case AudioType.Music => mediaPlayer.setCycleCount(MediaPlayer.Indefinite)
        case AudioType.Sound => mediaPlayer.onEndOfMedia = () => mediaPlayer.stop()
      }
      mediaPlayer
    }
  }

  def apply(): AudioPlayer = AudioPlayerImpl()
}
