package it.unibo.pps.caw.editor.model

/** Represent the concept of a game area */
trait Area {

  /** the width of the area */
  def width: Int

  /** the height of the area */
  def height: Int
}

/** Companion object of trait [[Area]] */
object Area {
  private case class AreaImpl(width: Int, height: Int, cells: Set[Cell]) extends Area
  def apply(width: Int, height: Int, cells: Set[Cell]): Area = AreaImpl(width, height, cells)
}

/** Represent the area in which one the player can move [[Cell]] on it */
trait PlayableArea extends Area {

  /** top-left area coordinates */
  def position: Position
}

/** Companion object of trait [[PlayableArea]] */
object PlayableArea {
  private case class PlayableAreaImpl(position: Position, width: Int, height: Int) extends PlayableArea
  def apply(position: Position, width: Int, height: Int): PlayableArea =
    PlayableAreaImpl(position, width, height)
}

/** Represent the main structure of the game */
trait Level extends Area {

  /** the set of [[Cell]] in the level */
  def board: Board[Cell]

  /** the [[PlayableArea]] of the game */
  def playableArea: Option[PlayableArea]
}

object Level {
  private case class LevelImpl(width: Int, height: Int, board: Board[Cell], playableArea: Option[PlayableArea]) extends Level
  def apply(width: Int, height: Int, board: Board[Cell], playableArea: PlayableArea): Level =
    LevelImpl(width, height, board, Some(playableArea))

  def apply(width: Int, height: Int, board: Board[Cell]): Level =
    LevelImpl(width, height, board, None)
}
