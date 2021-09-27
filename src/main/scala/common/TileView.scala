package it.unibo.pps.caw
package common

import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{GridPane, Pane}

trait TileView extends ViewComponent[ImageView]

/** Factory for new [[CellView]] instance. */
object TileView {

  /** Creates a new cell component.
    * @param tileImage
    *   the [[Image]] this [[TileView]] will have
    */
  def apply(tileImage: Image, gridPane: GridPane): TileView = TileViewImpl(tileImage, gridPane)

  /* Implementation of the TileView. */
  private final class TileViewImpl(tileImage: Image, gridPane: GridPane) extends TileView {
    override val innerComponent = ImageView()
    innerComponent.fitWidthProperty().bind(gridPane.heightProperty().divide(gridPane.getRowConstraints.size()))
    innerComponent.setPreserveRatio(true)
    innerComponent.setImage(tileImage)
  }
}
