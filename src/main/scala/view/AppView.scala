package it.unibo.pps.caw
package view

import game.Game
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

class AppView(stage: PrimaryStage) {
  private val gameTitle = "Cells at Work"
  private val scene = new Scene()

  def drawMenu(): Unit = {
    scene.root.value = Game().innerComponent
    stage.scene = scene
    stage.resizable = false
    stage.maximized = false
    stage.title = gameTitle
    stage.show()
  }
}
