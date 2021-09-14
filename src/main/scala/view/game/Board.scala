package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.model.{Cell, Level, Position, WallCell}
import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.geometry.Pos
import javafx.fxml.FXML
import Images.*
import javafx.application.Platform
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{ColumnConstraints, GridPane, Pane, RowConstraints}

object Board {
  def apply(levelInfo:Level): ViewComponent[GridPane] = new BoardImpl(levelInfo)

  def updateBoard(board:GridPane, updatedCells: Set[Cell]):Unit = {
    Platform.runLater(() => {
      val cells = board.getChildren.stream()
        .filter(_.isInstanceOf[ImageView])
        .toArray()

      for (n <- 0 until cells.size) {
        board.getChildren.remove(cells(0))
      }
      updatedCells.foreach( cell => {
        board.add(CellView(cell, board).innerComponent, cell.position.x, cell.position.y)
      })
    })
  }

  private final class BoardImpl(
    levelInfo: Level) extends AbstractViewComponent[GridPane](fxmlFileName = "board.fxml") with DragAndDrop {

    override val innerComponent: GridPane = loader.load[GridPane]
    private val boardHeight: Double = 500
    private val boardWidth: Double = (boardHeight / levelInfo.height)*levelInfo.width
    private val cellSize = boardHeight / levelInfo.width
    setUpBoard()
    placePavement()
    placeWall()
    drawLevel()

    private def setUpBoard(): Unit = {
      innerComponent.setPrefSize(boardWidth, boardHeight)
      println(boardWidth)
      println(boardHeight)
      innerComponent.setGridLinesVisible(true)
      for (n <- 0 until levelInfo.width) {
        innerComponent.getColumnConstraints.add(new ColumnConstraints() {
          setPercentWidth(boardWidth/levelInfo.width)
        })
      }
      for (n <- 0 until levelInfo.height) {
        innerComponent.getRowConstraints.add(new RowConstraints() {
          setPercentHeight(boardHeight/levelInfo.height)
        })
      }
    }

    private def placePavement(): Unit = {
      for (x <- 1 until levelInfo.width - 1; y <- 1 until levelInfo.height - 1) {
        innerComponent.add(TileView(images(CellsImage.DefaultTile)).innerComponent, x, y)
      }
    }

    private def placeWall(): Unit = {
      for (x <- 0 until levelInfo.width; y <- Set(0, levelInfo.height - 1)) {
        innerComponent.add(TileView(images(CellsImage.Wall)).innerComponent, x, y)
      }
      for (x <- Set(0, levelInfo.width - 1); y <- 1 until levelInfo.height - 1) {
        innerComponent.add(TileView(images(CellsImage.Wall)).innerComponent, x, y)
      }
    }

    private def drawLevel() = {
      val playableArea = levelInfo.playableArea
      for (x <- playableArea.position.x to playableArea.width; y <- playableArea.position.y to playableArea.height) {
        val node = TileView(images(CellsImage.PlayAreaTile)).innerComponent
        addDropFeature(node, innerComponent)
        innerComponent.add(node, x, y)
      }
      levelInfo.cells.foreach(c => {
        val node = CellView(c, innerComponent).innerComponent
        if (c.playable) {
          addDragFeature(node)
        }
        GridPane.setFillWidth(node, true)
        GridPane.setFillHeight(node, true)
        innerComponent.add(node, c.position.x, c.position.y)
      })
    }
  }
}
