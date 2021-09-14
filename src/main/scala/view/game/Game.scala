package it.unibo.pps.caw
package view.game

import view.ViewComponent
import view.ViewComponent.AbstractViewComponent

import it.unibo.pps.caw.model.*
import javafx.fxml.FXML
import javafx.geometry.{HPos, Insets, VPos}
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane}

object Game {

  /** Creates a Game component. */
  def apply(): ViewComponent[GridPane] = new GameImpl()

  /** Adds a new board to the Game.
    * @param gameView
    *   the [[Game]] in which will be add the [[boardView]]
    * @param boardView
    *   the board to be inserted into the [[gameView]]
    */
  def addNewBoard(gameView: GridPane, boardView: GridPane) = {
    val board = gameView.getChildren
      .stream()
      .filter(n => GridPane.getRowIndex(n).equals(3) && GridPane.getColumnIndex(n).equals(2))
      .findAny()
    if (board.isPresent) {
      gameView.getChildren.remove(board.get())
    }
    GridPane.setValignment(boardView, VPos.CENTER)
    GridPane.setHalignment(boardView, HPos.CENTER)
    GridPane.setMargin(boardView, new Insets(25, 0, 25, 0))
    gameView.add(boardView, 2, 3, 3, 1)
  }

  /** Implementation of the Game. */
  private final class GameImpl extends AbstractViewComponent[GridPane]("game.fxml") {
    @FXML
    var resetButton: Button = _
    @FXML
    var stepSimulationButton: Button = _
    @FXML
    var playSimulationButton: Button = _
    @FXML
    var backToLevelsButton: Button = _
    @FXML
    var nextButton: Button = _

    override val innerComponent: GridPane = loader.load[GridPane]

    //buttons controls
    resetButton.setOnMouseClicked(_ => println("RESET CLICKED"))
    stepSimulationButton.setOnMouseClicked(_ => println("STEP CLICKED"))
    playSimulationButton.setOnMouseClicked(_ => println("PLAY CLICKED"))
    backToLevelsButton.setOnMouseClicked(_ => println("BACK CLICKED"))
    nextButton.setVisible(true)
    nextButton.setOnMouseClicked(_ => println("NEXT CLICKED"))
  }
}
