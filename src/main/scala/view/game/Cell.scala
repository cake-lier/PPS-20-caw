package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.fxml.FXML
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{ClipboardContent, TransferMode}

object Cell {
  def apply(cellBackground: String, size: Double, x: Double, y: Double): ViewComponent[ImageView] =
    new CellImpl(cellBackground, size, x, y)

  private final class CellImpl(cellBackground: String, size: Double, x: Double, y: Double)
      extends AbstractViewComponent[ImageView](fxmlFileName = "cell.fxml") {
    private var xCoord = x
    private var yCoord = y

    override val innerComponent: ImageView = loader.load[ImageView]
    innerComponent.setImage(new Image("imgs/" + cellBackground))
    innerComponent.setFitWidth(size)
    innerComponent.setFitHeight(size)

    innerComponent.setOnDragDetected(e => {
      import javafx.scene.input.Dragboard
      import javafx.scene.input.TransferMode
      val db = innerComponent.startDragAndDrop(TransferMode.MOVE)
      val content = new ClipboardContent()
      content.putImage(new Image("imgs/" + cellBackground))
      content.putString(s"$x $y $cellBackground")
      db.setContent(content)
      e.consume()
    });
  }
}
