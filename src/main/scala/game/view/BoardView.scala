package it.unibo.pps.caw.game.view

import it.unibo.pps.caw.game.model.{Cell, CellConverter, Level, Board}
import it.unibo.pps.caw.ViewComponent
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints}

import scala.jdk.StreamConverters

trait BoardView extends ViewComponent[GridPane] {
  def resetBoard(initialLevel: Level): Unit

  def updateBoard(level: Level, currentBoard: Board[Cell]):Unit
}

/** Factory for new [[Board]] instance. */
object BoardView {

  /** Creates a new board component.
    * @param levelInfo
    *   the [[Level]] containing all the board's information to be drawn
    */
  def apply(level: Level, model:ModelUpdater): BoardView = BoardViewImpl(level, model)

  /** Implementation of the Board. */
  private class BoardViewImpl(initialLevel: Level, model:ModelUpdater)
    extends AbstractViewComponent[GridPane](fxmlFileName = "board.fxml")
    with BoardView
    with DragAndDrop {

    override val innerComponent: GridPane = loader.load[GridPane]
    private var boardWidth:Double = innerComponent.getPrefWidth
    private var boardHeight:Double = innerComponent.getPrefHeight

    if (initialLevel.width / initialLevel.height >= 2) {
      boardHeight = (boardWidth/initialLevel.width) * initialLevel.height
    } else {
      boardWidth = (boardHeight/initialLevel.height) * initialLevel.width
    }

    innerComponent.setPrefSize(boardWidth, boardHeight)
    (0 until initialLevel.width).foreach { _ =>
      val columnConstraints: ColumnConstraints = ColumnConstraints()
      columnConstraints.setPercentWidth(boardWidth / initialLevel.width)
      innerComponent.getColumnConstraints.add(columnConstraints)
    }
    (0 until initialLevel.height).foreach { _ =>
      val rowConstraints: RowConstraints = RowConstraints()
      rowConstraints.setPercentHeight(boardHeight / initialLevel.height)
      innerComponent.getRowConstraints.add(rowConstraints)
    }

    placePavement()
    drawFromLevel(initialLevel)

    def resetBoard(initialLevel: Level): Unit = {
      innerComponent.getChildren().clear()
      placePavement()
      drawFromLevel(initialLevel)
    }

    override def updateBoard(level: Level, currentBoard: Board[Cell]): Unit = {
      innerComponent.getChildren().clear()
      placePavement()
      drawFromUpdate(level, currentBoard.cells)
    }

    /* Places the default tiles in the grid. */
    private def placePavement(): Unit = {
      for {
        x <- 0 until innerComponent.getColumnConstraints.size()
        y <- 0 until innerComponent.getRowConstraints.size()
      } innerComponent.add(TileView(CellImage.DefaultTile.image, innerComponent).innerComponent, x, y)
    }

    /* Adds the playable area and cells. */
    private def drawFromLevel(level: Level): Unit = {
      val playableArea = level.playableArea
      for {
        x <- 0 until playableArea.width
        y <- 0 until playableArea.height
      } do {
        val node = TileView(CellImage.PlayAreaTile.image, innerComponent).innerComponent
        addDropFeature(node, innerComponent, model)
        innerComponent.add(node, playableArea.position.x + x, playableArea.position.y + y)
      }
      level.setupBoard.cells.foreach(c => {
        val node = CellView(CellConverter.fromSetup(c), innerComponent).innerComponent
        if (c.playable) {
          addDragFeature(node)
        }
        innerComponent.add(node, c.position.x, c.position.y)
      })
    }

    private def drawFromUpdate(level: Level, cells: Set[Cell]): Unit = {
      val playableArea = level.playableArea
      for {
        x <- 0 until playableArea.width
        y <- 0 until playableArea.height
      } do {
        val node = TileView(CellImage.PlayAreaTile.image, innerComponent).innerComponent
        addDropFeature(node, innerComponent, model)
        innerComponent.add(node, playableArea.position.x + x, playableArea.position.y + y)
      }
      cells.foreach(c => {
        val node = CellView(c, innerComponent).innerComponent
        innerComponent.add(node, c.position.x, c.position.y)
      })
    }
  }
}
