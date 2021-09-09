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

sealed trait OrientableCell extends Cell {
  val orientation: Orientation
}

object OrientableCell {
  private case class OrientedCellImpl(orientation: Orientation, position: Position) extends OrientableCell

  def apply(orientation: Orientation)(position: Position): OrientableCell = OrientedCellImpl(orientation, position)
}

enum Rotation {
  case Clockwise
  case Counterclockwise
}

sealed trait RotatableCell extends Cell {
  val rotation: Rotation
}

object RotatableCell {
  private case class DirectedCellImpl(rotation: Rotation, position: Position) extends RotatableCell

  def apply(rotation: Rotation)(position: Position): RotatableCell = DirectedCellImpl(rotation, position)
}

enum Push {
  case Vertical
  case Horizontal
  case Both
}

sealed trait PushableCell extends Cell {
  val push: Push
}

object PushableCell {
  private case class PushableCellImpl(push: Push, position: Position) extends PushableCell

  def apply(push: Push)(position: Position): PushableCell = PushableCellImpl(push, position)
}
