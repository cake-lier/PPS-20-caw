package it.unibo.pps.caw.dsl

sealed trait Cell extends Position

object Cell {
  private case class CellImpl(x: Int, y: Int) extends Cell

  def apply(x: Int, y: Int): Cell = CellImpl(x, y)
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
  private case class OrientedCellImpl(orientation: Orientation, x: Int, y: Int)
      extends OrientedCell

  def apply(orientation: Orientation, x: Int, y: Int): OrientedCell =
    OrientedCellImpl(orientation, x, y)
}

enum Direction {
  case Clockwise
  case Counterclockwise
}

sealed trait DirectedCell extends Cell {
  val direction: Direction
}

object DirectedCell {
  private case class DirectedCellImpl(direction: Direction, x: Int, y: Int)
      extends DirectedCell

  def apply(direction: Direction, x: Int, y: Int): DirectedCell =
    DirectedCellImpl(direction, x, y)
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
  private case class MovableCellImpl(
      movementDirection: MovementDirection,
      x: Int,
      y: Int
  ) extends MovableCell

  def apply(movementDirection: MovementDirection, x: Int, y: Int): MovableCell =
    MovableCellImpl(movementDirection, x, y)
}
