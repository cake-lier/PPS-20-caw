package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.scene.image.{Image, ImageView}

object TileView {
  def apply(cellBackground: String, size: Double): ViewComponent[ImageView] =
    new CellImpl(cellBackground, size)

  private final class CellImpl(cellBackground: String, size: Double)
    extends AbstractViewComponent[ImageView](fxmlFileName = "cell.fxml") {

    override val innerComponent: ImageView = loader.load[ImageView]
    innerComponent.setImage(new Image("imgs/" + cellBackground))
    innerComponent.setFitWidth(size)
    innerComponent.setFitHeight(size)
  }
}
