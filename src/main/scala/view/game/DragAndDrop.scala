package it.unibo.pps.caw
package view.game

import it.unibo.pps.caw.model.Position
import it.unibo.pps.caw.view.ViewComponent
import javafx.scene.image.ImageView
import javafx.scene.input.{ClipboardContent, DataFormat, Dragboard, TransferMode}
import javafx.scene.Node
import javafx.scene.layout.GridPane

trait DragAndDrop {

  def addDragFeature(node:ImageView) = {
    node.setOnDragDetected(e => {
      val db = node.startDragAndDrop(TransferMode.MOVE)
      val content = new ClipboardContent()
      content.putImage(node.getImage)
      db.setContent(content)
      e.consume()
    });
  }

  def addDropFeature(node:Node, gridPane: GridPane): Unit = {
    node.setOnDragDropped(e => {
      val db = e.getDragboard
      if (db.hasImage) {
        removeAndReplace(gridPane, node, e.getGestureSource)
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
}

private def removeAndReplace(gridPane: GridPane, tile: Node, cell:AnyRef): Unit = {
  val node = gridPane.getChildren
    .stream()
    .filter(_.equals(cell))
    .findAny()
    .get()
  gridPane.getChildren.remove(node)

  val newX = GridPane.getColumnIndex(tile).toInt
  val newY = GridPane.getRowIndex(tile).toInt
  gridPane.add(
    node,
    newX.toInt,
    newY.toInt
  )
}
