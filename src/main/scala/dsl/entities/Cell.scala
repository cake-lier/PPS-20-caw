package it.unibo.pps.caw.dsl.entities

/** A generic cell placed on a [[Board]].
  *
  * This trait represents a generic cell, hence, it is only represented by its [[Position]] on the [[Board]]. It must be
  * constructed through its companion object.
  */
sealed trait Cell {

  /** Returns the [[Position]] of the cell on the [[Board]]. */
  val position: Position
}

/** The companion object of the [[Cell]] trait, containing its factory method. */
object Cell {
  
  /* Default implementation of the Cell trait. */
  private case class CellImpl(position: Position) extends Cell

  /** Returns a new instance of the [[Cell]] trait.
    *
    * @param position
    *   the [[Position]] of the cell on the [[Board]]
    * @return
    *   a new instance of the [[Cell]] trait
    */
  def apply(position: Position): Cell = CellImpl(position)
}

/** The orientation of an [[OrientableCell]].
  *
  * This trait represents the direction an [[OrientableCell]] is facing to. These cells can face the left, right, top or bottom
  * direction and express their behavior in that direction.
  */
enum Orientation {

  /** The left direction. */
  case Left

  /** The right direction. */
  case Right

  /** The top direction. */
  case Top

  /** The down direction. */
  case Down
}

/** A [[Cell]] that has an [[Orientation]].
  *
  * The "mover" cells and "generator" cells are orientable cells because they can be oriented. It must be constructed through its
  * companion object.
  */
sealed trait OrientableCell extends Cell {

  /** Returns the [[Orientation]] of this cell. */
  val orientation: Orientation
}

/** Companion object of the [[OrientableCell]] trait, containing its factory method. */
object OrientableCell {
  
  /* Default implementation of the OrientableCell trait. */
  private case class OrientableCellImpl(orientation: Orientation, position: Position) extends OrientableCell

  /** Returns a new instance of the [[OrientableCell]] trait.
    *
    * @param orientation
    *   the [[Orientation]] of the cell to create
    * @param position
    *   the [[Position]] of the cell to create
    * @return
    *   a new instance of the [[OrientableCell]] trait
    */
  def apply(orientation: Orientation)(position: Position): OrientableCell = OrientableCellImpl(orientation, position)
}

/** The direction in which a [[RotatableCell]] is rotating.
  *
  * This trait represents the direction in which a [[RotatableCell]] is rotating. These cells can rotate in the standard two
  * directions, clockwise and counterclockwise and behave accordingly.
  */
enum Rotation {

  /** The clockwise direction of rotation. */
  case Clockwise

  /** The counterclockwise direction of rotation. */
  case Counterclockwise
}

/** A [[Cell]] that has a direction of [[Rotation]].
  *
  * The "rotator" cell is a rotatable cell because it has a direction of rotation. It must be constructed through its companion
  * object.
  */
sealed trait RotatableCell extends Cell {

  /** Returns the direction of [[Rotation]] of this cell. */
  val rotation: Rotation
}

/** Companion object of the [[RotatableCell]] trait, containing its factory method. */
object RotatableCell {
  
  /* Default implementation of the RotatableCell trait. */
  private case class RotatableCellImpl(rotation: Rotation, position: Position) extends RotatableCell

  /** Returns a new instance of the [[RotatableCell]] trait.
    *
    * @param rotation
    *   the direction of [[Rotation]] of the created cell
    * @param position
    *   the [[Position]] of the created cell
    * @return
    *   a new instance of the [[RotatableCell]] trait
    */
  def apply(rotation: Rotation)(position: Position): RotatableCell = RotatableCellImpl(rotation, position)
}

/** The direction along which a [[MovableCell]] can be pushed.
  *
  * This trait represents the direction along which a [[MovableCell]] can be moved. The directions are horizontal, vertical and
  * both at the same time.
  */
enum Push {

  /** The vertical push direction. */
  case Vertical

  /** The horizontal push direction. */
  case Horizontal

  /** The vertical and horizontal push direction. */
  case Both
}

/** A [[Cell]] that has explicitly been thought to be pushed around and nothing else.
  *
  * A "block" cell is a [[PushableCell]] because it can be pushed aroung and nothing else. It must be constructed through its
  * companion object.
  */
sealed trait PushableCell extends Cell {

  /** Returns the [[Push]] direction along which this cell can be pushed. */
  val push: Push
}

/** Companion object of the [[PushableCell]] trait, containing its factory method. */
object PushableCell {
  
  /* Default implementation of the PushableCell trait. */
  private case class PushableCellImpl(push: Push, position: Position) extends PushableCell

  /** Returns a new instance of the [[PushableCell]] trait.
    *
    * @param push
    *   the [[Push]] direction of the created cell
    * @param position
    *   the [[Position]] of the created cell
    * @return
    *   a new instance of the [[PushableCell]] trait
    */
  def apply(push: Push)(position: Position): PushableCell = PushableCellImpl(push, position)
}
