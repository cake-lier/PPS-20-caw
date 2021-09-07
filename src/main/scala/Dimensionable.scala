package it.unibo.pps.caw.dsl

trait Dimensionable {
  val width: Option[Int]

  val height: Option[Int]
}

object Dimensionable {
  private case class DimensionableImpl(width: Option[Int], height: Option[Int])
      extends Dimensionable

  def apply(width: Int, height: Int): Dimensionable =
    DimensionableImpl(Some(width), Some(height))

  def apply(): Dimensionable = DimensionableImpl(None, None)
}
