package it.unibo.pps.caw
package editor.model

import common.model.*
import common.model.cell.PlayableCell

/** The state of the editor, which collects the changes made to the [[it.unibo.pps.caw.common.model.Level]] being edited by the
  * player in the editor.
  *
  * This part of the [[EditorModel]] is responsible for collecting and recording pieces of information supplied by the player
  * while carrying out the [[it.unibo.pps.caw.common.model.Level]] building process thanks to the editor. This process creates a
  * new level from the ground up, meaning that the only mandatory information is about the
  * [[it.unibo.pps.caw.common.model.Dimensions]] of the level. All other pieces of information are at the discretion of the player
  * and they can not be given. It must be constructed through its companion object.
  */
trait EditorModelState {

  /** Returns the [[it.unibo.pps.caw.common.model.Dimensions]] of this [[EditorModelState]]. */
  val dimensions: Dimensions

  /** Returns the [[it.unibo.pps.caw.common.model.Board]] of this [[EditorModelState]]. */
  val board: Board[PlayableCell]

  /** Returns the [[it.unibo.pps.caw.common.model.PlayableArea]] of this [[EditorModelState]]. */
  val playableArea: Option[PlayableArea]
}

/** The companion object of the trait [[EditorModelState]], containing its factory methods. */
object EditorModelState {

  /* The case class implementaiton of LevelBuilderState*/
  private case class LevelBuilderImpl(dimensions: Dimensions, board: Board[PlayableCell], playableArea: Option[PlayableArea])
    extends EditorModelState

  /** Returns a new instance of [[EditorModelState]] given its [[it.unibo.pps.caw.common.model.Dimensions]], its
    * [[it.unibo.pps.caw.common.model.Board]] and its [[it.unibo.pps.caw.common.model.PlayableArea]].
    *
    * @param dimensions
    *   the [[it.unibo.pps.caw.common.model.Dimensions]] of the [[EditorModelState]] to create
    * @param board
    *   the [[it.unibo.pps.caw.common.model.Board]] of the [[EditorModelState]] to create
    * @param playableArea
    *   the [[it.unibo.pps.caw.common.model.PlayableArea]] of the [[EditorModelState]] to create
    * @return
    *   a new instance of [[EditorModelState]]
    */
  def apply(playableArea: PlayableArea)(dimensions: Dimensions)(board: Board[PlayableCell]): EditorModelState =
    LevelBuilderImpl(dimensions, board, Some(playableArea))

  /** Returns a new instance of [[EditorModelState]] given its [[it.unibo.pps.caw.common.model.Dimensions]] and its
    * [[it.unibo.pps.caw.common.model.Board]].
    *
    * @param dimensions
    *   the [[it.unibo.pps.caw.common.model.Dimensions]] of the [[EditorModelState]] to create
    * @param board
    *   the [[it.unibo.pps.caw.common.model.Board]] of the [[EditorModelState]] to create
    * @return
    *   a new instance of [[EditorModelState]]
    */
  def apply(dimensions: Dimensions)(board: Board[PlayableCell]): EditorModelState = LevelBuilderImpl(dimensions, board, None)

  /** Contains the extensions methods for the [[EditorModelState]] trait. */
  extension (builder: EditorModelState) {

    /** An extension method that returns a new [[EditorModelState]] whose characteristics are all or in part copied from the
      * current [[EditorModelState]]. By default, the new [[EditorModelState]] is a perfect copy of the current
      * [[EditorModelState]].
      *
      * @param dimensions
      *   the [[it.unibo.pps.caw.common.model.Dimensions]] of the [[EditorModelState]] to create
      * @param board
      *   the [[it.unibo.pps.caw.common.model.Board]] of the [[EditorModelState]] to create
      * @param playableArea
      *   the [[it.unibo.pps.caw.common.model.PlayableArea]] of the [[EditorModelState]] to create
      * @return
      *   a new instance of [[EditorModelState]] copied from this one
      */
    def copy(
      dimensions: Dimensions = builder.dimensions,
      board: Board[PlayableCell] = builder.board,
      playableArea: Option[PlayableArea] = builder.playableArea
    ): EditorModelState =
      LevelBuilderImpl(dimensions, board, playableArea)
  }
}
