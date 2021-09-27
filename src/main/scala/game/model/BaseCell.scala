package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.{PlayableArea, Position}

/** A base cell, with no added properties with respect to the original [[Cell]] type. */
sealed trait BaseCell extends Cell

/** The rotator [[Cell]], the [[Cell]] that can rotate other [[Cell]].
  *
  * This [[Cell]] can rotate other [[Cell]] which have a specific [[Orientation]] or [[Push]] so as to make them assume another
  * value [[Orientation]] or [[Push]], depending if the [[Rotation]] is clockwise or counterclockwise. For example, under a
  * clockwise [[Rotation]], a right [[Orientation]] will become a bottom [[Orientation]], an horizontal [[Push]] a vertical one
  * and so on and so forth. Block [[Cell]] with a [[Push]] in both direction will not be affected. Under a counterclockwise
  * [[Rotation]] the vice versa is valid. It must be constructed through its companion object.
  */
sealed trait BaseRotatorCell extends RotatorCell with BaseCell

/** Compation object of the [[BaseRotatorCell]] trait, containing its factory method. */
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

  def unapply(cell: BaseRotatorCell): (Position, Rotation) = (cell.position, cell.rotation)
}

sealed trait BaseGeneratorCell extends GeneratorCell with BaseCell

object BaseGeneratorCell {
  private case class BaseGeneratorCellImpl(position: Position, orientation: Orientation) extends BaseGeneratorCell

  def apply(position: Position, orientation: Orientation): BaseGeneratorCell = BaseGeneratorCellImpl(position, orientation)

  def unapply(cell: BaseGeneratorCell): (Position, Orientation) = (cell.position, cell.orientation)
}

/** Represent the enemy [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
sealed trait BaseEnemyCell extends EnemyCell with BaseCell

object BaseEnemyCell {
  private case class BaseEnemyCellImpl(position: Position) extends BaseEnemyCell

  def apply(position: Position): BaseEnemyCell = BaseEnemyCellImpl(position)

  def unapply(cell: BaseEnemyCell): Tuple1[Position] = Tuple1(cell.position)
}

/** Represent the mover [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation
  *   the [[Orientation]] in the area
  */
sealed trait BaseMoverCell extends MoverCell with BaseCell

object BaseMoverCell {
  private case class BaseMoverCellImpl(position: Position, orientation: Orientation) extends BaseMoverCell

  def apply(position: Position, orientation: Orientation): BaseMoverCell = BaseMoverCellImpl(position, orientation)

  def unapply(cell: BaseMoverCell): (Position, Orientation) = (cell.position, cell.orientation)
}

/** Represent the block [[Cell]]
  *
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param push
  *   the [[Push]]
  */
sealed trait BaseBlockCell extends BlockCell with BaseCell

object BaseBlockCell {
  private case class BaseBlockCellImpl(position: Position, push: Push) extends BaseBlockCell

  def apply(position: Position, push: Push): BaseBlockCell = BaseBlockCellImpl(position, push)

  def unapply(cell: BaseBlockCell): (Position, Push) = (cell.position, cell.push)
}

/** Represent the wall [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
sealed trait BaseWallCell extends WallCell with BaseCell

object BaseWallCell {
  private case class BaseWallCellImpl(position: Position) extends BaseWallCell

  def apply(position: Position): BaseWallCell = BaseWallCellImpl(position)

  def unapply(cell: BaseWallCell): Tuple1[Position] = Tuple1(cell.position)
}
