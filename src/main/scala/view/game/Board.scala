package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.geometry.{HPos, VPos, Pos}
import javafx.fxml.FXML
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{ColumnConstraints, GridPane, Priority, Region, RowConstraints, StackPane, TilePane}

object Board {

  def apply(gameWidht: Int, gameHeight: Int): ViewComponent[GridPane] =
    new BoardImpl(gameWidht, gameHeight)

  private final class BoardImpl(gameWidth: Int, gameHeight: Int)
      extends AbstractViewComponent[GridPane](fxmlFileName = "board.fxml") {

    private val wallRowCol: Int = 2
    private val numRow: Int = gameHeight + wallRowCol
    private val numCol: Int = gameWidth + wallRowCol
    private val boardHeight: Int = 500
    private val boardWidth: Int = 500 / numRow * numCol
    private val cellSize = 500 / numRow
    override val innerComponent: GridPane = loader.load[GridPane]
    setUpBoard()
    placePavement()
    placeWall()

    private def setUpBoard(): Unit = {
      innerComponent.setPrefHeight(boardHeight)
      innerComponent.setPrefWidth(boardWidth)
    }

    private def placePavement(): Unit = {
      for (x <- 1 until numCol - 1; y <- 1 until numRow - 1) {
        innerComponent.add(Cell("default.png", cellSize).innerComponent, x, y)
      }
    }

    private def placeWall(): Unit = {
      for (x <- 0 until numCol; y <- Set(0, numRow - 1)) {
        innerComponent.add(Cell("wall.png", cellSize).innerComponent, x, y)
      }
      for (x <- Set(0, numCol - 1); y <- 1 until numRow - 1) {
        innerComponent.add(Cell("wall.png", cellSize).innerComponent, x, y)
      }
    }
  }
}
