package it.unibo.pps.caw
package common.model

import common.model.cell.*

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[Level]] trait */
class LevelTest extends AnyFunSpec with Matchers {
  private val dimensions: Dimensions = Dimensions(10, 8)
  private val board: Board[BaseCell] = Board(
    BaseMoverCell(Orientation.Right)((3, 2)),
    BaseWallCell((1, 1)),
    BaseEnemyCell((9, 7))
  )
  private val playableArea: PlayableArea = PlayableArea((7, 5))((1, 1))

  describe("A Level") {
    describe("when first created") {
      it("should return the given dimensions, board and playable area") {
        val level: Level[BaseCell] = Level(dimensions, board, playableArea)
        level.dimensions shouldBe dimensions
        level.board shouldBe board
        level.playableArea shouldBe playableArea
      }
    }

    describe("when asked to produce a modified copy of itself") {
      it("should return a new level with the same properties as the original, unless specified otherwise") {
        val level: Level[BaseCell] = Level(dimensions, board, playableArea)
        val newDimensions: Dimensions = Dimensions(11, 11)
        val newBoard: Board[BaseCell] = Board(
          BaseMoverCell(Orientation.Top)((3, 2)),
          BaseEnemyCell((7, 7))
        )
        val newPlayableArea: PlayableArea = PlayableArea((4, 4))((2, 2))

        level.copy(dimensions = newDimensions) shouldBe Level(newDimensions, board, playableArea)
        level.copy(board = newBoard, playableArea = newPlayableArea) shouldBe Level(dimensions, newBoard, newPlayableArea)
      }
    }
  }
}
