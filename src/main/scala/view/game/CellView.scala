package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.model.*
import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.fxml.FXML
import javafx.scene.image.{Image, ImageView}
import it.unibo.pps.caw.view.game.Images.CellsImage
import javafx.scene.layout.GridPane

/** Factory for new [[CellView]] instance. */
object CellView {

  /** Creates a new cell component.
    * @param cell
    *   the [[Cell]] to be drawn
    * @param gridPane
    *   the gridpain in which the cell will be drawn
    */
  def apply(cell: Cell, gridPane: GridPane): ViewComponent[ImageView] =
    new CellImpl(cell, gridPane)

  /** Implementation of the CellView. */
  private class CellImpl(cell: Cell, gridPane: GridPane) extends ViewComponent[ImageView] {
    override val innerComponent: ImageView = new ImageView()
    innerComponent.fitWidthProperty().bind(gridPane.heightProperty().divide(gridPane.getRowConstraints.size()))
    innerComponent.setPreserveRatio(true)
    setImage()

    /** Sets the correct image to the innerComponent. */
    private def setImage(): Unit = {
      import Images.CellsImage._
      cell match {
        case RotatorCell(_, _, rotationDirection) =>
          rotationDirection match {
            case RotationDirection.Right => setComponentImage(RotatorRight)
            case RotationDirection.Left  => setComponentImage(RotatorLeft)
          }
        case GeneratorCell(_, _, orientation) =>
          orientation match {
            case Orientation.Right => setComponentImage(GeneratorRight)
            case Orientation.Down  => setComponentImage(GeneratorDown)
            case Orientation.Left  => setComponentImage(GeneratorLeft)
            case Orientation.Top   => setComponentImage(GeneratorTop)
          }
        case EnemyCell(_, _) =>
          setComponentImage(Enemy)
        case MoverCell(_, _, orientation) =>
          orientation match {
            case Orientation.Right => setComponentImage(MoverRight)
            case Orientation.Down  => setComponentImage(MoverDown)
            case Orientation.Left  => setComponentImage(MoverLeft)
            case Orientation.Top   => setComponentImage(MoverTop)
          }
        case BlockCell(_, _, allowedMovement) =>
          allowedMovement match {
            case AllowedMovement.Both       => setComponentImage(Block)
            case AllowedMovement.Vertical   => setComponentImage(BlockHorizontal)
            case AllowedMovement.Horizontal => setComponentImage(BlockVertical)
          }
        case WallCell(_, _) =>
          setComponentImage(Wall)
      }
    }

    private def setComponentImage(cellType: CellsImage): Unit = {
      innerComponent.setImage(Images.images(cellType))
    }
  }
}
