package it.unibo.pps.caw
package game.view

import it.unibo.pps.caw.game.model.Position
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.layout.GridPane

trait ModelUpdater {
  def updateCell(oldPosition:Position, newPosition:Position):Unit
}

/** Provides the necessary functions to drag and drop an ImageView. */
trait DragAndDrop {

  def addDragFeature(node: ImageView) = {
    node.setOnDragDetected(e => {
      val db = node.startDragAndDrop(TransferMode.MOVE)
      val content = new ClipboardContent()
      content.putImage(node.getImage)
      db.setContent(content)
      e.consume()
    });
  }

  def addDropFeature(node: Node, gridPane: GridPane, model: ModelUpdater): Unit = {
    node.setOnDragDropped(e => {
      val db = e.getDragboard
      if (db.hasImage) {
        removeAndReplace(gridPane, node, e.getGestureSource, model)
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


  /** Removes a cell from the gridpane and replace it with same cell but in a new position.
    * @param gridPane
    *   the [[GridPane]] that containes the [[CellView]]
    * @param tile
    *   the [[TileView]] where the [[cell]] was dropped
    * @param cell
    *   the [[CellView]] to be removed and re-inserted in the [[gridPane]] with a new position
    */
  private def removeAndReplace(gridPane: GridPane, tile: Node, cell: AnyRef, model: ModelUpdater): Unit = {
    val node = gridPane.getChildren
      .stream()
      .filter(_.equals(cell))
      .findAny()
      .get()

    val newX = GridPane.getColumnIndex(tile).toInt
    val newY = GridPane.getRowIndex(tile).toInt
    
    model.updateCell((GridPane.getColumnIndex(node).toInt, GridPane.getRowIndex(node).toInt), (newX, newY))

    gridPane.getChildren.remove(node)
    gridPane.add(
      node,
      newX.toInt,
      newY.toInt
    )
  }
}
