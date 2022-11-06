package it.unibo.pps.caw
package common.model.cell

import common.model.Position

/** A base cell, with no added properties with respect to the original [[Cell]] type. */
sealed trait BaseCell extends Cell

/** Companion object to the [[BaseCell]] trait, containing utility methods. */
object BaseCell {

  /** Contains all extensions methods for the [[BaseCell]] trait. */
  extension (cell: BaseCell) {

    /** Changes the [[BaseCell.position]] property of this [[BaseCell]] with the result of the given function. The function may
      * depend on the [[BaseCell]] on which the property is going to be changed.
      *
      * @param getPosition
      *   the function used for getting the [[Position]] to set to this [[BaseCell]]
      * @return
      *   this [[BaseCell]] with its [[Position]] changed according to the result of the given function
      */
    def changePositionProperty(getPosition: BaseCell => Position): BaseCell = cell match {
      case _: BaseWallCell         => BaseWallCell(getPosition(cell))
      case _: BaseEnemyCell        => BaseEnemyCell(getPosition(cell))
      case BaseRotatorCell(r, _)   => BaseRotatorCell(r)(getPosition(cell))
      case BaseGeneratorCell(o, _) => BaseGeneratorCell(o)(getPosition(cell))
      case BaseMoverCell(o, _)     => BaseMoverCell(o)(getPosition(cell))
      case BaseBlockCell(p, _)     => BaseBlockCell(p)(getPosition(cell))
      case _: BaseDeleterCell      => BaseDeleterCell(getPosition(cell))
    }
  }
}

/** A [[RotatorCell]] which is also a [[BaseCell]]. */
sealed trait BaseRotatorCell extends RotatorCell with BaseCell

/** Companion object of the [[BaseRotatorCell]] trait, containing its utility methods. */
object BaseRotatorCell {

  /* Default implementation of the BaseRotatorCell trait. */
  private case class BaseRotatorCellImpl(rotation: Rotation, position: Position) extends BaseRotatorCell

  /** Returns a new instance of the [[BaseRotatorCell]] trait given its [[Rotation]] and its [[Position]].
    *
    * @param rotation
    *   the [[Rotation]] of the [[BaseRotatorCell]] to create
    * @param position
    *   the [[Position]] of the [[BaseRotatorCell]] to create
    * @return
    *   a new [[BaseRotatorCell]] instance
    */
  def apply(rotation: Rotation)(position: Position): BaseRotatorCell = BaseRotatorCellImpl(rotation, position)

  /** Extracts the [[Rotation]] and [[Position]] properties from the given instance of [[BaseRotatorCell]].
    *
    * @param cell
    *   the [[BaseRotatorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]] and [[Rotation]] properties
    */
  def unapply(cell: BaseRotatorCell): (Rotation, Position) = (cell.rotation, cell.position)
}

/** A [[GeneratorCell]] which is also a [[BaseCell]]. */
sealed trait BaseGeneratorCell extends GeneratorCell with BaseCell

/** Companion object of the [[BaseGeneratorCell]] trait, containing its utility methods. */
object BaseGeneratorCell {

  /* Default implementation of the BaseGeneratorCell trait. */
  private case class BaseGeneratorCellImpl(orientation: Orientation, position: Position) extends BaseGeneratorCell

  /** Returns a new instance of the [[BaseGeneratorCell]] trait given its [[Orientation]] and its [[Position]].
    *
    * @param orientation
    *   the [[Orientation]] of the [[BaseGeneratorCell]] to create
    * @param position
    *   the [[Position]] of the [[BaseGeneratorCell]] to create
    * @return
    *   a new [[BaseGeneratorCell]] instance
    */
  def apply(orientation: Orientation)(position: Position): BaseGeneratorCell = BaseGeneratorCellImpl(orientation, position)

  /** Extracts the [[Orientation]] and [[Position]] properties from the given instance of [[BaseGeneratorCell]].
    *
    * @param cell
    *   the [[BaseGeneratorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Orientation]] and [[Position]] properties
    */
  def unapply(cell: BaseGeneratorCell): (Orientation, Position) = (cell.orientation, cell.position)
}

/** An [[EnemyCell]] which is also a [[BaseCell]]. */
sealed trait BaseEnemyCell extends EnemyCell with BaseCell

/** Companion object of the [[BaseEnemyCell]] trait, containing its utility methods. */
object BaseEnemyCell {

  /* Default implementation of the BaseEnemyCell trait. */
  private case class BaseEnemyCellImpl(position: Position) extends BaseEnemyCell

  /** Returns a new instance of the [[BaseEnemyCell]] trait given its [[Position]].
    *
    * @param position
    *   the [[Position]] of the [[BaseEnemyCell]] to create
    * @return
    *   a new [[BaseEnemyCell]] instance
    */
  def apply(position: Position): BaseEnemyCell = BaseEnemyCellImpl(position)

  /** Extracts the [[Position]] property from the given instance of [[BaseEnemyCell]].
    *
    * @param cell
    *   the [[BaseEnemyCell]] from which extracting the property
    * @return
    *   a tuple containing the [[Position]] property
    */
  def unapply(cell: BaseEnemyCell): Tuple1[Position] = Tuple1(cell.position)
}

/** A [[MoverCell]] which is also a [[BaseCell]]. */
sealed trait BaseMoverCell extends MoverCell with BaseCell

/** Companion object of the [[BaseMoverCell]] trait, containing its utility methods. */
object BaseMoverCell {

  /* Default implementation of the BaseMoverCell trait. */
  private case class BaseMoverCellImpl(orientation: Orientation, position: Position) extends BaseMoverCell

  /** Returns a new instance of the [[BaseMoverCell]] trait given its [[Orientation]] and its [[Position]].
    *
    * @param orientation
    *   the [[Orientation]] of the [[BaseMoverCell]] to create
    * @param position
    *   the [[Position]] of the [[BaseMoverCell]] to create
    * @return
    *   a new [[BaseMoverCell]] instance
    */
  def apply(orientation: Orientation)(position: Position): BaseMoverCell = BaseMoverCellImpl(orientation, position)

  /** Extracts the [[Orientation]] and [[Position]] properties from the given instance of [[BaseMoverCell]].
    *
    * @param cell
    *   the [[BaseMoverCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Orientation]] and [[Position]] properties
    */
  def unapply(cell: BaseMoverCell): (Orientation, Position) = (cell.orientation, cell.position)
}

/** A [[BlockCell]] which is also a [[BaseCell]]. */
sealed trait BaseBlockCell extends BlockCell with BaseCell

/** Companion object of the [[BaseBlockCell]] trait, containing its utility methods. */
object BaseBlockCell {

  /* Default implementation of the BaseBlockCell trait. */
  private case class BaseBlockCellImpl(push: Push, position: Position) extends BaseBlockCell

  /** Returns a new instance of the [[BaseBlockCell]] trait given its [[Push]] direction and its [[Position]].
    *
    * @param push
    *   the [[Push]] direction of the [[BaseBlockCell]] to create
    * @param position
    *   the [[Position]] of the [[BaseBlockCell]] to create
    * @return
    *   a new [[BaseBlockCell]] instance
    */
  def apply(push: Push)(position: Position): BaseBlockCell = BaseBlockCellImpl(push, position)

  /** Extracts the [[Position]] and the [[Push]] direction properties from the given instance of [[BaseBlockCell]].
    *
    * @param cell
    *   the [[BaseBlockCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Push]] direction and the [[Position]] properties
    */
  def unapply(cell: BaseBlockCell): (Push, Position) = (cell.push, cell.position)
}

/** A [[WallCell]] which is also a [[BaseCell]]. */
sealed trait BaseWallCell extends WallCell with BaseCell

/** Companion object of the [[BaseWallCell]] trait, containing its utility methods. */
object BaseWallCell {

  /* Default implementation of the BaseWallCell trait. */
  private case class BaseWallCellImpl(position: Position) extends BaseWallCell

  /** Returns a new instance of the [[BaseWallCell]] trait given its [[Position]].
    *
    * @param position
    *   the [[Position]] of the [[BaseWallCell]] to create
    * @return
    *   a new [[BaseWallCell]] instance
    */
  def apply(position: Position): BaseWallCell = BaseWallCellImpl(position)

  /** Extracts the [[Position]] property from the given instance of [[BaseWallCell]].
    *
    * @param cell
    *   the [[BaseWallCell]] from which extracting the property
    * @return
    *   a tuple containing the [[Position]] property
    */
  def unapply(cell: BaseWallCell): Tuple1[Position] = Tuple1(cell.position)
}

/** A [[DeleterCell]] which is also a [[BaseCell]]. */
sealed trait BaseDeleterCell extends DeleterCell with BaseCell

/** Companion object of the [[BaseDeleterCell]] trait, containing its utility methods. */
object BaseDeleterCell {

  /* Default implementation of the BaseDeleterCell trait. */
  private case class BaseDeleterCellImpl(position: Position) extends BaseDeleterCell

  /** Returns a new instance of the [[BaseDeleterCell]] trait given its [[Position]].
    *
    * @param position
    *   the [[Position]] of the [[BaseDeleterCell]] to create
    * @return
    *   a new [[BaseDeleterCell]] instance
    */
  def apply(position: Position): BaseDeleterCell = BaseDeleterCellImpl(position)

  /** Extracts the [[Position]] property from the given instance of [[BaseDeleterCell]].
    *
    * @param cell
    *   the [[BaseDeleterCell]] from which extracting the property
    * @return
    *   a tuple containing the [[Position]] property
    */
  def unapply(cell: BaseDeleterCell): Tuple1[Position] = Tuple1(cell.position)
}
