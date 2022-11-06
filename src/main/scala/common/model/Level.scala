package it.unibo.pps.caw
package common.model

import common.model.cell.Cell

/** A level of the game, with its structure and its cells.
  *
  * A [[Level]] is a part of the game in which this is divided. Every level is characterized by its [[Dimensions]], its
  * [[PlayableArea]] and the [[it.unibo.pps.caw.common.model.cell.Cell]] which are part of the [[Board]] of this level. Every cell
  * is fully contained into the level and the same goes for the playable area. Moreover, if a cell is also contained into the
  * playable area is then a "playable" cell, which means that it can be manipulated by the player and mover in another
  * [[Position]] inside the playable area. It must be constructed through its companion object.
  *
  * @tparam A
  *   the type of [[it.unibo.pps.caw.common.model.cell.Cell]] inside the [[Board]] which is part of this [[Level]]
  */
trait Level[A <: Cell] {

  /** Returns the [[Dimensions]] of this [[Level]]. */
  val dimensions: Dimensions

  /** Returns the [[Board]] which is part of this [[Level]]. */
  val board: Board[A]

  /** Returns the [[PlayableArea]] of this [[Level]]. */
  val playableArea: PlayableArea
}

/** Companion object of the [[Level]] trait, containing its factory method. */
object Level {

  /* Default implementation of the LevelBuilderState trait. */
  private case class LevelImpl[A <: Cell](dimensions: Dimensions, board: Board[A], playableArea: PlayableArea) extends Level[A]

  /** Returns a new instance of the [[Level]] trait given its [[Dimensions]], its [[Board]] of
    * [[it.unibo.pps.caw.common.model.cell.Cell]] and its [[PlayableArea]].
    *
    * @param dimensions
    *   the [[Dimensions]] of the created [[Level]]
    * @param board
    *   the [[Board]] of [[it.unibo.pps.caw.common.model.cell.Cell]] of the created [[Level]]
    * @param playableArea
    *   the [[PlayableArea]] of the created [[Level]]
    * @tparam A
    *   the type of [[it.unibo.pps.caw.common.model.cell.Cell]] inside the [[Board]] which is part of this [[Level]]
    * @return
    *   a new [[Level]] instance
    */
  def apply[A <: Cell](dimensions: Dimensions, board: Board[A], playableArea: PlayableArea): Level[A] =
    LevelImpl(dimensions, board, playableArea)

  /** Contains extension methods to the [[Level]] trait. */
  extension [A <: Cell](l: Level[A]) {

    /** Copy constructor of the [[Level]] trait for creating a new instance copying the already created one and modifying its
      * properties with the given values for its [[Dimensions]], its [[Board]] of [[it.unibo.pps.caw.common.model.cell.Cell]] and
      * its [[PlayableArea]].
      *
      * @param dimensions
      *   the [[Dimensions]] of the created [[Level]]
      * @param board
      *   the [[Board]] of [[it.unibo.pps.caw.common.model.cell.Cell]] of the created [[Level]]
      * @param playableArea
      *   the [[PlayableArea]] of the created [[Level]]
      * @return
      *   a new [[Level]] instance copied from the given one
      */
    def copy(
      dimensions: Dimensions = l.dimensions,
      board: Board[A] = l.board,
      playableArea: PlayableArea = l.playableArea
    ): Level[A] =
      LevelImpl(dimensions, board, playableArea)
  }
}
