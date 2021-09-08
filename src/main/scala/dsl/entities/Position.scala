package it.unibo.pps.caw.dsl.entities

trait Position {
  val x: Int

  val y: Int
}

object Position {
  private case class PositionableImpl(x: Int, y: Int) extends Position

  def apply(x: Int, y: Int): Position = PositionableImpl(x, y)
}
