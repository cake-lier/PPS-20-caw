package it.unibo.pps.caw
package model

trait Position{
  def x: Int
  def y: Int
}

object Position{
  private case class ItemPosition(x: Int, y: Int) extends Position
  def apply(x: Int, y: Int): Position = ItemPosition(x,y)
}

