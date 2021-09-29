package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea}
import it.unibo.pps.caw.common.model.cell.PlayableCell

/** Represent the main structure of the game */
trait LevelBuilder extends Dimensions {

  /** the set of [[Cell]] in the level */
  def board: Board[PlayableCell]

  /** the [[PlayableArea]] of the game */
  def playableArea: Option[PlayableArea]
}

object LevelBuilder {
  private case class LevelBuilderImpl(width: Int, height: Int, board: Board[PlayableCell], playableArea: Option[PlayableArea])
    extends LevelBuilder

  def apply(width: Int, height: Int, board: Board[PlayableCell], playableArea: PlayableArea): LevelBuilder =
    LevelBuilderImpl(width, height, board, Some(playableArea))

  def apply(width: Int, height: Int, board: Board[PlayableCell]): LevelBuilder =
    LevelBuilderImpl(width, height, board, None)

  extension (l: LevelBuilder) {
    def copy(
      width: Int = l.width,
      height: Int = l.height,
      board: Board[PlayableCell] = l.board,
      playableArea: Option[PlayableArea] = l.playableArea
    ): LevelBuilder =
      LevelBuilderImpl(width, height, board, playableArea)
  }
}
