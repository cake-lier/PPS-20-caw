package it.unibo.pps.caw.game.model

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
enum Rotation(rotation: String) {
  case Clockwise extends Rotation("clockwise")
  case Counterclockwise extends Rotation("counterclockwise")

  /** Getter of [[Rotation]] value
 *
    * @return
    *   the value as string
    */
  def getRotation = rotation
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

/** Enum that represent the direction to which some cells can be pushed */
enum Push(push: String) {
  case Horizontal extends Push("horizontal")
  case Vertical extends Push("vertical")
  case Both extends Push("both")

  /** Getter of [[Push]] value
 *
    * @return
    *   the value as string
    */
  def getPush = push
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

  /** get the [[Option]] of given push to [[Push]]
 *
    * @param stringPush
    * the value of [[Push]] as string
    */
  def toPush(stringPush: String): Option[Push] =
    Push.values.find(_.getPush == stringPush)

  /** get the [[Option]] of given rotation to [[Rotation]]
 *
    * @param stringRotation
    * the value of [[Rotation]] as string
    */
  def toRotation(stringRotation: String): Option[Rotation] =
    Rotation.values.find(_.getRotation == stringRotation)
}

/** Represent the rotator [[Cell]]
 *
  * @param position
  * the coordinates of the cell
  * @param playable
  * if the cell is playable (is in the [[PlayableArea]])
  * @param rotation
  * the [[Rotation]]
  */
case class RotatorCell(position: Position, playable: Boolean, rotation: Rotation) extends Cell

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
 *
  * @param position
  * the coordinates of the cell
  * @param playable
  * if the cell is playable (is in the [[PlayableArea]])
  * @param push
  * the [[Push]]
  */
case class BlockCell(position: Position, playable: Boolean, push: Push) extends Cell

/** Represent the wall [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
case class WallCell(position: Position, playable: Boolean) extends Cell
