package it.unibo.pps.caw
package view.game

import javafx.scene.image.Image

/** Provides all the images necessary to draw a board.
  *
  * The singleton provides a map that returns its [[Image]] given the enum key [[Images.CellsImage]].
  */
object Images {
  import CellsImage._
  val images: Map[CellsImage, Image] = Map(
    (Block, createImage(Block.getType)),
    (BlockHorizontal, createImage(BlockHorizontal.getType)),
    (BlockVertical, createImage(BlockVertical.getType)),
    (GeneratorDown, createImage(GeneratorDown.getType)),
    (GeneratorLeft, createImage(GeneratorLeft.getType)),
    (GeneratorRight, createImage(GeneratorRight.getType)),
    (GeneratorTop, createImage(GeneratorTop.getType)),
    (MoverDown, createImage(MoverDown.getType)),
    (MoverLeft, createImage(MoverLeft.getType)),
    (MoverRight, createImage(MoverRight.getType)),
    (MoverTop, createImage(MoverTop.getType)),
    (RotatorLeft, createImage(RotatorLeft.getType)),
    (RotatorRight, createImage(RotatorRight.getType)),
    (Wall, createImage(Wall.getType)),
    (Enemy, createImage(Enemy.getType)),
    (DefaultTile, createImage(DefaultTile.getType)),
    (PlayAreaTile, createImage(PlayAreaTile.getType))
  )

  /** Represents all the possible cells. */
  enum CellsImage(cellType: String) {
    case Enemy extends CellsImage("enemy")
    case RotatorRight extends CellsImage("rotator_right")
    case RotatorLeft extends CellsImage("rotator_left")
    case MoverRight extends CellsImage("mover_right")
    case MoverLeft extends CellsImage("mover_left")
    case MoverTop extends CellsImage("mover_top")
    case MoverDown extends CellsImage("mover_down")
    case Block extends CellsImage("block")
    case BlockHorizontal extends CellsImage("block_horizontal")
    case BlockVertical extends CellsImage("block_vertical")
    case Wall extends CellsImage("wall")
    case GeneratorRight extends CellsImage("generator_right")
    case GeneratorLeft extends CellsImage("generator_left")
    case GeneratorTop extends CellsImage("generator_top")
    case GeneratorDown extends CellsImage("generator_down")
    case DefaultTile extends CellsImage("default")
    case PlayAreaTile extends CellsImage("play_area")

    /** Getter of [[CellsImage]] value
      * @return
      *   the value as string
      */
    def getType = cellType
  }

  private def createImage(imageName: String): Image = new Image("imgs/" + imageName + ".png")
}
