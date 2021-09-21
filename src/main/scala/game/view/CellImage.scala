package it.unibo.pps.caw.game.view

import javafx.scene.image.Image

/** Provides all the images necessary to draw a board.
  *
  * The singleton provides a map that returns its [[Image]] given the enum key [[Images.CellImage]].
  */
enum CellImage(imageName: String) {
  val image: Image = Image("imgs/" + imageName + ".png")

  case Enemy extends CellImage("enemy")
  case RotatorRight extends CellImage("rotator_right")
  case RotatorLeft extends CellImage("rotator_left")
  case MoverRight extends CellImage("mover_right")
  case MoverLeft extends CellImage("mover_left")
  case MoverTop extends CellImage("mover_top")
  case MoverDown extends CellImage("mover_down")
  case Block extends CellImage("block")
  case BlockHorizontal extends CellImage("block_horizontal")
  case BlockVertical extends CellImage("block_vertical")
  case Wall extends CellImage("wall")
  case GeneratorRight extends CellImage("generator_right")
  case GeneratorLeft extends CellImage("generator_left")
  case GeneratorTop extends CellImage("generator_top")
  case GeneratorDown extends CellImage("generator_down")
  case DefaultTile extends CellImage("default")
  case PlayAreaTile extends CellImage("play_area")
}
