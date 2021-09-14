package it.unibo.pps.caw.dsl.entities

/** The position of an entity in the game.
  *
  * In this game, all positionable entities are thought to be two-dimensional and aligned with a grid defined by the [[Board]]
  * itself. This means that the position coordinates could only be two and could only be integer ones. It must be constructed
  * through its companion object.
  */
trait Position {

  /** Returns the x coordinate of this position. */
  val x: Int

  /** Returns the y coordinate of this position. */
  val y: Int
}

/** Companion object of the [[Position]] trait, containing its factory method. */
object Position {
  
  /* Default implementation of the Position trait. */
  private case class PositionImpl(x: Int, y: Int) extends Position

  /** Returns a new instance of the [[Position]] trait.
    *
    * @param x
    *   the x coordinate of the created position
    * @param y
    *   the y coordinate of the created position
    * @return
    *   a new instance of the [[Position]] trait
    */
  def apply(x: Int, y: Int): Position = PositionImpl(x, y)
}
