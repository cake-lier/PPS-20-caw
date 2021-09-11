package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.fxml.FXML
import javafx.scene.image.{Image, ImageView}

object Cell {
  def apply(cellBackground: String, size: Int): ViewComponent[ImageView] =
    new CellImpl(cellBackground, size)

  private final class CellImpl(cellBackground: String, size: Int)
      extends AbstractViewComponent[ImageView](fxmlFileName = "cell.fxml") {
    @FXML
    var cell: ImageView = _

    override val innerComponent: ImageView = loader.load[ImageView]
    innerComponent.setImage(new Image("imgs/" + cellBackground))
    innerComponent.setFitWidth(size)
    innerComponent.setFitHeight(size)
  }
}
