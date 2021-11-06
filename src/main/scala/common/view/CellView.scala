package it.unibo.pps.caw.common.view

import it.unibo.pps.caw.common.model.cell.*
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.GridPane

/** The view of a [[Cell]].
  *
  * It draws the view of a cell with the correct [[javafx.scene.image.Image]]. Depending on the cell to be drawn, the view can be
  * draggable or not, that is the view can be moved to a different position.
  */
trait CellView extends ViewComponent[ImageView]

/** Factory for a new [[CellView]] instance. */
object CellView {

  /** Creates a new [[CellView]] component for either a [[PlayableCell]] or a [[BaseCell]].
    * @param cell
    *   the [[PlayableCell]] o the [[BaseCell]] to be drawn
    * @param board
    *   the [[GridPane]] in which the cell will be drawn
    * @return
    *   a new instance of [[CellView]]
    */
  def apply(cell: BaseCell | PlayableCell, board: GridPane): CellView = cell match {
    case b: BaseCell     => BaseCellView(b, board)
    case p: PlayableCell => PlayableCellView(p, board)
  }

  /* Abstract implementation of CellView trait to factorize common behaviour. */
  private abstract class AbstractCellView(gridPane: GridPane) extends CellView {
    override val innerComponent: ImageView = getImageView

    innerComponent.fitWidthProperty().bind(gridPane.heightProperty().divide(gridPane.getRowConstraints.size()))
    innerComponent.setPreserveRatio(true)
    innerComponent.setImage(getImage)

    protected def getImage: Image
    protected def getImageView: ImageView
  }

  /* Extension of AbstractCellView for BaseCells that can not be moved. */
  private final class BaseCellView(cell: BaseCell, gridPane: GridPane) extends AbstractCellView(gridPane) {

    override protected def getImageView: ImageView = ImageView()

    import ImagePatternMatching._
    import it.unibo.pps.caw.common.view.CellImage.*
    override protected def getImage: Image = cell match {
      case BaseRotatorCell(rotation, _)      => imageFromRotation(rotation)
      case BaseGeneratorCell(orientation, _) => imageFromGeneratorOrientation(orientation)
      case BaseMoverCell(orientation, _)     => imageFromMoverOrientation(orientation)
      case BaseBlockCell(push, _)            => imageFromPush(push)
      case _: BaseEnemyCell                  => Enemy.image
      case _: BaseWallCell                   => Wall.image
      case _: BaseDeleterCell                => Deleter.image
    }
  }

  /* Extension of AbstractCellView for a PlayableCells that can be either moved or not. */
  private final class PlayableCellView(cell: PlayableCell, gridPane: GridPane) extends AbstractCellView(gridPane) {

    override protected def getImageView: ImageView = if (cell.playable) DraggableImageView() else ImageView()

    import ImagePatternMatching._
    import it.unibo.pps.caw.common.view.CellImage.*
    override protected def getImage: Image = cell match {
      case PlayableRotatorCell(rotation, _, _)      => imageFromRotation(rotation)
      case PlayableGeneratorCell(orientation, _, _) => imageFromGeneratorOrientation(orientation)
      case PlayableMoverCell(orientation, _, _)     => imageFromMoverOrientation(orientation)
      case PlayableBlockCell(push, _, _)            => imageFromPush(push)
      case _: PlayableEnemyCell                     => Enemy.image
      case _: PlayableWallCell                      => Wall.image
      case _: PlayableDeleterCell                   => Deleter.image
    }
  }

  /* Helper object to factorize common pattern matching of a cell orientation, rotation or push. */
  private object ImagePatternMatching {
    import it.unibo.pps.caw.common.view.CellImage.*
    def imageFromMoverOrientation(orientation: Orientation): Image = orientation match {
      case Orientation.Right => MoverRight.image
      case Orientation.Down  => MoverDown.image
      case Orientation.Left  => MoverLeft.image
      case Orientation.Top   => MoverTop.image
    }

    def imageFromGeneratorOrientation(orientation: Orientation): Image = orientation match {
      case Orientation.Right => GeneratorRight.image
      case Orientation.Down  => GeneratorDown.image
      case Orientation.Left  => GeneratorLeft.image
      case Orientation.Top   => GeneratorTop.image
    }

    def imageFromPush(push: Push): Image = push match {
      case Push.Both       => Block.image
      case Push.Vertical   => BlockVertical.image
      case Push.Horizontal => BlockHorizontal.image
    }

    def imageFromRotation(rotation: Rotation) = rotation match {
      case Rotation.Clockwise        => RotatorClockwise.image
      case Rotation.Counterclockwise => RotatorCounterclockwise.image
    }
  }
}
