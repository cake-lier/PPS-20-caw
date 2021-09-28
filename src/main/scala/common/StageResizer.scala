package it.unibo.pps.caw.common

import scalafx.geometry.Rectangle2D
import scalafx.stage.Screen
import scalafx.application.JFXApp3.PrimaryStage

object StageResizer {
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
