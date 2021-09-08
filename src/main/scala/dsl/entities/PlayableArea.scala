package it.unibo.pps.caw.dsl.entities

trait PlayableArea {
  val dimensions: Dimensions
  
  val position: Position
}

object PlayableArea {
  private case class PlayableAreaImpl(dimensions: Dimensions, position: Position) extends PlayableArea

  def apply(dimensions: Dimensions)(position: Position): PlayableArea = PlayableAreaImpl(dimensions, position)
}
