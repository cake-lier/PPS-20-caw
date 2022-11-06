package it.unibo.pps.caw
package common.model

import common.model.cell.*

/** A group of cells in the game world.
  *
  * Every cell in the [[Board]] should have a unique [[Position]] because no two cells can be placed in the same [[Position]]. It
  * must be constructed through its companion object.
  *
  * @tparam A
  *   the type of [[it.unibo.pps.caw.common.model.cell.Cell]] in this [[Board]]
  */
trait Board[A <: Cell] {

  /** Returns the cells in this [[Board]]. */
  val cells: Set[A]
}

/** Companion object of the [[Board]] trait, containing its factory methods. */
object Board {

  /* Default implementation of the Board trait. */
  private case class BoardImpl[A <: Cell](cells: Set[A]) extends Board[A]

  /** Returns a new instance of the [[Board]] trait given the [[it.unibo.pps.caw.common.model.cell.Cell]] that are contained into
    * the [[Board]] itself. If two or more [[it.unibo.pps.caw.common.model.cell.Cell]] have the same [[Position]], only the first
    * will be kept in the [[Board]].
    *
    * @param cells
    *   the [[it.unibo.pps.caw.common.model.cell.Cell]] in the created [[Board]]
    * @tparam A
    *   the type of [[it.unibo.pps.caw.common.model.cell.Cell]] in this [[Board]]
    * @return
    *   a new [[Board]] instance
    */
  def apply[A <: Cell](cells: A*): Board[A] = BoardImpl(cells.toSet)

  /** Returns a new instance of the [[Board]] trait given the [[it.unibo.pps.caw.common.model.cell.Cell]] that are contained into
    * the [[Board]] itself.
    *
    * @param cells
    *   the [[it.unibo.pps.caw.common.model.cell.Cell]] in the created [[Board]]
    * @tparam A
    *   the type of [[it.unibo.pps.caw.common.model.cell.Cell]] in this [[Board]]
    * @return
    *   a new [[Board]] instance
    */
  def apply[A <: Cell](cells: Set[A]): Board[A] = BoardImpl(cells)

  /** Returns a new instance of the [[Board]] trait which is empty.
    *
    * @tparam A
    *   the type of [[it.unibo.pps.caw.common.model.cell.Cell]] in this [[Board]]
    * @return
    *   a new empty [[Board]] instance
    */
  def empty[A <: Cell]: Board[A] = BoardImpl(Set.empty)

  /** Converts a [[Set]] into a [[Board]] by wrapping it into a new one. */
  given fromSetToBoard[A <: Cell]: Conversion[Set[A], Board[A]] = Board(_)

  /** Converts a [[Board]] into a [[Set]] by unwrapping its contents. */
  given fromBoardToSet[A <: Cell]: Conversion[Board[A], Set[A]] = _.cells
}
