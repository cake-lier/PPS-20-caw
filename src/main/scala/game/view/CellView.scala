package it.unibo.pps.caw.game.view

import it.unibo.pps.caw.game.model.*
import it.unibo.pps.caw.ViewComponent
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane

trait CellView extends ViewComponent[ImageView]

/** Factory for new [[CellView]] instance. */
object CellView {

  /** Creates a new cell component.
    * @param cell
    *   the [[Cell]] to be drawn
    * @param gridPane
    *   the gridpain in which the cell will be drawn
    */
  def apply(cell: Cell, gridPane: GridPane): CellView = CellImpl(cell, gridPane)

  /* Implementation of the CellView. */
  private class CellImpl(cell: Cell, gridPane: GridPane) extends CellView {
    override val innerComponent: ImageView = ImageView()
    innerComponent.fitWidthProperty().bind(gridPane.heightProperty().divide(gridPane.getRowConstraints.size()))
    innerComponent.setPreserveRatio(true)

    cell match {
      case RotatorCell(_, _, rotationDirection) =>
        rotationDirection match {
          case RotationDirection.Clockwise => innerComponent.setImage(CellImage.RotatorRight.image)
          case RotationDirection.Counterclockwise  => innerComponent.setImage(CellImage.RotatorLeft.image)
        }
      case GeneratorCell(_, _, orientation) =>
        orientation match {
          case Orientation.Right => innerComponent.setImage(CellImage.GeneratorRight.image)
          case Orientation.Down  => innerComponent.setImage(CellImage.GeneratorDown.image)
          case Orientation.Left  => innerComponent.setImage(CellImage.GeneratorLeft.image)
          case Orientation.Top   => innerComponent.setImage(CellImage.GeneratorTop.image)
        }
      case EnemyCell(_, _) => innerComponent.setImage(CellImage.Enemy.image)
      case MoverCell(_, _, orientation) =>
        orientation match {
          case Orientation.Right => innerComponent.setImage(CellImage.MoverRight.image)
          case Orientation.Down  => innerComponent.setImage(CellImage.MoverDown.image)
          case Orientation.Left  => innerComponent.setImage(CellImage.MoverLeft.image)
          case Orientation.Top   => innerComponent.setImage(CellImage.MoverTop.image)
        }
      case BlockCell(_, _, allowedMovement) =>
        allowedMovement match {
          case AllowedMovement.Both       => innerComponent.setImage(CellImage.Block.image)
          case AllowedMovement.Vertical   => innerComponent.setImage(CellImage.BlockVertical.image)
          case AllowedMovement.Horizontal => innerComponent.setImage(CellImage.BlockHorizontal.image)
        }
      case WallCell(_, _) => innerComponent.setImage(CellImage.Wall.image)
    }
  }
}
