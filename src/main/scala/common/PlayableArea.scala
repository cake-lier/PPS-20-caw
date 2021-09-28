package it.unibo.pps.caw.common

import it.unibo.pps.caw.game.model.{Cell, Level}

/** The area of a [[Level]] on which the player can move [[Cell]].
  *
  * The [[PlayableArea]] is a special area in a [[Level]] where the [[Cell]] inside it can be moved by the player to another
  * [[Position]] inside the the area itself. Being an area like the area of the whole game, and being a part of the game world, it
  * is represented by a two-dimensional grid and, because of this, it has specific [[Dimensions]]. Furthermore, being only a part
  * of the game word, it needs to be located at a given [[Position]]. The chosen [[Position]] for representing the
  * [[PlayableArea]] is the upper left corner of the area itself. It must be constructed through its companion object.
  */
trait PlayableArea {

  /** Returns the [[Dimensions]] of this [[PlayableArea]]. */
  val dimensions: Dimensions

  /** Returns the [[Position]] of the upper left corner of this [[PlayableArea]]. */
  val position: Position
}

/** Companion object of the [[PlayableArea]] trait, containing its factory method. */
object PlayableArea {

  /* Default implementation of the PlayableArea trait. */
  private case class PlayableAreaImpl(position: Position, dimensions: Dimensions) extends PlayableArea

  /** Returns a new instance of the [[PlayableArea]] trait given its [[Dimensions]] and the [[Position]] of its upper left corner.
    *
    * @param position
    *   the [[Position]] of the upper left corner of the created [[PlayableArea]]
    * @param dimensions
    *   the [[Dimensions]] of the created [[PlayableArea]]
    * @return
    *   a new [[PlayableArea]] instance
    */
  def apply(position: Position, dimensions: Dimensions): PlayableArea = PlayableAreaImpl(position, dimensions)
}
