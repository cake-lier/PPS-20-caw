package it.unibo.pps.caw
package view

import game.{Board, Game}
import it.unibo.pps.caw.model._
import javafx.scene.layout.GridPane
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

class AppView(stage: PrimaryStage) {
  private val gameTitle = "Cells at Work"
  private val scene = new Scene()

  def drawMenu(): Unit = {
    val gameView = Game().innerComponent
    val board: GridPane = Board(
      Level(
        10,
        10,
        Set(MoverCell(Position(1, 4), true, Orientation.Right), EnemyCell(Position(4, 4), false)),
        PlayableArea(Position(1, 1), 8, 8)
      )
    ).innerComponent
    scene.root.value = gameView
    Game.addNewBoard(gameView, board)
    stage.scene = scene
    stage.resizable = false
    stage.maximized = false
    stage.title = gameTitle
    stage.show()
  }
}
