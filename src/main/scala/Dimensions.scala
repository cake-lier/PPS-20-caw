package it.unibo.pps.caw.dsl

trait Dimensions {
  val width: Int

  val height: Int
}

object Dimensions {
  private case class DimensionableImpl(width: Int, height: Int)
      extends Dimensions

  def apply(width: Int, height: Int): Dimensions =
    DimensionableImpl(width, height)
}
