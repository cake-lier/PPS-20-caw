package it.unibo.pps.caw
package view

import view.ViewComponent.AbstractViewComponent

import javafx.fxml.FXML
import javafx.scene.control.{Button, ScrollPane}
import javafx.scene.layout.{GridPane, Pane, RowConstraints}

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
    @FXML
    var scrollablePane: GridPane = _
    @FXML
    var scrollPane: ScrollPane = _

    override val innerComponent: Pane = loader.load[GridPane]

    backButton.setOnMouseClicked(_ => backButton.getScene.setRoot(MainMenu().innerComponent))

    // Read levels
    val dir = new File(ClassLoader.getSystemResource("levels/").toURI)
    var levels: List[Level] = dir.listFiles()
            .filter(_.getName.endsWith(".json")).toList
            .zipWithIndex.map((file, i) => Level(i)(file))

    // Draw level buttons
    val rowCons1: RowConstraints = RowConstraints()
    rowCons1.setPercentHeight(2.5)
    val rowCons2: RowConstraints = RowConstraints()
    rowCons2.setPercentHeight(10.0)

    var col = 1
    var row = 0
    levels.foreach(l => {
      scrollablePane.add(LevelButton(l.number).innerComponent, col, row)
      if (col + 1 > 10) {
        col = 1
        row += 2
        if (row >= 5) {
          scrollablePane.getRowConstraints.addAll(rowCons1, rowCons2)
          scrollPane.setVmax(1) // allow scrolling
        }
      } else col += 1
    })

  }
}
