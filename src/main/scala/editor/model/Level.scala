package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.{Area, Board, PlayableArea}

/** Represent the main structure of the game */
trait Level extends Area {

  /** the set of [[Cell]] in the level */
  def board: Board[SetupCell]

  /** the [[PlayableArea]] of the game */
  def playableArea: Option[PlayableArea]
}

object Level {
  private case class LevelImpl(width: Int, height: Int, board: Board[SetupCell], playableArea: Option[PlayableArea]) extends Level
  def apply(width: Int, height: Int, board: Board[SetupCell], playableArea: PlayableArea): Level =
    LevelImpl(width, height, board, Some(playableArea))

  def apply(width: Int, height: Int, board: Board[SetupCell]): Level =
    LevelImpl(width, height, board, None)
}
