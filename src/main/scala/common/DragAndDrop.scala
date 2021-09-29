package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.model.Position
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.input.{ClipboardContent, TransferMode}
import javafx.scene.layout.GridPane

/** Provides the necessary functions to drag and drop an ImageView. */
object DragAndDrop {
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
