package it.unibo.pps.caw.editor.model

/** Represent a cell of the game */
trait Cell extends Ordered[Cell] {

  /** the position of the [[Cell]] in coorinates */
  def position: Position

  override def compare(that: Cell): Int = (position.x - that.position.x) + (position.y - that.position.y)
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

/** Represent the generator [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation
  *   the [[Orientation]] in the area
  */
case class GeneratorCell(position: Position, orientation: Orientation) extends Cell

/** Represent the enemy [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
case class EnemyCell(position: Position) extends Cell

/** Represent the mover [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation
  *   the [[Orientation]] in the area
  */
case class MoverCell(position: Position, orientation: Orientation) extends Cell

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

/** Represent the wall [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
case class WallCell(position: Position) extends Cell