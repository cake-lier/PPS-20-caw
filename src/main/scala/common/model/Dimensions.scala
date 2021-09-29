package it.unibo.pps.caw.common.model

/** The dimensions of an area into the game world.
  *
  * Being the game world represented by a two-dimensional grid, every area in the game must also be a two-dimensional grid. This
  * means that the [[Dimensions]] of an area can be represented by just a width and a height. It must be constructed through its
  * companion object.
  */
trait Dimensions {

  /** Returns the width component of these [[Dimensions]]. */
  val width: Int

  /** Returns the height component of these [[Dimensions]]. */
  val height: Int
}

/** Companion object of [[Dimensions]] trait, containing its factory method. */
object Dimensions {

  /* Default implementation of the Dimensions trait. */
  private case class DimensionsImpl(width: Int, height: Int) extends Dimensions

  /** Returns a new instance of the [[Dimensions]] trait given the width and the height that represents those [[Dimensions]].
    *
    * @param width
    *   the width component of the created [[Dimensions]]
    * @param height
    *   the height component of the created [[Dimensions]]
    * @return
    *   a new [[Dimensions]] instance
    */
  def apply(width: Int, height: Int): Dimensions = DimensionsImpl(width, height)

  /** Converts a tuple of two integers into a [[Dimensions]]. */
  given Conversion[Tuple2[Int, Int], Dimensions] = t => Dimensions(t._1, t._2)
}
