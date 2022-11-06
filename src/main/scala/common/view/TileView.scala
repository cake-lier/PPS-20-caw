package it.unibo.pps.caw
package common.view

import common.model.Position

import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{GridPane, Pane}

/* The view of a board tile. */
private trait TileView extends ViewComponent[ImageView]

/* Companion object for the [[TileView]] trait. */
private object TileView {

  /* Creates a new [[TileView]] component. */
  def apply(tileImage: Image, board: GridPane, droppable: Boolean, dropHandler: (ImageView, Position) => Unit): TileView =
    TileViewImpl(tileImage, board, droppable, dropHandler)

  /* Default implementation of the TileView trait. */
  private final class TileViewImpl(
    tileImage: Image,
    gridPane: GridPane,
    droppable: Boolean,
    dropHandler: (ImageView, Position) => Unit
  ) extends TileView {
    override val innerComponent: ImageView = if (droppable) DroppableImageView(dropHandler) else ImageView()
    innerComponent.fitWidthProperty().bind(gridPane.heightProperty().divide(gridPane.getRowConstraints.size()))
    innerComponent.setPreserveRatio(true)
    innerComponent.setImage(tileImage)
  }
}
