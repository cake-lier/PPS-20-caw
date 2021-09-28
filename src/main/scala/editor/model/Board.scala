package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.editor.model.Cell

/** A group of cells in the game world.
  *
  * Every cell in the [[Board]] should have a unique [[Position]] because no two cells can be placed in the same [[Position]]. It
  * must be constructed through its companion object.
 *
  * @tparam A
  *   the type of [[Cell]] in this [[Board]]
  */
trait Board[A <: Cell] {

  /** Returns the cells in this [[Board]]. */
  val cells: Set[A]
}

/** Companion object of the [[Board]] trait, containing its factory methods. */
object Board {

  /* Default implementation of the Board trait. */
  private case class BoardImpl[A <: Cell](val cells: Set[A]) extends Board[A]

  /** Returns a new instance of the [[Board]] trait given the [[Cell]] that are contained into the [[Board]] itself. If two or
    * more [[Cell]] have the same [[Position]], only the first will be kept in the [[Board]].
    *
    * @param cells
    *   the [[Cell]] in the created [[Board]]
    * @tparam A
    *   the type of [[Cell]] in this [[Board]]
    * @return
    *   a new [[Board]] instance
    */
  def apply[A <: Cell](cells: A*): Board[A] = BoardImpl(cells.toSet)

  /** Returns a new instance of the [[Board]] trait given the [[Cell]] that are contained into the [[Board]] itself.
    *
    * @param cells
    *   the [[Cell]] in the created [[Board]]
    * @tparam A
    *   the type of [[Cell]] in this [[Board]]
    * @return
    *   a new [[Board]] instance
    */
  def apply[A <: Cell](cells: Set[A]): Board[A] = BoardImpl(cells)
  
  def empty[A <: Cell]: Board[A] = BoardImpl(Set.empty)
  
  given fromSetToBoard[A <: Cell]: Conversion[Set[A], Board[A]] = Board(_)
}