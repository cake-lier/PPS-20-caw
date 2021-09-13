package it.unibo.pps.caw
package model

/** Represent the coordinates of an item in the game */
trait Position {

  /** coordinate x of the item */
  val x: Int

  /** coordinate y of the item */
  val y: Int
}

/** the companion object of trait [[Position]] */
object Position {
  given Conversion[Tuple2[Int, Int], Position] = t => Position(t._1, t._2)
  private case class ItemPosition(x: Int, y: Int) extends Position

  def apply(x: Int, y: Int): Position = ItemPosition(x, y)
}
