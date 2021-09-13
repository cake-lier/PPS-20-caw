package it.unibo.pps.caw.dsl.entities

/** The dimensions of an entity in the game.
  *
  * In this game, all dimensionable entities are thought to be two-dimensional and aligned with a grid defined by the [[Board]]
  * itself. This means that the dimensions could only be two and could only be integer ones. It must be constructed through its
  * companion object.
  */
trait Dimensions {

  /** Returns the width of the entity. */
  val width: Int

  /** Returns the height of the entity. */
  val height: Int
}

/** Companion object of the [[Dimensions]] trait, containing its factory method. */
object Dimensions {
  
  /* Default implementation of the Dimensions trait. */
  private case class DimensionsImpl(width: Int, height: Int) extends Dimensions

  /** Returns a new instance of the [[Dimensions]] trait.
    *
    * @param width
    *   the width dimension of the created dimensions instance
    * @param height
    *   the height dimension of the create dimensions instance
    * @return
    *   a new instance of the [[Dimensions]] trait
    */
  def apply(width: Int, height: Int): Dimensions = DimensionsImpl(width, height)
}
