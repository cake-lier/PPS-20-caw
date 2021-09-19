package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.editor.model.Position
import it.unibo.pps.caw.game.model.Position

/** Represent the coordinates of an item in the game */
trait Position {

  /** coordinate x of the item */
  def x: Int

  /** coordinate y of the item */
  def y: Int
}

/** the companion object of trait [[Position]] */
object Position {
  private case class ItemPosition(x: Int, y: Int) extends Position
  def apply(x: Int, y: Int): Position = ItemPosition(x, y)
}
