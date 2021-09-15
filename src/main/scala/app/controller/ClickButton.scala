package it.unibo.pps.caw
package app.controller

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent

/** Represent a button that when clicked emits a sound */
class ClickButton extends Button {
  onActionProperty().set(_ => AudioManager.play(Track.Button))
}
