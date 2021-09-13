package it.unibo.pps.caw.app.view

import it.unibo.pps.caw.app.view.ViewComponent.AbstractViewComponent

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
      .listFiles(_.getName.endsWith(".json"))
      .length

    // Draw level buttons
    val rowCons1, rowCons2 = RowConstraints()
    rowCons1.setPercentHeight(2.5)
    rowCons2.setPercentHeight(10.0)

    val coords = (for {
      row <- LazyList.from(0, 2)
      col <- 1 to 10
    } yield (col, row)).iterator

    (1 to numFiles).foreach(i => {
      val (col, row) = coords.next()
      if (row >= 6) {
        scrollablePane.getRowConstraints.addAll(rowCons1, rowCons2)
        scrollPane.setVmax(1) // allow scrolling
      }
      scrollablePane.add(LevelButton(i).innerComponent, col, row)
    })

  }
}
