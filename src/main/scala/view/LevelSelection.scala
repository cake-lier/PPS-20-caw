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
    val numFiles = new File(ClassLoader.getSystemResource("levels/").toURI)
                        .listFiles(_.getName.endsWith(".json")).length

    // Draw level buttons
    val rowCons1, rowCons2 = RowConstraints()
    rowCons1.setPercentHeight(2.5)
    rowCons2.setPercentHeight(10.0)

    var col = 1
    var row = 0
    (1 to numFiles).foreach(i => {
      scrollablePane.add(LevelButton(i).innerComponent, col, row)
      if (col + 1 > 10) {
        col = 1
        row += 2
        if (row >= 6) {
          scrollablePane.getRowConstraints.addAll(rowCons1, rowCons2)
          scrollPane.setVmax(1) // allow scrolling
        }
      } else col += 1
    })

  }
}
