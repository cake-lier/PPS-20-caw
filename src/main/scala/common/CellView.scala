package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.model.cell.*

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
  def apply(cell: PlayableCell, gridPane: GridPane): CellView = CellImpl(cell, gridPane)

  /* Implementation of the CellView. */
  private class CellImpl(cell: PlayableCell, gridPane: GridPane) extends CellView {
    override val innerComponent: ImageView = ImageView()
    innerComponent.fitWidthProperty().bind(gridPane.heightProperty().divide(gridPane.getRowConstraints.size()))
    innerComponent.setPreserveRatio(true)
    cell match {
      case PlayableRotatorCell(_, rotation, _) =>
        rotation match {
          case Rotation.Clockwise        => innerComponent.setImage(CellImage.RotatorRight.image)
          case Rotation.Counterclockwise => innerComponent.setImage(CellImage.RotatorLeft.image)
        }
      case PlayableGeneratorCell(_, orientation, _) =>
        orientation match {
          case Orientation.Right => innerComponent.setImage(CellImage.GeneratorRight.image)
          case Orientation.Down  => innerComponent.setImage(CellImage.GeneratorDown.image)
          case Orientation.Left  => innerComponent.setImage(CellImage.GeneratorLeft.image)
          case Orientation.Top   => innerComponent.setImage(CellImage.GeneratorTop.image)
        }
      case _: PlayableEnemyCell => innerComponent.setImage(CellImage.Enemy.image)
      case PlayableMoverCell(_, orientation, _) =>
        orientation match {
          case Orientation.Right => innerComponent.setImage(CellImage.MoverRight.image)
          case Orientation.Down  => innerComponent.setImage(CellImage.MoverDown.image)
          case Orientation.Left  => innerComponent.setImage(CellImage.MoverLeft.image)
          case Orientation.Top   => innerComponent.setImage(CellImage.MoverTop.image)
        }
      case PlayableBlockCell(_, push, _) =>
        push match {
          case Push.Both       => innerComponent.setImage(CellImage.Block.image)
          case Push.Vertical   => innerComponent.setImage(CellImage.BlockVertical.image)
          case Push.Horizontal => innerComponent.setImage(CellImage.BlockHorizontal.image)
        }
      case _: PlayableWallCell => innerComponent.setImage(CellImage.Wall.image)
    }
  }
}
