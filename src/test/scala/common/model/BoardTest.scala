package it.unibo.pps.caw.common.model

import it.unibo.pps.caw.common.model.cell._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[Board]] trait */
class BoardTest extends AnyFunSpec with Matchers {
  private val baseCells = Set(BaseMoverCell(Orientation.Right)((3, 2)), BaseWallCell((1, 1)), BaseEnemyCell((9, 7)))
  private val playableCells = Set(
    PlayableMoverCell(Orientation.Right)((3, 2))(playable = true),
    PlayableGeneratorCell(Orientation.Down)((1, 1))(playable = true),
    PlayableDeleterCell((9, 7))(playable = false)
  )

  describe("A Board") {
    describe("when first created") {
      it("should be empty") {
        Board().cells shouldBe empty
        Board.empty.cells shouldBe empty
      }
    }

    describe("when created with a set of cells") {
      it("should return the given cells") {
        val board = Board(baseCells)
        val pBoard = Board(playableCells)
        board.cells shouldBe baseCells
        pBoard.cells shouldBe playableCells
      }
    }

    describe("when created directly with cells") {
      it("should return the given cells") {
        val board = Board(BaseMoverCell(Orientation.Right)((3, 2)), BaseWallCell((1, 1)), BaseEnemyCell((9, 7)))
        val pBoard = Board(
          PlayableMoverCell(Orientation.Right)((3, 2))(playable = true),
          PlayableGeneratorCell(Orientation.Down)((1, 1))(playable = true),
          PlayableDeleterCell((9, 7))(playable = false)
        )
        board.cells shouldBe baseCells
        pBoard.cells shouldBe playableCells
      }
    }
  }
}
