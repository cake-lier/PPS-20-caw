package it.unibo.pps.caw.common.view

import it.unibo.pps.caw.common.model.cell.*
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane

/** The view of a [[Cell]]. */
trait CellView extends ViewComponent[ImageView]

/** Factory for new [[CellView]] instance. */
object CellView {

  /** Creates a new [[CellView]] component for a [[PlayableCell]].
    * @param cell
    *   the [[PlayableCell]] to be drawn
    * @param board
    *   the [[GridPane]] in which the cell will be drawn
    */
  def apply(cell: PlayableCell, board: GridPane): CellView = CellViewImpl(cell.toBaseCell, cell.playable, board)

  /** Creates a new [[CellView]] component for a [[BaseCell]].
    * @param cell
    *   the [[BaseCell]] to be drawn
    * @param board
    *   the [[GridPane]] in which the cell will be drawn
    */
  def apply(cell: BaseCell, board: GridPane): CellView = CellViewImpl(cell, draggable = false, board)

  /* Implementation of the CellViewImpl. */
  private class CellViewImpl(cell: BaseCell, draggable: Boolean, gridPane: GridPane) extends CellView {
    override val innerComponent: ImageView = if (draggable) DraggableImageView() else ImageView()

    innerComponent.fitWidthProperty().bind(gridPane.heightProperty().divide(gridPane.getRowConstraints.size()))
    innerComponent.setPreserveRatio(true)

    import it.unibo.pps.caw.common.view.CellImage.*

    cell match {
      case BaseRotatorCell(rotation, _) =>
        rotation match {
          case Rotation.Clockwise        => innerComponent.setImage(RotatorClockwise.image)
          case Rotation.Counterclockwise => innerComponent.setImage(RotatorCounterclockwise.image)
        }
      case BaseGeneratorCell(orientation, _) =>
        orientation match {
          case Orientation.Right => innerComponent.setImage(GeneratorRight.image)
          case Orientation.Down  => innerComponent.setImage(GeneratorDown.image)
          case Orientation.Left  => innerComponent.setImage(GeneratorLeft.image)
          case Orientation.Top   => innerComponent.setImage(GeneratorTop.image)
        }
      case _: BaseEnemyCell => innerComponent.setImage(Enemy.image)
      case BaseMoverCell(orientation, _) =>
        orientation match {
          case Orientation.Right => innerComponent.setImage(MoverRight.image)
          case Orientation.Down  => innerComponent.setImage(MoverDown.image)
          case Orientation.Left  => innerComponent.setImage(MoverLeft.image)
          case Orientation.Top   => innerComponent.setImage(MoverTop.image)
        }
      case BaseBlockCell(push, _) =>
        push match {
          case Push.Both       => innerComponent.setImage(Block.image)
          case Push.Vertical   => innerComponent.setImage(BlockVertical.image)
          case Push.Horizontal => innerComponent.setImage(BlockHorizontal.image)
        }
      case _: BaseWallCell    => innerComponent.setImage(Wall.image)
      case _: BaseDeleterCell => innerComponent.setImage(Deleter.image)
    }
  }
}
