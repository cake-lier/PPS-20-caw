package it.unibo.pps.caw
package view

import view.ViewComponent.AbstractViewComponent

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}

import java.io.File

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

    // Read levels
    val dir = new File(ClassLoader.getSystemResource("levels/").toURI)
    var levels: List[Level] = dir.listFiles()
            .filter(_.getName.endsWith(".json")).toList
            .zipWithIndex.map((file, i) => Level(i)(file))

    // Draw level buttons
    var col = 1
    var row = 3
    levels.foreach(l => {
      levelSelectionGridPane.add(LevelButton(l.number).innerComponent, col, row)
      if (col + 1 > 10) {
        col = 1
        row += 2
        //TODO slider for rows > 7
      } else col += 1
    })
  }
}
