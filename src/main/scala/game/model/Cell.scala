package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.{Board, PlayableArea, Position}

/** A cell, an element of a [[Board]] in the game.
  *
  * The [[Cell]] represents the atomic entity of the game, its smallest component. A [[Cell]] is alive and, being so, it has a
  * behavior that keeps to express until it dies and is removed from the game. There are different types of [[Cell]], each with
  * its peculiar behavior. The [[Cell]] are what the user manipulates in the game for winning a [[Level]] and advance to the next
  * until all [[Level]] are completed. A [[Cell]] necessarily has a [[Position]] in a [[Board]], because [[Board]] is what
  * contains them, and no two distinct [[Cell]] can share the same [[Position]].
  */
trait Cell extends Ordered[Cell] {

  /** Returns the [[Position]] of this [[Cell]] into the [[Board]] in which is inserted. */
  val position: Position
  
  final override def compare(that: Cell): Int = (position.x - that.position.x) + (position.y - that.position.y)

  final override def equals(obj: Any): Boolean = obj match {
    case c: Cell => position == c.position
    case _       => false
  }

  final override def hashCode(): Int = position.hashCode()
}

/** The rotator [[Cell]], the [[Cell]] that can rotate other [[Cell]].
  *
  * This [[Cell]] can rotate other [[Cell]] which have a specific [[Orientation]] or [[Push]] so as to make them assume another
  * value [[Orientation]] or [[Push]], depending if the [[Rotation]] is clockwise or counterclockwise. For example, under a
  * clockwise [[Rotation]], a right [[Orientation]] will become a bottom [[Orientation]], an horizontal [[Push]] a vertical one
  * and so on and so forth. Block [[Cell]] with a [[Push]] in both direction will not be affected. Under a counterclockwise
  * [[Rotation]] the vice versa is valid. It must be constructed through its companion object.
  */
trait RotatorCell extends Cell {

  /** Returns the direction of rotation of this [[BaseRotatorCell]]. */
  val rotation: Rotation
}

trait GeneratorCell extends Cell {
  val orientation: Orientation
}

/** Represent the enemy [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
trait EnemyCell extends Cell

/** Represent the mover [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param orientation
  *   the [[Orientation]] in the area
  */
trait MoverCell extends Cell {
  val orientation: Orientation
}

/** Represent the block [[Cell]]
  *
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  * @param push
  *   the [[Push]]
  */
trait BlockCell extends Cell {
  val push: Push
}

/** Represent the wall [[Cell]]
  * @param position
  *   the coordinates of the cell
  * @param playable
  *   if the cell is playable (is in the [[PlayableArea]])
  */
trait WallCell extends Cell

