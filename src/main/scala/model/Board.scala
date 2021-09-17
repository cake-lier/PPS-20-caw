package it.unibo.pps.caw
package model

import model.Board.BoardImpl

trait Board[A]{
  val cells: Set[A]
}

object Board{
  case class BoardImpl[A](val cells: Set[A]) extends Board[A]
  def apply[A](cells: A*): Board[A] = BoardImpl(cells.toSet)
  def apply[A](cells: Set[A]): Board[A] = BoardImpl(cells)
}
