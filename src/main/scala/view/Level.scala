package it.unibo.pps.caw
package view

import java.io.File

trait Level {
  val number: Int
  val file: File
}

object Level {
  private case class LevelImpl(number: Int, file: File) extends Level

  def apply(number: Int)(file: File): Level = LevelImpl(number, file)
}
