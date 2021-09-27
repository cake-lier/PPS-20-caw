package it.unibo.pps.caw.game.view

import it.unibo.pps.caw.game.model.{BaseCell, Board, Level, Position, SetupCell}
import it.unibo.pps.caw.ViewComponent
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints}

trait ModelUpdater {
  def updateCell(oldPosition: Position, newPosition: Position): Unit
}

trait BoardView extends ViewComponent[GridPane] {
  def resetBoard(level: Level[SetupCell]): Unit

  def updateBoard(update: Level[SetupCell]): Unit
}

/** Factory for new [[Board]] instance. */
object BoardView {

  /** Creates a new board component.
    * @param levelInfo
    *   the [[Level]] containing all the board's information to be drawn
    */
  def apply(level: Level[SetupCell], model: ModelUpdater): BoardView = BoardViewImpl(level, model)

  /** Implementation of the Board. */
  private class BoardViewImpl(initialLevel: Level[SetupCell], model: ModelUpdater)
    extends AbstractViewComponent[GridPane](fxmlFileName = "board.fxml")
    with BoardView
    with DragAndDrop {

    override val innerComponent: GridPane = loader.load[GridPane]
    private var boardWidth: Double = innerComponent.getPrefWidth
    private var boardHeight: Double = innerComponent.getPrefHeight

    if (initialLevel.dimensions.width / initialLevel.dimensions.height >= 2) {
      boardHeight = (boardWidth / initialLevel.dimensions.width) * initialLevel.dimensions.height
    } else {
      boardWidth = (boardHeight / initialLevel.dimensions.height) * initialLevel.dimensions.width
    }

    innerComponent.setPrefSize(boardWidth, boardHeight)
    (0 until initialLevel.dimensions.width).foreach { _ =>
      val columnConstraints: ColumnConstraints = ColumnConstraints()
      columnConstraints.setPercentWidth(boardWidth / initialLevel.dimensions.width)
      innerComponent.getColumnConstraints.add(columnConstraints)
    }
    (0 until initialLevel.dimensions.height).foreach { _ =>
      val rowConstraints: RowConstraints = RowConstraints()
      rowConstraints.setPercentHeight(boardHeight / initialLevel.dimensions.height)
      innerComponent.getRowConstraints.add(rowConstraints)
    }

    placePavement()
    drawFromLevel(initialLevel)

    def resetBoard(level: Level[SetupCell]): Unit = {
      innerComponent.getChildren().clear()
      placePavement()
      drawFromLevel(level)
    }

    override def updateBoard(update: Level[SetupCell]): Unit = {
      innerComponent.getChildren().clear()
      placePavement()
      drawFromUpdate(update)
    }

    /* Places the default tiles in the grid. */
    private def placePavement(): Unit = {
      for {
        x <- 0 until innerComponent.getColumnConstraints.size()
        y <- 0 until innerComponent.getRowConstraints.size()
      } innerComponent.add(TileView(CellImage.DefaultTile.image, innerComponent), x, y)
    }

    /* Adds the playable area and cells. */
    private def drawFromLevel(level: Level[SetupCell]): Unit = {
      val playableArea = level.playableArea
      for {
        x <- 0 until playableArea.dimensions.width
        y <- 0 until playableArea.dimensions.height
      } do {
        val node = TileView(CellImage.PlayAreaTile.image, innerComponent)
        addDropFeature(node, innerComponent, model)
        innerComponent.add(node, playableArea.position.x + x, playableArea.position.y + y)
      }
      level
        .board
        .cells
        .foreach(c => {
          val node = CellView(c, innerComponent)
          if (c.playable) {
            addDragFeature(node)
          }
          innerComponent.add(node, c.position.x, c.position.y)
        })
    }

    private def drawFromUpdate(update: Level[SetupCell]): Unit = {
      for {
        x <- 0 until update.playableArea.dimensions.width
        y <- 0 until update.playableArea.dimensions.height
      } do {
        val node = TileView(CellImage.PlayAreaTile.image, innerComponent)
        addDropFeature(node, innerComponent, model)
        innerComponent.add(node, update.playableArea.position.x + x, update.playableArea.position.y + y)
      }
      update.board.cells.foreach(c => innerComponent.add(CellView(c, innerComponent), c.position.x, c.position.y))
    }
  }
}
