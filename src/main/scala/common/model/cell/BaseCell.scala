package it.unibo.pps.caw.common.model.cell

import it.unibo.pps.caw.common.model.Position

/** A base cell, with no added properties with respect to the original [[Cell]] type. */
sealed trait BaseCell extends Cell

/** A [[RotatorCell]] which is also a [[BaseCell]]. */
sealed trait BaseRotatorCell extends RotatorCell with BaseCell

/** Compation object of the [[BaseRotatorCell]] trait, containing its utility methods. */
object BaseRotatorCell {

  /* Default implementation of the BaseRotatorCell trait. */
  private case class BaseRotatorCellImpl(position: Position, rotation: Rotation) extends BaseRotatorCell

  /** Returns a new instance of the [[BaseRotatorCell]] trait given its [[Position]] and its [[Rotation]].
    *
    * @param position
    *   the [[Position]] of the [[BaseRotatorCell]] to create
    * @param rotation
    *   the [[Rotation]] of the [[BaseRotatorCell]] to create
    * @return
    *   a new [[BaseRotatorCell]] instance
    */
  def apply(position: Position, rotation: Rotation): BaseRotatorCell = BaseRotatorCellImpl(position, rotation)

  /** Extracts the [[Position]] and [[Rotation]] properties from the given instance of [[BaseRotatorCell]].
    *
    * @param cell
    *   the [[BaseRotatorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]] and [[Rotation]] properties
    */
  def unapply(cell: BaseRotatorCell): (Position, Rotation) = (cell.position, cell.rotation)
}

/** A [[GeneratorCell]] which is also a [[BaseCell]]. */
sealed trait BaseGeneratorCell extends GeneratorCell with BaseCell

/** Compation object of the [[BaseGeneratorCell]] trait, containing its utility methods. */
object BaseGeneratorCell {

  /* Default implementation of the BaseGeneratorCell trait. */
  private case class BaseGeneratorCellImpl(position: Position, orientation: Orientation) extends BaseGeneratorCell

  /** Returns a new instance of the [[BaseGeneratorCell]] trait given its [[Position]] and its [[Orientation]].
    *
    * @param position
    *   the [[Position]] of the [[BaseGeneratorCell]] to create
    * @param orientation
    *   the [[Orientation]] of the [[BaseGeneratorCell]] to create
    * @return
    *   a new [[BaseGeneratorCell]] instance
    */
  def apply(position: Position, orientation: Orientation): BaseGeneratorCell = BaseGeneratorCellImpl(position, orientation)

  /** Extracts the [[Position]] and [[Orientation]] properties from the given instance of [[BaseGeneratorCell]].
    *
    * @param cell
    *   the [[BaseGeneratorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]] and [[Orientation]] properties
    */
  def unapply(cell: BaseGeneratorCell): (Position, Orientation) = (cell.position, cell.orientation)
}

/** An [[EnemyCell]] which is also a [[BaseCell]]. */
sealed trait BaseEnemyCell extends EnemyCell with BaseCell

/** Compation object of the [[BaseEnemyCell]] trait, containing its utility methods. */
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

/** Compation object of the [[BaseMoverCell]] trait, containing its utility methods. */
object BaseMoverCell {

  /* Default implementation of the BaseMoverCell trait. */
  private case class BaseMoverCellImpl(position: Position, orientation: Orientation) extends BaseMoverCell

  /** Returns a new instance of the [[BaseMoverCell]] trait given its [[Position]] and its [[Orientation]].
    *
    * @param position
    *   the [[Position]] of the [[BaseMoverCell]] to create
    * @param orientation
    *   the [[Orientation]] of the [[BaseMoverCell]] to create
    * @return
    *   a new [[BaseMoverCell]] instance
    */
  def apply(position: Position, orientation: Orientation): BaseMoverCell = BaseMoverCellImpl(position, orientation)

  /** Extracts the [[Position]] and [[Orientation]] properties from the given instance of [[BaseMoverCell]].
    *
    * @param cell
    *   the [[BaseMoverCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]] and [[Orientation]] properties
    */
  def unapply(cell: BaseMoverCell): (Position, Orientation) = (cell.position, cell.orientation)
}

/** A [[BlockCell]] which is also a [[BaseCell]]. */
sealed trait BaseBlockCell extends BlockCell with BaseCell

/** Compation object of the [[BaseBlockCell]] trait, containing its utility methods. */
object BaseBlockCell {

  /* Default implementation of the BaseBlockCell trait. */
  private case class BaseBlockCellImpl(position: Position, push: Push) extends BaseBlockCell

  /** Returns a new instance of the [[BaseBlockCell]] trait given its [[Position]] and its [[Push]] direction.
    *
    * @param position
    *   the [[Position]] of the [[BaseBlockCell]] to create
    * @param push
    *   the [[Push]] direction of the [[BaseBlockCell]] to create
    * @return
    *   a new [[BaseBlockCell]] instance
    */
  def apply(position: Position, push: Push): BaseBlockCell = BaseBlockCellImpl(position, push)

  /** Extracts the [[Position]] and the [[Push]] direction properties from the given instance of [[BaseBlockCell]].
    *
    * @param cell
    *   the [[BaseBlockCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]] and the [[Push]] direction properties
    */
  def unapply(cell: BaseBlockCell): (Position, Push) = (cell.position, cell.push)
}

/** A [[WallCell]] which is also a [[BaseCell]]. */
sealed trait BaseWallCell extends WallCell with BaseCell

/** Compation object of the [[BaseWallCell]] trait, containing its utility methods. */
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
