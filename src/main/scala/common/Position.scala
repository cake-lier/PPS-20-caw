package it.unibo.pps.caw.common

import it.unibo.pps.caw.editor.model.Cell

/** The position of an entity in the game world.
  *
  * Being the world a two-dimensional grid, the [[Position]] is represented by two coordinates: the x coordinate and the y
  * coordinate, also known as "column" and "row", in this order. Again, following the idea of a grid, the coordinates can only be
  * integer non-negative numbers. Differently from the standard Carthesian plane, the y coordinate grows moving torwards the
  * bottom and not moving torwards the top. This means that [[Position]] with a greater y coordinate are in fact lower in the area
  * in which they are contained. It must be constructed through its companion object.
  */
trait Position extends Ordered[Position] {

  /** Returns the x coordinate of this [[Position]]. */
  val x: Int

  /** Returns the y coordinate of this [[Position]] */
  val y: Int

  override def compare(that: Position): Int = (x - that.x) + (y - that.y)
}

/** Companion object of trait [[Position]], containing its factory method. */
object Position {

  /* Default implementation of the Position trait. */
  private case class PositionImpl(x: Int, y: Int) extends Position

  /** Returns a new [[Position]] instance given its coordinates.
    *
    * @param x
    *   the x coordinate of the created [[Position]]
    * @param y
    *   the y coordinate of the created [[Position]]
    * @return
    *   a new [[Position]] instance
    */
  def apply(x: Int, y: Int): Position = PositionImpl(x, y)

  /** Converts a tuple of two integers into a [[Position]]. */
  given Conversion[Tuple2[Int, Int], Position] = t => Position(t._1, t._2)
}
