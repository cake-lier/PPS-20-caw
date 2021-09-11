package it.unibo.pps.caw
package model

trait Cell {
  def position: Position
  def playable: Boolean
}

enum CellTypes(cellType: String){
  case Enemy extends CellTypes("enemy")
  case Rotator extends CellTypes("rotator")
  case Mover extends CellTypes("mover")
  case Block extends CellTypes("block")
  case Empty extends CellTypes("empty")
  case Wall extends CellTypes("wall")
  case Generator extends CellTypes("generator")

  def getType = cellType
}

enum RotationDirection(direction: String) {
  case Right extends RotationDirection("right")
  case Left extends RotationDirection("left")

  def getDirection = direction
}

enum Orientation(orientation: String){
  case Right extends Orientation("right")
  case Left extends Orientation("left")
  case Down  extends Orientation("down")
  case Top extends Orientation("top")

  def getOrientation = orientation
}

enum AllowedMovement(movement: String){
  case Horizontal extends AllowedMovement("horizontal")
  case Vertical extends AllowedMovement("vertical")
  case Both extends AllowedMovement("both")

  def getMovement = movement
}

object EnumHelper{
  def toCellTypes(stringCellTypes: String): Option[CellTypes] =
    CellTypes.values.find(_.getType == stringCellTypes)

  def toOrientation(stringOrientation: String): Option[Orientation] =
    Orientation.values.find(_.getOrientation == stringOrientation)

  def toMovement(stringOrientation: String): Option[AllowedMovement] =
    AllowedMovement.values.find(_.getMovement == stringOrientation)

  def toRotation(stringOrientation: String): Option[RotationDirection] =
    RotationDirection.values.find(_.getDirection == stringOrientation)
}

case class RotatorCell(position: Position, playable: Boolean, directions: RotationDirection) extends Cell
case class GeneratorCell(position: Position, playable: Boolean, orientation: Orientation) extends Cell
case class EnemyCell(position: Position, playable: Boolean) extends Cell
case class MoverCell(position: Position, playable: Boolean, orientation: Orientation) extends Cell
case class BlockCell(position: Position, playable: Boolean,  allowedMovement: AllowedMovement) extends Cell
case class WallCel(position: Position, playable: Boolean) extends Cell
