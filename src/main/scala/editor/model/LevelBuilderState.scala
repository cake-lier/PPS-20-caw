package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea}
import it.unibo.pps.caw.common.model.cell.PlayableCell

/** The state of a level builder used by the editor.
  *
  * It collects the necessary data inserted by the user in order to allow another entity to modify and build a
  * [[it.unibo.pps.caw .common.model.Level]] and its structure. It must be constructed through its companion object.
  */
trait LevelBuilderState {

  /** Returns the [[Dimensions]] of this [[LevelBuilderState]]. */
  val dimensions: Dimensions

  /** Returns the [[Board]] of this [[LevelBuilderState]]. */
  val board: Board[PlayableCell]

  /** Returns the [[PlayableArea]] of this [[LevelBuilderState]]. */
  val playableArea: Option[PlayableArea]
}

/** The companion object of the trait [[LevelBuilderState]], containing its factory methods. */
object LevelBuilderState {

  /* The case class implementaiton of LevelBuilderState*/
  private case class LevelBuilderImpl(dimensions: Dimensions, board: Board[PlayableCell], playableArea: Option[PlayableArea])
    extends LevelBuilderState

  /** Returns a new instance of [[LevelBuilderState]] given its [[Dimensions]], its [[Board]] and its [[PlayableArea]].
    *
    * @param dimensions
    *   the [[Dimensions]] of the [[LevelBuilderState]] to create
    * @param board
    *   the [[Board]] of the [[LevelBuilderState]] to create
    * @param playableArea
    *   the [[PlayableArea]] of the [[LevelBuilderState]] to create
    * @param playableArea
    *   the [[PlayableArea]] of the level
    * @return
    *   a new instance of [[LevelBuilderState]]
    */
  def apply(playableArea: PlayableArea)(dimensions: Dimensions)(board: Board[PlayableCell]): LevelBuilderState =
    LevelBuilderImpl(dimensions, board, Some(playableArea))

  /** Returns a new instance of [[LevelBuilderState]] given its [[Dimensions]] and its [[Board]].
    *
    * @param dimensions
    *   the [[Dimensions]] of the [[LevelBuilderState]] to create
    * @param board
    *   the [[Board]] of the [[LevelBuilderState]] to create
    * @return
    *   a new instance of [[LevelBuilderState]]
    */
  def apply(dimensions: Dimensions)(board: Board[PlayableCell]): LevelBuilderState = LevelBuilderImpl(dimensions, board, None)

  /** Contains the extensions methods for the [[LevelBuilderState]] trait. */
  extension (builder: LevelBuilderState) {

    /** An extension method that returns a new [[LevelBuilderState]] whose characteristics are all or in part copied from the
      * current [[LevelBuilderState]]. By default, the new [[LevelBuilderState]] is a perfect copy of the current
      * [[LevelBuilderState]].
      *
      * @param dimensions
      *   the [[Dimensions]] of the [[LevelBuilderState]] to create
      * @param board
      *   the [[Board]] of the [[LevelBuilderState]] to create
      * @param playableArea
      *   the [[PlayableArea]] of the [[LevelBuilderState]] to create
      * @return
      *   a new instance of [[LevelBuilderState]] copied from this one
      */
    def copy(
      dimensions: Dimensions = builder.dimensions,
      board: Board[PlayableCell] = builder.board,
      playableArea: Option[PlayableArea] = builder.playableArea
    ): LevelBuilderState =
      LevelBuilderImpl(dimensions, board, playableArea)
  }
}
