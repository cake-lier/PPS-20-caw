package it.unibo.pps.caw.dsl

trait Positionable {
  val x: Option[Int]
  
  val y: Option[Int]
}

object Positionable {
  private case class PositionableImpl(x: Option[Int], y: Option[Int]) extends Positionable
  
  def apply(x: Int, y: Int): Positionable = PositionableImpl(Some(x), Some(y))
  
  def apply(): Positionable = PositionableImpl(None, None)
}
