package it.unibo.pps.caw
package common.view

import javafx.scene.image.ImageView
import javafx.scene.input.{ClipboardContent, TransferMode}

/** An [[javafx.scene.image.ImageView]] that can be dragged. */
class DraggableImageView extends ImageView {
  super.setOnDragDetected(e => {
    val content = new ClipboardContent()
    content.putImage(super.getImage)
    super.startDragAndDrop(TransferMode.MOVE).setContent(content)
    e.consume()
  })
}
