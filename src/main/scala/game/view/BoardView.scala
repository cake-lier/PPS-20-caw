package it.unibo.pps.caw.game.view

import it.unibo.pps.caw.game.model.{Cell, Level}
import it.unibo.pps.caw.ViewComponent
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints}
import scala.jdk.StreamConverters.given

trait BoardView extends ViewComponent[GridPane] {
  def updateBoard(updatedLevel: Level): Unit
}

/** Factory for new [[Board]] instance. */
object BoardView {

  /** Creates a new board component.
    * @param levelInfo
    *   the [[Level]] containing all the board's information to be drawn
    */
  def apply(level: Level): BoardView = BoardViewImpl(level)

  /** Implementation of the Board. */
  private class BoardViewImpl(initialLevel: Level)
    extends AbstractViewComponent[GridPane](fxmlFileName = "board.fxml")
    with BoardView
    with DragAndDrop {

    override val innerComponent: GridPane = loader.load[GridPane]
    private var boardWidth:Double = innerComponent.getPrefWidth
    private var boardHeight:Double = innerComponent.getPrefHeight

    if (initialLevel.width / initialLevel.height >= 2) {
      boardHeight = (boardWidth/initialLevel.width) * initialLevel.height
    } else {
      boardWidth = (boardWidth/initialLevel.height) * initialLevel.width
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
    drawLevel(initialLevel)

    //TODO change to new type of cell
    def updateBoard(updatedlevel: Level): Unit = {
      innerComponent.getChildren().clear()
      placePavement()
      drawLevel(updatedlevel)
    }

    /* Places the default tiles in the grid. */
    private def placePavement(): Unit = {
      for {
        x <- 0 until innerComponent.getColumnConstraints.size()
        y <- 0 until innerComponent.getRowConstraints.size()
      } innerComponent.add(TileView(CellImage.DefaultTile.image, innerComponent).innerComponent, x, y)
    }

    /* Adds the playable area and cells. */
    private def drawLevel(level: Level): Unit = {
      val playableArea = level.playableArea
      for {
        x <- 0 until playableArea.width
        y <- 0 until playableArea.height
      } do {
        val node = TileView(CellImage.PlayAreaTile.image, innerComponent).innerComponent
        addDropFeature(node, innerComponent)
        innerComponent.add(node, playableArea.position.x + x, playableArea.position.y + y)
      }
      level.cells.foreach(c => {
        val node = CellView(c, innerComponent).innerComponent
        if (c.playable) {
          addDragFeature(node)
        }
        innerComponent.add(node, c.position.x, c.position.y)
      })
    }
  }
}
