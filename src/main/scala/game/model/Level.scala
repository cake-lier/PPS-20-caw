package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.{Area, Board, PlayableArea, Position}

/** Represent the main structure of the game */
trait Level extends Area {

  /** the set of [[Cell]] in the level */
  def setupBoard: Board[SetupCell]

  /** the [[PlayableArea]] of the game */
  def playableArea: PlayableArea
}

object Level {
  private case class LevelImpl(width: Int, height: Int, setupBoard: Board[SetupCell], playableArea: PlayableArea) extends Level
  def apply(width: Int, height: Int, setupBoard: Board[SetupCell], playableArea: PlayableArea): Level =
    LevelImpl(width, height, setupBoard, playableArea)
}
