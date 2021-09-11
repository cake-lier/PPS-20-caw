package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.FakeCell
import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.geometry.Pos
import javafx.fxml.FXML
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{GridPane, Pane}

object Board {

  def apply(
      gameWidht: Int,
      gameHeight: Int,
      cells: Set[FakeCell]
  ): ViewComponent[GridPane] =
    new BoardImpl(gameWidht, gameHeight, cells)

  private final class BoardImpl(
      gameWidth: Int,
      gameHeight: Int,
      cells: Set[FakeCell]
  ) extends AbstractViewComponent[GridPane](fxmlFileName = "board.fxml") {

    override val innerComponent: GridPane = loader.load[GridPane]
    innerComponent.setGridLinesVisible(true)
    private val wallRowCol: Int = 2
    private val numRow: Int = gameHeight + wallRowCol
    private val numCol: Int = gameWidth + wallRowCol
    private val boardHeight: Double = 500
    private val boardWidth: Double = boardHeight / numRow * numCol
    private val cellSize = boardHeight / numRow
    setUpBoard()
    placePavement()
    placeWall()
    placeCells()

    private def setUpBoard(): Unit = {
      innerComponent.setPrefHeight(boardHeight)
      innerComponent.setPrefWidth(boardWidth)
    }

    private def placePavement(): Unit = {
      for (x <- 1 until numCol - 1; y <- 1 until numRow - 1) {
        val pane = new Pane() {
          getChildren.add(new ImageView(new Image("imgs/default.png")) {
            setFitWidth(cellSize)
            setFitHeight(cellSize)
          })
        }
        addDropHandling(pane)
        innerComponent.add(pane, x, y)
      }
    }

    private def placeWall(): Unit = {
      for (x <- 0 until numCol; y <- Set(0, numRow - 1)) {
        innerComponent.add(Cell("wall.png", cellSize, x, y).innerComponent, x, y)
      }
      for (x <- Set(0, numCol - 1); y <- 1 until numRow - 1) {
        innerComponent.add(Cell("wall.png", cellSize, x, y).innerComponent, x, y)
        innerComponent.add(new Pane(), x, y)
      }
    }

    import javafx.scene.input.DragEvent
    import javafx.scene.input.Dragboard
    import javafx.scene.input.TransferMode

    private def addDropHandling(tile: Pane): Unit = {
      tile.setOnDragDropped(e => {
        val db = e.getDragboard
        if (db.hasString) {
          val coordinates = db.getString.split("\\s+")
          removeAndReplace(tile, coordinates(0).toDouble, coordinates(1).toDouble, coordinates(2))
          e.setDropCompleted(true);
          e.consume()
        }
      })

      tile.setOnDragOver(e => {
        if (!e.getGestureSource.equals(tile) && e.getDragboard.hasString) {
          e.acceptTransferModes(TransferMode.MOVE)
          e.consume()
        }
      })
    }

    private def removeAndReplace(tile: Pane, x: Double, y: Double, background: String): Unit = {
      val node = innerComponent.getChildren
        .stream()
        .filter(n =>
          (GridPane.getRowIndex(n).toDouble == y && GridPane.getColumnIndex(n).toDouble == x && n.isInstanceOf[ImageView])
        )
        .findAny()
        .get()
      innerComponent.getChildren.remove(node)

      val newX = GridPane.getColumnIndex(tile).toDouble
      val newY = GridPane.getRowIndex(tile).toDouble
      innerComponent.add(
        Cell(
          background,
          cellSize,
          newX,
          newY
        ).innerComponent,
        newX.toInt,
        newY.toInt
      )
    }

    private def placeCells() = {
      cells.foreach(f =>
        innerComponent.add(
          Cell(f.typeOfCell + ".png", cellSize, f.xCoor, f.yCoor).innerComponent,
          f.xCoor,
          f.yCoor
        )
      )
    }
  }
}
