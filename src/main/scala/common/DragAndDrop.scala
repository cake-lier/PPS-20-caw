package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.Position
import it.unibo.pps.caw.editor.model.SetupCell
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.layout.GridPane

trait ModelUpdater {
  def manageCell(cell: ImageView, newPosition: Position): Unit
}

/** Provides the necessary functions to drag and drop an ImageView. */
object DragAndDrop {

  def addDragFeature(node: ImageView) = {
    node.setOnDragDetected(e => {
      val content = new ClipboardContent()
      content.putImage(node.getImage)
      node.startDragAndDrop(TransferMode.MOVE).setContent(content)
      e.consume()
    })
  }

  def addDropFeature(node: Node, model: ModelUpdater): Unit = {
    node.setOnDragDropped(e => {
      if (e.getDragboard.hasImage) {
        model.manageCell(
          e.getGestureSource.asInstanceOf[ImageView],
          Position(GridPane.getColumnIndex(node), GridPane.getRowIndex(node))
        )
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
