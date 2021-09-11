package it.unibo.pps.caw
package view.game

import view.ViewComponent
import view.ViewComponent.AbstractViewComponent

import it.unibo.pps.caw.FakeCell
import javafx.fxml.FXML
import javafx.geometry.{HPos, Insets}
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}

object Game {

  /** Creates a main menu component. */
  def apply(): ViewComponent[GridPane] = new GameImpl()

  /** Implementation of the MainMenu. */
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
    innerComponent.setGridLinesVisible(true)
    createBoard()

    //buttons controls
    resetButton.setOnMouseClicked(_ => println("RESET CLICKED"))
    stepSimulationButton.setOnMouseClicked(_ => println("STEP CLICKED"))
    playSimulationButton.setOnMouseClicked(_ => println("PLAY CLICKED"))
    backToLevelsButton.setOnMouseClicked(_ => println("BACK CLICKED"))
    nextButton.setVisible(true)
    nextButton.setOnMouseClicked(_ => println("NEXT CLICKED"))

    private def createBoard(): Unit = {
      val board: GridPane = Board(
        8,
        8,
        Set(new FakeCell("push_right", 5, 3), new FakeCell("enemy", 6, 6))
      ).innerComponent
      GridPane.setHalignment(board, HPos.CENTER)
      GridPane.setMargin(board, new Insets(25, 0, 25, 0))
      innerComponent.add(board, 2, 3, 3, 1)
    }
  }
}
