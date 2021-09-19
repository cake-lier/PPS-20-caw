package it.unibo.pps.caw.editor.model

/** Represent a cell of the game */
trait Cell {

  /** the position of the [[Cell]] in coorinates */
  def position: Position

  /** if the [[Cell]] is movable by the player (is in the [[PlayableArea]] */
  def playable: Boolean
}

/** Enum that represent all [[Cell]] types */
enum CellTypes(cellType: String) {
  case Enemy extends CellTypes("enemy")
  case Rotator extends CellTypes("rotator")
  case Mover extends CellTypes("mover")
  case Block extends CellTypes("block")
  case Empty extends CellTypes("empty")
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
    * @param stringCellTypes
    *   the value of [[CellTypes]] as string
    */
  def toCellTypes(stringCellTypes: String): Option[CellTypes] =
    CellTypes.values.find(_.getType == stringCellTypes)

  /** get the [[Option]] of given orientation to [[Orientation]]
    * @param stringOrientation
    *   the value of [[Orientation]] as string
    */
  def toOrientation(stringOrientation: String): Option[Orientation] =
    Orientation.values.find(_.getOrientation == stringOrientation)

  /** get the [[Option]] of given allowed movement to [[AllowedMovement]]
    * @param stringMovement
    *   the value of [[AllowedMovement]] as string
    */
  def toMovement(stringMovement: String): Option[AllowedMovement] =
    AllowedMovement.values.find(_.getMovement == stringMovement)

  /** get the [[Option]] of given rotation direction to [[RotationDirection]]
    * @param stringRotationDirection
    *   the value of [[RotationDirection]] as string
    */
  def toRotation(stringRotationDirection: String): Option[RotationDirection] =
    RotationDirection.values.find(_.getDirection == stringRotationDirection)
}

/** Represent the rotator [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param rotationDirection
  *   the [[RotationDirection]]
  */
case class RotatorCell(position: Position, playable: Boolean, rotationDirection: RotationDirection) extends Cell

/** Represent the generator [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation
  *   the [[Orientation]] in the area
  */
case class GeneratorCell(position: Position, playable: Boolean, orientation: Orientation) extends Cell

/** Represent the enemy [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
case class EnemyCell(position: Position, playable: Boolean) extends Cell

/** Represent the mover [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation
  *   the [[Orientation]] in the area
  */
case class MoverCell(position: Position, playable: Boolean, orientation: Orientation) extends Cell

/** Represent the block [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param allowedMovement
  *   the [[AllowedMovement]]
  */
case class BlockCell(position: Position, playable: Boolean, allowedMovement: AllowedMovement) extends Cell

/** Represent the wall [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
case class WallCel(position: Position, playable: Boolean) extends Cell
