package it.unibo.pps.caw
package common.view

import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Rectangle2D
import scalafx.stage.Screen

/** Utility object to automatically resize the [[PrimaryStage]] for the screen. */
object StageResizer {

  /** Resizes the [[PrimaryStage]] to the screen dimensions, so as to be fullscreen windowed.
    *
    * @param stage
    *   the stage to be resized
    */
  def resize(stage: PrimaryStage): Unit = {
    val heightRatio: Int = 9
    val widthRatio: Int = 16
    val screenBounds: Rectangle2D = Screen.primary.visualBounds
    stage.x = screenBounds.minX
    stage.y = screenBounds.minY
    stage.centerOnScreen()
    val unitaryHeight: Double = screenBounds.height / heightRatio
    val unitaryWidth: Double = screenBounds.width / widthRatio
    if (screenBounds.width < unitaryHeight * widthRatio) {
      stage.width = screenBounds.width
      stage.height = unitaryWidth * heightRatio
    } else if (screenBounds.height < unitaryWidth * heightRatio) {
      stage.height = screenBounds.height
      stage.width = unitaryHeight * widthRatio
    } else {
      stage.height = screenBounds.height
      stage.width = screenBounds.width
    }
  }
}
