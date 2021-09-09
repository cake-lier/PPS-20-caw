package it.unibo.pps.caw
package view

import view.ViewComponent.AbstractViewComponent

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}

/** Factory for new [[LevelSelection]] instance. */
object LevelSelection {
  /** Creates a level selection component. */
  def apply(): ViewComponent[Pane] = new LevelSelectionImpl()

  private final class LevelSelectionImpl extends AbstractViewComponent[Pane]("level_selection.fxml") {
    @FXML
    var backButton: Button = _
    @FXML
    var levelSelectionGridPane: GridPane = _

    override val innerComponent: Pane = loader.load[GridPane]

    backButton.setOnMouseClicked(_ => backButton.getScene.setRoot(MainMenu().innerComponent))
  }
}
