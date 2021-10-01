package it.unibo.pps.caw
package common

import it.unibo.pps.caw.common.model.Position
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{GridPane, Pane}

/** The view of a board tile. */
trait TileView extends ViewComponent[ImageView]

/** Factory for new [[TileView]] instance. */
object TileView {

  /** Creates a new [[TileView]] component.
    * @param tileImage
    *   the [[Image]] this [[TileView]] will have
    * @param board
    *   the [[GridPane]] in which the tile will be drawn
    * @param droppable
    *   if it's possible to drop [[ImageView]] on top of this [[TileView]]
    * @param dropHandler
    *   the handler that will be called once an [[ImageView]] is dropped on this [[TileView]]
    */
  def apply(tileImage: Image, board: GridPane, droppable: Boolean, dropHandler: (ImageView, Position) => Unit): TileView =
    TileViewImpl(tileImage, board, droppable, dropHandler)

  /* Implementation of the TileView. */
  private final class TileViewImpl(
    tileImage: Image,
    gridPane: GridPane,
    droppable: Boolean,
    dropHandler: (ImageView, Position) => Unit
  ) extends TileView {
    override val innerComponent = if (droppable) DroppableImageView(dropHandler) else ImageView()
    innerComponent.fitWidthProperty().bind(gridPane.heightProperty().divide(gridPane.getRowConstraints.size()))
    innerComponent.setPreserveRatio(true)
    innerComponent.setImage(tileImage)
  }
}
