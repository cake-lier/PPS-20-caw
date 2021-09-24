package it.unibo.pps.caw.common

import it.unibo.pps.caw.game.model.Cell

/** Represent the concept of a game area */
trait Area {

  /** the width of the area */
  def width: Int

  /** the height of the area */
  def height: Int
}

/** Companion object of trait [[Area]] */
object Area {
  private case class AreaImpl(width: Int, height: Int) extends Area
  def apply(width: Int, height: Int): Area = AreaImpl(width, height)
}

/** Represent the area in which one the player can move [[Cell]] on it */
trait PlayableArea extends Area {

  /** top-left area coordinates */
  def position: Position
}

/** Companion object of trait [[PlayableArea]] */
object PlayableArea {
  private case class PlayableAreaImpl(position: Position, width: Int, height: Int) extends PlayableArea
  def apply(position: Position, width: Int, height: Int): PlayableArea =
    PlayableAreaImpl(position, width, height)
}
