package it.unibo.pps.caw.common

import javafx.scene.image.ImageView
import javafx.scene.input.{ClipboardContent, TransferMode}

class DraggableImageView extends ImageView {
  super.setOnDragDetected(e => {
    val content = new ClipboardContent()
    content.putImage(super.getImage)
    super.startDragAndDrop(TransferMode.MOVE).setContent(content)
    e.consume()
  })
}
