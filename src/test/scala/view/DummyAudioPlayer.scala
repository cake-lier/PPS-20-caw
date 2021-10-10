package it.unibo.pps.caw.view

import it.unibo.pps.caw.common.view.sounds.{AudioPlayer, AudioType, Track}

object DummyAudioPlayer {

  private class AudioPlayerImpl extends AudioPlayer {
    private var volumes: Map[AudioType, Double] = Map(AudioType.Music -> 0.5, AudioType.Sound -> 0.5)

    override def play(track: Track): Unit = {}

    override def getVolume(audioType: AudioType): Double = volumes(audioType)

    override def setVolume(volume: Double, audioType: AudioType): Unit = volumes += (audioType -> volume)
  }

  def apply(): AudioPlayer = AudioPlayerImpl()
}
