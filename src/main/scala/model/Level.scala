package it.unibo.pps.caw
package model

/** Represent the concept of a game area */
trait Area {

  /** the width of the area */
  val width: Int

  /** the height of the area */
  val height: Int
}

/** Companion object of trait [[Area]] */
object Area {
  private case class AreaImpl(width: Int, height: Int, cells: Set[Cell]) extends Area
  def apply(width: Int, height: Int, cells: Set[Cell]): Area = AreaImpl(width, height, cells)
}

/** Represent the area in witch one the player can move [[Cell]] on it */
trait PlayableArea extends Area {

  /** top-left area coordiantes */
  val position: Position
}

/** Companion object of trait [[PlayableArea]] */
object PlayableArea {
  private case class PlayableAreaImpl(position: Position, width: Int, height: Int) extends PlayableArea
  def apply(position: Position, width: Int, height: Int): PlayableArea =
    PlayableAreaImpl(position, width, height)
}

/** Represent the main structure of the game */
trait Level extends Area {

  /** [[SetupBoard]] in the level */
  val setupBoard: Board[SetupCell]

  /** the [[PlayableArea]] of the game */
  val playableArea: PlayableArea
}

/** Companion object of [[Level]] */
object Level {
  private case class LevelImpl(width: Int, height: Int, setupBoard: Board[SetupCell], playableArea: PlayableArea) extends Level
  def apply(width: Int, height: Int, setupBoard: Board[SetupCell], playableArea: PlayableArea): Level =
    LevelImpl(width, height, setupBoard, playableArea)
}
