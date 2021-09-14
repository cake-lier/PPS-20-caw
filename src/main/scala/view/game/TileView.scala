package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.scene.layout.Pane
import javafx.scene.image.{Image, ImageView}

object TileView {
  def apply(tileImage:Image): ViewComponent[Pane] =
    new CellImpl(tileImage)

  private final class CellImpl(tileImage: Image)
    extends ViewComponent[Pane]{

    override val innerComponent = new Pane()
    private val image: ImageView = new ImageView()
    image.setImage(tileImage)
    image.fitWidthProperty.bind(innerComponent.widthProperty())
    image.fitHeightProperty.bind(innerComponent.heightProperty())
    innerComponent.getChildren.add(image)
  }
}
