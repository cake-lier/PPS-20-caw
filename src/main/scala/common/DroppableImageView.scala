package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.model.Position
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import javafx.scene.layout.GridPane

/** An [[ImageView]] that allows other [[ImageView]] to be dropped on top of it. */
class DroppableImageView(dropHandler: (ImageView, Position) => Unit) extends ImageView {
  super.setOnDragDropped(e => {
    if (e.getDragboard.hasImage) {
      dropHandler(e.getGestureSource.asInstanceOf[ImageView], Position(GridPane.getColumnIndex(this), GridPane.getRowIndex(this)))
      e.setDropCompleted(true);
      e.consume()
    }
  })

  super.setOnDragOver(e => {
    if (!e.getGestureSource.equals(this) && e.getDragboard.hasImage) {
      e.acceptTransferModes(TransferMode.MOVE)
      e.consume()
    }
  })
}
