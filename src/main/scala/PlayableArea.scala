package it.unibo.pps.caw.dsl

trait PlayableArea extends Dimensions with Position

object PlayableArea {
  private case class PlayableAreaImpl(width: Int, height: Int, x: Int, y: Int) extends PlayableArea

  def apply(width: Int, height: Int)(x: Int, y: Int): PlayableArea = PlayableAreaImpl(width, height, x, y)
}
