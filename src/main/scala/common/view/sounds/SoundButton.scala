package it.unibo.pps.caw
package common.view.sounds

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.media.AudioClip

/** Represent a button that when clicked emits a sound */
class SoundButton extends Button {
  val sound: AudioClip = AudioClip(getClass.getResource("/sounds/button_click.mp3").toExternalForm)

  onActionProperty().set(_ => sound.play())
}
