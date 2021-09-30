package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.{CellImage, ViewComponent, DraggableImageView}

import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane

/** The view of a [[PlayableCell]]. */
trait CellView extends ViewComponent[ImageView]

/** Factory for new [[CellView]] instance. */
object CellView {

  /** Creates a new [[CellView]] component.
    * @param cell
    *   the [[PlayableCell]] to be drawn
    * @param board
    *   the [[GridPane]] in which the cell will be drawn
    */
  def apply(cell: PlayableCell, board: GridPane): CellView = CellImpl(cell, board)

  /* Implementation of the CellView. */
  private class CellImpl(cell: PlayableCell, gridPane: GridPane) extends CellView {
    override val innerComponent: ImageView = if (cell.playable) DraggableImageView() else ImageView()
    innerComponent.fitWidthProperty().bind(gridPane.heightProperty().divide(gridPane.getRowConstraints.size()))
    innerComponent.setPreserveRatio(true)

    import CellImage._
    cell match {
      case PlayableRotatorCell(_, rotation, _) =>
        rotation match {
          case Rotation.Clockwise        => innerComponent.setImage(RotatorClockwise.image)
          case Rotation.Counterclockwise => innerComponent.setImage(RotatorCounterclockwise.image)
        }
      case PlayableGeneratorCell(_, orientation, _) =>
        orientation match {
          case Orientation.Right => innerComponent.setImage(GeneratorRight.image)
          case Orientation.Down  => innerComponent.setImage(GeneratorDown.image)
          case Orientation.Left  => innerComponent.setImage(GeneratorLeft.image)
          case Orientation.Top   => innerComponent.setImage(GeneratorTop.image)
        }
      case _: PlayableEnemyCell => innerComponent.setImage(Enemy.image)
      case PlayableMoverCell(_, orientation, _) =>
        orientation match {
          case Orientation.Right => innerComponent.setImage(MoverRight.image)
          case Orientation.Down  => innerComponent.setImage(MoverDown.image)
          case Orientation.Left  => innerComponent.setImage(MoverLeft.image)
          case Orientation.Top   => innerComponent.setImage(MoverTop.image)
        }
      case PlayableBlockCell(_, push, _) =>
        push match {
          case Push.Both       => innerComponent.setImage(Block.image)
          case Push.Vertical   => innerComponent.setImage(BlockVertical.image)
          case Push.Horizontal => innerComponent.setImage(BlockHorizontal.image)
        }
      case _: PlayableWallCell => innerComponent.setImage(Wall.image)
    }
  }
}
