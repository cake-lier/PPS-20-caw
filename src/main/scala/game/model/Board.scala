package it.unibo.pps.caw.game.model

trait Board[A] {
  val cells: Set[A]
}

object Board {
  private case class BoardImpl[A](val cells: Set[A]) extends Board[A]
  def apply[A](cells: A*): Board[A] = BoardImpl(cells.toSet)
  def apply[A](cells: Set[A]): Board[A] = BoardImpl(cells)
}
