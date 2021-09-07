package it.unibo.pps.caw.dsl

trait PlayableArea extends Dimensionable with Positionable

object PlayableArea {
  private case class PlayableAreaImpl(
      width: Option[Int],
      height: Option[Int],
      x: Option[Int],
      y: Option[Int]
  ) extends PlayableArea

  def apply(width: Int, height: Int)(x: Int, y: Int): PlayableArea =
    PlayableAreaImpl(Some(width), Some(height), Some(x), Some(y))

  def apply(): PlayableArea = PlayableAreaImpl(None, None, None, None)
}
