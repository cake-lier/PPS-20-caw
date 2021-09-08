package it.unibo.pps.caw
package it.unibo.pps.caw

trait Position{
  def x: Int
  def y: Int
}

trait Cell{
  def position: Position
}

enum CellTypes:
  case EnemyCell, RotationCell, PushCell, BlockCell, EmptyCell, WallCell

enum RotationDirection:
  case Right, Left

enum Orientation:
  case Right, Left, Down, Top

enum AllowedMovement:
  case Horizontal, Vertical, Both

case class RotationCell(position: Position, directions: RotationDirection) extends Cell
case class GeneratorCell(position: Position, orientation: Orientation) extends Cell
case class EnemyCell(position: Position) extends Cell
case class PushCell(position: Position,  orientation: Orientation) extends Cell
case class BlockCell(position: Position,  allowedMovement: AllowedMovement) extends Cell
case class EmptyCell(position: Position) extends Cell
case class WallCel(position: Position) extends Cell
