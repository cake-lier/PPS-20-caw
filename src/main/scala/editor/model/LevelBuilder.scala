package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea}
import it.unibo.pps.caw.common.model.cell.PlayableCell

/** The level builder used by the editor.
  *
  * It provides the necessary functionalities to build, modify and retrieve a level and its structure while this is continously
  * edited by the user. It must be constructed through its companion object.
  */
trait LevelBuilder {

  /** Returns the [[Dimensions]] of this [[LevelBuilder]]. */
  val dimensions: Dimensions

  /** Returns the [[Board]] of this [[LevelBuilder]]. */
  val board: Board[PlayableCell]

  /** Returns the [[PlayableArea]] of this [[LevelBuilder]]. */
  val playableArea: Option[PlayableArea]
}

/** The companion object of the trait [[LevelBuilder]], containing its factory methods. */
object LevelBuilder {
  /* The case class implementaiton of LevelBuilder*/
  private case class LevelBuilderImpl(dimensions: Dimensions, board: Board[PlayableCell], playableArea: Option[PlayableArea])
    extends LevelBuilder

  /** Returns a new instance of [[LevelBuilder]] given its [[Dimensions]], its [[Board]] and its [[PlayableArea]].
    *
    * @param dimensions
    *   the [[Dimensions]] of the [[LevelBuilder]] to create
    * @param board
    *   the [[Board]] of the [[LevelBuilder]] to create
    * @param playableArea
    *   the [[PlayableArea]] of the [[LevelBuilder]] to create
    * @param playableArea
    *   the [[PlayableArea]] of the level
    * @return
    *   a new instance of [[LevelBuilder]]
    */
  def apply(playableArea: PlayableArea)(dimensions: Dimensions)(board: Board[PlayableCell]): LevelBuilder =
    LevelBuilderImpl(dimensions, board, Some(playableArea))

  /** Returns a new instance of [[LevelBuilder]] given its [[Dimensions]] and its [[Board]].
    *
    * @param dimensions
    *   the [[Dimensions]] of the [[LevelBuilder]] to create
    * @param board
    *   the [[Board]] of the [[LevelBuilder]] to create
    * @return
    *   a new instance of [[LevelBuilder]]
    */
  def apply(dimensions: Dimensions)(board: Board[PlayableCell]): LevelBuilder = LevelBuilderImpl(dimensions, board, None)

  /** Contains the extensions methods for the [[LevelBuilder]] trait. */
  extension (builder: LevelBuilder) {

    /** An extension method that returns a new [[LevelBuilder]] whose characteristics are all or in part copied from the current
      * [[LevelBuilder]]. By default, the new [[LevelBuilder]] is a perfect copy of the current [[LevelBuilder]].
      *
      * @param dimensions
      *   the [[Dimensions]] of the [[LevelBuilder]] to create
      * @param board
      *   the [[Board]] of the [[LevelBuilder]] to create
      * @param playableArea
      *   the [[PlayableArea]] of the [[LevelBuilder]] to create
      * @return
      *   a new instance of [[LevelBuilder]] copied from this one
      */
    def copy(
      dimensions: Dimensions = builder.dimensions,
      board: Board[PlayableCell] = builder.board,
      playableArea: Option[PlayableArea] = builder.playableArea
    ): LevelBuilder =
      LevelBuilderImpl(dimensions, board, playableArea)
  }
}
