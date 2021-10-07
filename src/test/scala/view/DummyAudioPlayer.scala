package it.unibo.pps.caw
package view

import common.view.sounds.{AudioPlayer, AudioType, Track}

import scalafx.scene.media.{Media, MediaPlayer}

object DummyAudioPlayer {

  private class AudioPlayerImpl extends AudioPlayer {
    override def play(track: Track): Unit = println()

    override def getVolume(audioType: AudioType): Double = 0

    override def setVolume(volume: Double, audioType: AudioType): Unit = println()
  }

  def apply(): AudioPlayer = AudioPlayerImpl()
}
