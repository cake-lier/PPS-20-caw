package it.unibo.pps.caw
package model

trait Board {
  val cells: Set[Cell]
}

object Board {
  private case class BoardImpl(cells: Set[Cell]) extends Board
  def apply(cells: Cell*): Board = BoardImpl(cells.toSet)
  def apply(cells: Set[Cell]): Board = BoardImpl(cells)
}

trait SetupBoard {
  val cells: Set[SetupCell]
}

object SetupBoard {
  private case class BoardImpl(cells: Set[SetupCell]) extends SetupBoard
  def apply(cells: SetupCell*): SetupBoard = BoardImpl(cells.toSet)
  def apply(cells: Set[SetupCell]): SetupBoard = BoardImpl(cells)
}
