package it.unibo.pps.caw
package model

trait Area{
  def width: Int
  def height: Int
}

object Area{
  private case class AreaImpl(width: Int, height: Int, cells: Set[Cell]) extends Area
  def apply(width: Int, height: Int, cells: Set[Cell]): Area = AreaImpl(width, height, cells)
}

trait PlayableArea extends Area{
  def position: Position
}

object PlayableArea{
  private case class PlayableAreaImpl(position: Position, width: Int, height: Int) extends PlayableArea
  def apply(position: Position, width: Int, height: Int): PlayableArea =
    PlayableAreaImpl(position,width,height)
}

trait Level extends Area{
  def cells: Set[Cell]
  def playableArea: PlayableArea
}

object Level{
  private case class LevelImpl(width: Int, height: Int, cells: Set[Cell], playableArea: PlayableArea) extends Level
  def apply(width: Int, height: Int,cells: Set[Cell], playableArea: PlayableArea): Level =
    LevelImpl(width, height, cells, playableArea)
}
