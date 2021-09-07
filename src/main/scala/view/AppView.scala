package it.unibo.pps.caw
package view

import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

class AppView(stage: PrimaryStage) {
  private val gameTitle = "Cells at Work"

  def drawMenu(): Unit = {
    stage.scene = new Scene(MainMenu.getRoot)
    stage.resizable = false
    stage.maximized = false
    stage.title = gameTitle
    stage.show()
  }
}
