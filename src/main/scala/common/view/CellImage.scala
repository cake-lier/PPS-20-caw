package it.unibo.pps.caw.common.view

import javafx.scene.image.Image

/** The images used to draw a [[it.unibo.pps.caw.common.model.Level]].
  *
  * This enumeration is used to list all the different images used to draw a [[it.unibo.pps.caw.common.model.Level]] and to store
  * and retrieve the [[Image]] for the given enumeration.
  */
enum CellImage(val imageName: String) {
  val image: Image = Image("imgs/" + imageName + ".png")

  /** The image of the enemy. */
  case Enemy extends CellImage("enemy")

  /** The image of the clockwise rotator. */
  case RotatorClockwise extends CellImage("rotator_clockwise")

  /** The image of the counterclockwise rotator. */
  case RotatorCounterclockwise extends CellImage("rotator_counterclockwise")

  /** The image of the right mover. */
  case MoverRight extends CellImage("mover_right")

  /** The image of the left mover. */
  case MoverLeft extends CellImage("mover_left")

  /** The image of the top mover. */
  case MoverTop extends CellImage("mover_top")

  /** The image of the down. */
  case MoverDown extends CellImage("mover_down")

  /** The image of the block. */
  case Block extends CellImage("block")

  /** The image of the horizontal block . */
  case BlockHorizontal extends CellImage("block_horizontal")

  /** The image of the vertical block. */
  case BlockVertical extends CellImage("block_vertical")

  /** The image of the wall. */
  case Wall extends CellImage("wall")

  /** The image of the right generator. */
  case GeneratorRight extends CellImage("generator_right")

  /** The image of the left generator. */
  case GeneratorLeft extends CellImage("generator_left")

  /** The image of the top generator. */
  case GeneratorTop extends CellImage("generator_top")

  /** The image of the down generator. */
  case GeneratorDown extends CellImage("generator_down")

  /** The image of the deleter. */
  case Deleter extends CellImage("deleter")

  /** The image of the default tile. */
  case DefaultTile extends CellImage("default")

  /** The image of the playable area tile. */
  case PlayAreaTile extends CellImage("play_area")
}
