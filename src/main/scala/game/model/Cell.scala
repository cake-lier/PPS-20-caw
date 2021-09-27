package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.Position

/** Represent a cell of the game */
trait Cell extends Ordered[Cell] {

  /** the position of the [[Cell]] in coorinates */
  def position: Position

  override def compare(that: Cell): Int = (position.x - that.position.x) + (position.y - that.position.y)
}

/** Represent a [[Cell]] during the setup phase, a [[Cell]] could be playable */
sealed trait SetupCell extends Cell {

  /** if the [[Cell]] is movable by the player (is in the [[PlayableArea]] */
  val playable: Boolean
}

/** Represent a [[Cell]] during the update phase */
sealed trait IdCell extends Cell {

  /** cell identifier for one update cycle */
  val id: Int

  /** whether the cell has been updated or not */
  val updated: Boolean
}

/** Enum that represent all [[Cell]] types */
enum CellTypes(val cellType: String) {
  case Enemy extends CellTypes("enemy")
  case Rotator extends CellTypes("rotator")
  case Mover extends CellTypes("mover")
  case Block extends CellTypes("block")
  case Empty extends CellTypes("empty")
  case Wall extends CellTypes("wall")
  case Generator extends CellTypes("generator")
}

/** Enum that represent the direction of rotation some cells */
enum Rotation(val rotation: String) {
  case Clockwise extends Rotation("clockwise")
  case Counterclockwise extends Rotation("counterclockwise")
}

/** Enum that represent the orientation of some cells */
enum Orientation(val orientation: String) {
  case Right extends Orientation("right")
  case Left extends Orientation("left")
  case Down extends Orientation("down")
  case Top extends Orientation("top")
}

/** Enum that represent the direction to which some cells can be pushed */
enum Push(val push: String) {
  case Horizontal extends Push("horizontal")
  case Vertical extends Push("vertical")
  case Both extends Push("both")
}

/** Object for enum helper funcions */
object EnumHelper {

  /** get the [[Option]] of given type to [[CellTypes]]
    * @param stringCellTypes
    *   the value of [[CellTypes]] as string
    */
  def toCellTypes(stringCellTypes: String): Option[CellTypes] =
    CellTypes.values.find(_.cellType == stringCellTypes)

  /** get the [[Option]] of given orientation to [[Orientation]]
    * @param stringOrientation
    *   the value of [[Orientation]] as string
    */
  def toOrientation(stringOrientation: String): Option[Orientation] =
    Orientation.values.find(_.orientation == stringOrientation)

  /** get the [[Option]] of given push to [[Push]]
    *
    * @param stringPush
    *   the value of [[Push]] as string
    */
  def toPush(stringPush: String): Option[Push] =
    Push.values.find(_.push == stringPush)

  /** get the [[Option]] of given rotation to [[Rotation]]
    *
    * @param stringRotation
    *   the value of [[Rotation]] as string
    */
  def toRotation(stringRotation: String): Option[Rotation] =
    Rotation.values.find(_.rotation == stringRotation)
}

object CellConverter {
  def fromSetup(setupCell: SetupCell) = setupCell match {
    case SetupEnemyCell(position, _)                      => EnemyCell(position)
    case SetupWallCell(position, _)                       => WallCell(position)
    case SetupRotatorCell(position, rotationDirection, _) => RotatorCell(position, rotationDirection)
    case SetupMoverCell(position, orientation, _)         => MoverCell(position, orientation)
    case SetupGeneratorCell(position, orientation, _)     => GeneratorCell(position, orientation)
    case SetupBlockCell(position, movement, _)            => BlockCell(position, movement)
  }

  def fromId(idCell: IdCell) = idCell match {
    case IdEnemyCell(position, _, _)                      => EnemyCell(position)
    case IdWallCell(position, _, _)                       => WallCell(position)
    case IdRotatorCell(position, rotationDirection, _, _) => RotatorCell(position, rotationDirection)
    case IdMoverCell(position, orientation, _, _)         => MoverCell(position, orientation)
    case IdGeneratorCell(position, orientation, _, _)     => GeneratorCell(position, orientation)
    case IdBlockCell(position, allowedMovement, _, _)     => BlockCell(position, allowedMovement)
  }

  def toId(cell: Cell, id: Int) = cell match {
    case EnemyCell(position)                      => IdEnemyCell(position, id, false)
    case WallCell(position)                       => IdWallCell(position, id, false)
    case RotatorCell(position, rotationDirection) => IdRotatorCell(position, rotationDirection, id, false)
    case MoverCell(position, orientation)         => IdMoverCell(position, orientation, id, false)
    case GeneratorCell(position, orientation)     => IdGeneratorCell(position, orientation, id, false)
    case BlockCell(position, allowedMovement)     => IdBlockCell(position, allowedMovement, id, false)
  }

  def toUpdated(idCell: IdCell, updated: Boolean) = idCell match {
    case IdEnemyCell(position, id, _)                      => IdEnemyCell(position, id, updated)
    case IdWallCell(position, id, _)                       => IdWallCell(position, id, updated)
    case IdRotatorCell(position, rotationDirection, id, _) => IdRotatorCell(position, rotationDirection, id, updated)
    case IdMoverCell(position, orientation, id, _)         => IdMoverCell(position, orientation, id, updated)
    case IdGeneratorCell(position, orientation, id, _)     => IdGeneratorCell(position, orientation, id, updated)
    case IdBlockCell(position, allowedMovement, id, _)     => IdBlockCell(position, allowedMovement, id, updated)
  }

}

/** Represent the rotator [[Cell]]
  *
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param rotation
  *   the [[Rotation]]
  */
case class RotatorCell(position: Position, rotation: Rotation) extends Cell
case class SetupRotatorCell(position: Position, rotation: Rotation, playable: Boolean) extends SetupCell
case class IdRotatorCell(position: Position, rotation: Rotation, id: Int, updated: Boolean) extends IdCell

/** Represent the generator [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation
  *   the [[Orientation]] in the area
  */
case class GeneratorCell(position: Position, orientation: Orientation) extends Cell
case class SetupGeneratorCell(position: Position, orientation: Orientation, playable: Boolean) extends SetupCell
case class IdGeneratorCell(position: Position, orientation: Orientation, id: Int, updated: Boolean) extends IdCell

/** Represent the enemy [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
case class EnemyCell(position: Position) extends Cell
case class SetupEnemyCell(position: Position, playable: Boolean) extends SetupCell
case class IdEnemyCell(position: Position, id: Int, updated: Boolean) extends IdCell

/** Represent the mover [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation
  *   the [[Orientation]] in the area
  */
case class MoverCell(position: Position, orientation: Orientation) extends Cell
case class SetupMoverCell(position: Position, orientation: Orientation, playable: Boolean) extends SetupCell
case class IdMoverCell(position: Position, orientation: Orientation, id: Int, updated: Boolean) extends IdCell

/** Represent the block [[Cell]]
  *
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param push
  *   the [[Push]]
  */
case class BlockCell(position: Position, push: Push) extends Cell
case class SetupBlockCell(position: Position, push: Push, playable: Boolean) extends SetupCell
case class IdBlockCell(position: Position, push: Push, id: Int, updated: Boolean) extends IdCell

/** Represent the wall [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
case class WallCell(position: Position) extends Cell
case class SetupWallCell(position: Position, playable: Boolean) extends SetupCell
case class IdWallCell(position: Position, id: Int, updated: Boolean) extends IdCell
