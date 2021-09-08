package it.unibo.pps.caw.dsl.entities

sealed trait Cell {
  val position: Position
}

object Cell {
  private case class CellImpl(position: Position) extends Cell

  def apply(position: Position): Cell = CellImpl(position)
}

enum Orientation {
  case Left
  case Right
  case Top
  case Down
}

sealed trait OrientedCell extends Cell {
  val orientation: Orientation
}

object OrientedCell {
  private case class OrientedCellImpl(orientation: Orientation, position: Position) extends OrientedCell

  def apply(orientation: Orientation)(position: Position): OrientedCell = OrientedCellImpl(orientation, position)
}

enum Direction {
  case Clockwise
  case Counterclockwise
}

sealed trait DirectedCell extends Cell {
  val direction: Direction
}

object DirectedCell {
  private case class DirectedCellImpl(direction: Direction, position: Position) extends DirectedCell

  def apply(direction: Direction)(position: Position): DirectedCell = DirectedCellImpl(direction, position)
}

enum MovementDirection {
  case Vertical
  case Horizontal
  case Both
}

sealed trait MovableCell extends Cell {
  val movementDirection: MovementDirection
}

object MovableCell {
  private case class MovableCellImpl(movementDirection: MovementDirection, position: Position) extends MovableCell

  def apply(movementDirection: MovementDirection)(position: Position): MovableCell = MovableCellImpl(movementDirection, position)
}
