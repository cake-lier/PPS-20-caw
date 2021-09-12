package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.model.{Level, Position, WallCell}
import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.geometry.Pos
import javafx.fxml.FXML
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{GridPane, Pane}

object Board {

  def apply(levelInfo:Level): ViewComponent[GridPane] = new BoardImpl(levelInfo)

  private final class BoardImpl(
    levelInfo: Level) extends AbstractViewComponent[GridPane](fxmlFileName = "board.fxml") with DragAndDrop {

    override val innerComponent: GridPane = loader.load[GridPane]
    private val defaultImage:String = "default.png"
    private val playableImage:String = "play_area.png"
    private val boardHeight: Double = 500
    private val boardWidth: Double = boardHeight / levelInfo.width * levelInfo.height
    private val cellSize = boardHeight / levelInfo.width
    setUpBoard()
    placePavement()
    placeWall()
    drawLevel()

    private def setUpBoard(): Unit = {
      innerComponent.setPrefHeight(boardHeight)
      innerComponent.setPrefWidth(boardWidth)
    }

    private def placePavement(): Unit = {
      for (x <- 1 until levelInfo.height - 1; y <- 1 until levelInfo.width - 1) {
        innerComponent.add(TileView(defaultImage, cellSize).innerComponent, x, y)
      }
    }

    private def placeWall(): Unit = {
      for (x <- 0 until levelInfo.height; y <- Set(0, levelInfo.width - 1)) {
        innerComponent.add(CellView(WallCell(Position(x, y), false), cellSize).innerComponent, x, y)
      }
      for (x <- Set(0, levelInfo.height - 1); y <- 1 until levelInfo.width - 1) {
        innerComponent.add(CellView(WallCell(Position(x, y), false), cellSize).innerComponent, x, y)
      }
    }

    private def drawLevel() = {
      val playableArea = levelInfo.playableArea
      for (x <- playableArea.position.x to playableArea.width; y <- playableArea.position.y to playableArea.height) {
        val node = TileView(playableImage, cellSize).innerComponent
        addDropFeature(node, innerComponent)
        innerComponent.add(node, x, y)
      }
      levelInfo.cells.foreach(c => {
        val node = CellView(c, cellSize).innerComponent
        if (c.playable) {
          addDragFeature(node)
        }
        innerComponent.add(node, c.position.x, c.position.y)
      })
    }
  }
}
