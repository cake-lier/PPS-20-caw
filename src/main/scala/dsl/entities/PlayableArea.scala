package it.unibo.pps.caw.dsl.entities

/** The area in a [[Board]] where the player can drag the cells around and move them.
  *
  * This area is an area fully contained inside the [[Board]] but can be placed anywhere. For this to be true it is required that
  * it has [[Dimensions]] and a [[Position]] inside the [[Board]] itself. It must be constructed through its companion object.
  */
trait PlayableArea {

  /** Returns the [[Dimensions]] of this playable area. */
  val dimensions: Dimensions

  /** Returns the [[Position]] of this playable area. */
  val position: Position
}

/** Companion object of the [[PlayableArea]] trait, containing its factory method. */
object PlayableArea {
  
  /* Default implementation of the PlayableArea trait. */
  private case class PlayableAreaImpl(dimensions: Dimensions, position: Position) extends PlayableArea

  /** Returns a new instance of the [[PlayableArea]] trait.
    *
    * @param dimensions
    *   the [[Dimensions]] of the created playable area
    * @param position
    *   the [[Position]] of the created playable area
    * @return
    *   a new instance of the [[PlayableArea]] trait
    */
  def apply(dimensions: Dimensions)(position: Position): PlayableArea = PlayableAreaImpl(dimensions, position)
}
