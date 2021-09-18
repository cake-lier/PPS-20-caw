package it.unibo.pps.caw
package model

/** Represent a cell of the game */
sealed trait Cell extends Ordered[Cell] {

  /** the position of the [[Cell]] in coorinates */
  val position: Position

  override def compare(that: Cell): Int = (position.x - that.position.x) + (position.y - that.position.y)
}

/** Represent a [[Cell]] during the setup phase, a [[Cell]] could be playable */
sealed trait SetupCell extends Cell {

  /** if the [[Cell]] is movable by the player (is in the [[PlayableArea]] */
  val playable: Boolean
}

/**  Represent a [[Cell]] during the update phase */
sealed trait IdCell extends Cell {

  /** cell identifier for one update cycle */
  val id: Int
  /** whether the cell has been updated or not */
  val updated: Boolean
}

/** Enum that represent all [[Cell]] types */
enum CellTypes(cellType: String) {
  case Enemy extends CellTypes("enemy")
  case Rotator extends CellTypes("rotator")
  case Mover extends CellTypes("mover")
  case Block extends CellTypes("block")
  case Wall extends CellTypes("wall")
  case Generator extends CellTypes("generator")

  /** Getter of [[CellTypes]] value
    * @return
    *   the value as string
    */
  def getType = cellType
}

/** Enum that represent the direction of rotation some cells */
enum RotationDirection(direction: String) {
  case Right extends RotationDirection("right")
  case Left extends RotationDirection("left")

  /** Getter of [[RotationDirection]] value
    * @return
    *   the value as string
    */
  def getDirection = direction
}

/** Enum that represent the orientation of some cells */
enum Orientation(orientation: String) {
  case Right extends Orientation("right")
  case Left extends Orientation("left")
  case Down extends Orientation("down")
  case Top extends Orientation("top")

  /** Getter of [[Orientation]] value
    * @return
    *   the value as string
    */
  def getOrientation = orientation
}

/** Enum that represent allowed movement of some cells */
enum AllowedMovement(movement: String) {
  case Horizontal extends AllowedMovement("horizontal")
  case Vertical extends AllowedMovement("vertical")
  case Both extends AllowedMovement("both")

  /** Getter of [[AllowedMovement]] value
    * @return
    *   the value as string
    */
  def getMovement = movement
}

/** Object for enum helper funcions */
object EnumHelper {

  /** get the [[Option]] of given type to [[CellTypes]]
    * @param stringCellTypes:
    *   the value of [[CellTypes]] as string
    */
  def toCellTypes(stringCellTypes: String): Option[CellTypes] =
    CellTypes.values.find(_.getType == stringCellTypes)

  /** get the [[Option]] of given orientation to [[Orientation]]
    * @param stringOrientation:
    *   the value of [[Orientation]] as string
    */
  def toOrientation(stringOrientation: String): Option[Orientation] =
    Orientation.values.find(_.getOrientation == stringOrientation)

  /** get the [[Option]] of given allowed movement to [[AllowedMovement]]
    * @param stringMovement:
    *   the value of [[AllowedMovement]] as string
    */
  def toMovement(stringMovement: String): Option[AllowedMovement] =
    AllowedMovement.values.find(_.getMovement == stringMovement)

  /** get the [[Option]] of given rotation direction to [[RotationDirection]]
    * @param stringRotationDirection:
    *   the value of [[RotationDirection]] as string
    */
  def toRotation(stringRotationDirection: String): Option[RotationDirection] =
    RotationDirection.values.find(_.getDirection == stringRotationDirection)
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
    case EnemyCell(position)                              => IdEnemyCell(position, id, false)
    case WallCell(position)                               => IdWallCell(position, id, false)
    case RotatorCell(position, rotationDirection)         => IdRotatorCell(position, rotationDirection, id, false)
    case MoverCell(position, orientation)                 => IdMoverCell(position, orientation, id, false)
    case GeneratorCell(position, orientation)             => IdGeneratorCell(position, orientation, id, false)
    case BlockCell(position, allowedMovement)             => IdBlockCell(position, allowedMovement, id, false)
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
  * @param position:
  *   the coordinates of the cell
  * @param playable:
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param rotationDirection:
  *   the [[RotationDirection]]
  */
case class RotatorCell(position: Position, rotationDirection: RotationDirection) extends Cell
case class SetupRotatorCell(position: Position, rotationDirection: RotationDirection, playable: Boolean) extends SetupCell
case class IdRotatorCell(position: Position, rotationDirection: RotationDirection, id: Int, updated: Boolean) extends IdCell

/** Represent the generator [[Cell]]
  * @param position:
  *   the coordinates of the cell
  * @param playable:
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation:
  *   the [[Orientation]] in the area
  */
case class GeneratorCell(position: Position, orientation: Orientation) extends Cell
case class SetupGeneratorCell(position: Position, orientation: Orientation, playable: Boolean) extends SetupCell
case class IdGeneratorCell(position: Position, orientation: Orientation, id: Int, updated: Boolean) extends IdCell

/** Represent the enemy [[Cell]]
  * @param position:
  *   the coordinates of the cell
  * @param playable:
  *   if the cell is playable (is in the [[PlayableArea]])
  */
case class EnemyCell(position: Position) extends Cell
case class SetupEnemyCell(position: Position, playable: Boolean) extends SetupCell
case class IdEnemyCell(position: Position, id: Int, updated: Boolean) extends IdCell

/** Represent the mover [[Cell]]
  * @param position:
  *   the coordinates of the cell
  * @param playable:
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation:
  *   the [[Orientation]] in the area
  */
case class MoverCell(position: Position, orientation: Orientation) extends Cell
case class SetupMoverCell(position: Position, orientation: Orientation, playable: Boolean) extends SetupCell
case class IdMoverCell(position: Position, orientation: Orientation, id: Int, updated: Boolean) extends IdCell

/** Represent the block [[Cell]]
  * @param position:
  *   the coordinates of the cell
  * @param playable:
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param allowedMovement:
  *   the [[AllowedMovement]]
  */
case class BlockCell(position: Position, allowedMovement: AllowedMovement) extends Cell
case class SetupBlockCell(position: Position, allowedMovement: AllowedMovement, playable: Boolean) extends SetupCell
case class IdBlockCell(position: Position, allowedMovement: AllowedMovement, id: Int, updated: Boolean) extends IdCell

/** Represent the wall [[Cell]]
  * @param position:
  *   the coordinates of the cell
  * @param playable:
  *   if the cell is playable (is in the [[PlayableArea]])
  */
case class WallCell(position: Position) extends Cell
case class SetupWallCell(position: Position, playable: Boolean) extends SetupCell
case class IdWallCell(position: Position, id: Int, updated: Boolean) extends IdCell