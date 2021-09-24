package it.unibo.pps.caw.common

/** Represent the coordinates of an item in the game */
trait Position {

  /** coordinate x of the item */
  def x: Int

  /** coordinate y of the item */
  def y: Int
}

/** the companion object of trait [[Position]] */
object Position {
  given Conversion[Tuple2[Int, Int], Position] = t => Position(t._1, t._2)
  private case class ItemPosition(x: Int, y: Int) extends Position
  def apply(x: Int, y: Int): Position = ItemPosition(x, y)
}
