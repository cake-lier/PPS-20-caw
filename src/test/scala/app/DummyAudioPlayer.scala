package it.unibo.pps.caw
package app

import common.view.sounds.*

object DummyAudioPlayer {

  private class AudioPlayerImpl extends AudioPlayer {
    private var volumes: Map[AudioType, Double] = Map(AudioType.Music -> 0.5, AudioType.Sound -> 0.5)

    override def play(track: Track): Unit = {}

    override def setVolume(volume: Double, audioType: AudioType): Unit = volumes += (audioType -> volume)
  }

  def apply(): AudioPlayer = AudioPlayerImpl()
}
