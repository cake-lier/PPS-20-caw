package it.unibo.pps.caw.dsl

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class BoardTests extends AnyFunSpec with Matchers {
  describe("A board") {
    describe("when first created") {
      it("should be empty") {
        val board: Board = Board()
        board.dimensions shouldBe empty
        board.playableArea shouldBe empty
        board.moverCells shouldBe empty
        board.generatorCells shouldBe empty
        board.rotatorCells shouldBe empty
        board.blockCells shouldBe empty
        board.enemyCells shouldBe empty
        board.wallCells shouldBe empty
      }
    }

    describe("when dimensions are set") {
      it("should return the given dimensions") {
        val dimensions: Dimensions = Dimensions(10, 20)
        val board: Board = Board(dimensions = Some(dimensions))
        board.dimensions should contain(dimensions)
      }
    }

    describe("when the playable area is set") {
      it("should return the given playable area") {
        val playableArea: PlayableArea = PlayableArea(10, 20)(1, 2)
        val board: Board = Board(playableArea = Some(playableArea))
        board.playableArea should contain(playableArea)
      }
    }

    describe("when a mover cell is added") {
      it("should contain the added mover cell") {
        val moverCell: OrientedCell = OrientedCell(Orientation.Right, 1, 2)
        val board: Board = Board(moverCells = Set(moverCell))
        board.moverCells should contain(moverCell)
      }
    }

    describe("when a generator cell is added") {
      it("should contain the added generator cell") {
        val generatorCell: OrientedCell = OrientedCell(Orientation.Right, 1, 2)
        val board: Board = Board(generatorCells = Set(generatorCell))
        board.generatorCells should contain(generatorCell)
      }
    }

    describe("when a rotator cell is added") {
      it("should contain the added rotator cell") {
        val rotatorCell: DirectedCell = DirectedCell(Direction.Clockwise, 1, 2)
        val board: Board = Board(rotatorCells = Set(rotatorCell))
        board.rotatorCells should contain(rotatorCell)
      }
    }

    describe("when a block cell is added") {
      it("should contain the added block cell") {
        val blockCell: MovableCell =
          MovableCell(MovementDirection.Vertical, 1, 2)
        val board: Board = Board(blockCells = Set(blockCell))
        board.blockCells should contain(blockCell)
      }
    }

    describe("when a enemy cell is added") {
      it("should contain the added enemy cell") {
        val enemyCell: Cell = Cell(1, 2)
        val board: Board = Board(enemyCells = Set(enemyCell))
        board.enemyCells should contain(enemyCell)
      }
    }

    describe("when a wall cell is added") {
      it("should contain the added wall cell") {
        val wallCell: Cell = Cell(1, 2)
        val board: Board = Board(wallCells = Set(wallCell))
        board.wallCells should contain(wallCell)
      }
    }
  }
}
