package it.unibo.pps.caw.editor.model

trait Board[A] {
  val cells: Set[A]
}

object Board {
  given Conversion[Set[_], Board[_]] = Board(_)
  private case class BoardImpl[A](val cells: Set[A]) extends Board[A]
  def empty[A]: Board[A] = BoardImpl(Set.empty)
  def apply[A](cells: A*): Board[A] = BoardImpl(cells.toSet)
  def apply[A](cells: Set[A]): Board[A] = BoardImpl(cells)
}
