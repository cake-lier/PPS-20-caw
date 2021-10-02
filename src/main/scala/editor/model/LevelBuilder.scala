package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea}
import it.unibo.pps.caw.common.model.cell.PlayableCell

/** The level builder used by the editor.
  *
  * It provides the necessary functionalities to build and modify a level while this is continously edited by the user.
  */
trait LevelBuilder extends Dimensions {

  /** The set of [[Cell]] of the level. */
  def board: Board[PlayableCell]

  /** The [[PlayableArea]] of the level. */
  def playableArea: Option[PlayableArea]
}

/** The companion object of the trait [[LevelBuilder]]. */
object LevelBuilder {
  /* The case class implementaiton of LevelBuilder*/
  private case class LevelBuilderImpl(width: Int, height: Int, board: Board[PlayableCell], playableArea: Option[PlayableArea])
    extends LevelBuilder

  /** Returns a new instance of [[LevelBuilder]] given the width, the height, its cells and playable area.
    * @param width
    *   the width of the level
    * @param height
    *   the height of the level
    * @param board
    *   the cells of the level
    * @param playableArea
    *   the playable area of the level
    */
  def apply(width: Int, height: Int, board: Board[PlayableCell], playableArea: PlayableArea): LevelBuilder =
    LevelBuilderImpl(width, height, board, Some(playableArea))

  /** Returns a new instance of [[LevelBuilder]] given the width, the height and its cell.
    * @param width
    *   the width of the level
    * @param height
    *   the height of the level
    * @param board
    *   the cells of the level
    */
  def apply(width: Int, height: Int, board: Board[PlayableCell]): LevelBuilder =
    LevelBuilderImpl(width, height, board, None)

  extension (l: LevelBuilder) {

    /** An extension method that returns a new [[LevelBuilder]] whose characteristics are all or in part copied from the current
      * [[LevelBuilder]]. By default, the new [[LevelBuilder]] is a perfect copy of the current [[LevelBuilder]].
      *
      * @param width
      *   the width of the level
      * @param height
      *   the height of the level
      * @param board
      *   the cells of the level
      * @param playableArea
      *   the playable area of the level
      */
    def copy(
      width: Int = l.width,
      height: Int = l.height,
      board: Board[PlayableCell] = l.board,
      playableArea: Option[PlayableArea] = l.playableArea
    ): LevelBuilder =
      LevelBuilderImpl(width, height, board, playableArea)
  }
}
