package it.unibo.pps.caw.app.view

import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

class AppView(stage: PrimaryStage) {
  private val gameTitle = "Cells at Work"
  private val scene = new Scene()

  def drawMenu(): Unit = {
    scene.root.value = MainMenu().innerComponent
    stage.scene = scene
    stage.resizable = false
    stage.maximized = false
    stage.title = gameTitle
    stage.show()
  }
}
