package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.Position
import it.unibo.pps.caw.editor.model.SetupCell
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.layout.GridPane

trait ModelUpdater {
  def manageCell(cell:ImageView, newPosition: Position, isInBoard:Boolean): Unit
}

/** Provides the necessary functions to drag and drop an ImageView. */
object DragAndDrop {

  def addDragFeature(node: ImageView, copy: Boolean = false) = {
    node.setOnDragDetected(e => {
      val content = new ClipboardContent()
      content.putImage(node.getImage)
      node.startDragAndDrop(TransferMode.MOVE).setContent(content)
      e.consume()
    })
  }

  def addDropFeature(node: Node, gridPane: GridPane, model: ModelUpdater): Unit = {
    node.setOnDragDropped(e => {
      if (e.getDragboard.hasImage) {
        updateGridPane(gridPane, node, e.getGestureSource.asInstanceOf[ImageView], model)
        e.setDropCompleted(true);
        e.consume()
      }
    })

    node.setOnDragOver(e => {
      if (!e.getGestureSource.equals(node) && e.getDragboard.hasImage) {
        e.acceptTransferModes(TransferMode.MOVE)
        e.consume()
      }
    })
  }

  private def isInGridPane(gridPane: GridPane, cell: AnyRef): Boolean = gridPane.getChildren.contains(cell)

  /** Removes a cell from the gridpane and replace it with same cell but in a new position.
    * @param gridPane
    *   the [[GridPane]] that containes the [[CellView]]
    * @param tile
    *   the [[TileView]] where the [[cell]] was dropped
    * @param cell
    *   the [[CellView]] to be removed and re-inserted in the [[gridPane]] with a new position
    */
  private def updateGridPane(gridPane: GridPane, tile: Node, cell: ImageView, model: ModelUpdater): Unit = {
    gridPane
      .getChildren
      .stream()
      .filter(_.equals(cell))
      .findAny()
      .ifPresentOrElse(
        node => {
          val newX = GridPane.getColumnIndex(tile).toInt
          val newY = GridPane.getRowIndex(tile).toInt

          model.manageCell(cell, (newX, newY), true)
          gridPane.getChildren.remove(node)

          gridPane.add(
            node,
            newX.toInt,
            newY.toInt
          )
        },
        () => {
          val newX = GridPane.getColumnIndex(tile).toInt
          val newY = GridPane.getRowIndex(tile).toInt
          model.manageCell(cell, (newX, newY), false)
        }
      )
  }

  private def addTile(gridPane: GridPane, tile: Node, cell: AnyRef, model: ModelUpdater): Unit = {}
}
