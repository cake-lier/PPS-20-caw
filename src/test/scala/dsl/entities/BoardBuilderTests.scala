package it.unibo.pps.caw.dsl.entities

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[BoardBuilder]] trait. */
class BoardBuilderTests extends AnyFunSpec with Matchers {
  private val position: Position = Position(1, 2)
  private val dimensions: Dimensions = Dimensions(10, 20)

  describe("A board builder") {
    describe("when first created") {
      it("should be empty") {
        val board: BoardBuilder = BoardBuilder()
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
        val board: BoardBuilder = BoardBuilder(dimensions = Some(dimensions))
        board.dimensions should contain(dimensions)
      }
    }

    describe("when the playable area is set") {
      it("should return the given playable area") {
        val playableArea: PlayableArea = PlayableArea(dimensions)(position)
        val board: BoardBuilder = BoardBuilder(playableArea = Some(playableArea))
        board.playableArea should contain(playableArea)
      }
    }

    describe("when a mover cell is added") {
      it("should contain the added mover cell") {
        val moverCell: OrientableCell = OrientableCell(Orientation.Right)(position)
        val board: BoardBuilder = BoardBuilder(moverCells = Set(moverCell))
        board.moverCells should contain(moverCell)
      }
    }

    describe("when a generator cell is added") {
      it("should contain the added generator cell") {
        val generatorCell: OrientableCell = OrientableCell(Orientation.Right)(position)
        val board: BoardBuilder = BoardBuilder(generatorCells = Set(generatorCell))
        board.generatorCells should contain(generatorCell)
      }
    }

    describe("when a rotator cell is added") {
      it("should contain the added rotator cell") {
        val rotatorCell: RotatableCell = RotatableCell(Rotation.Clockwise)(position)
        val board: BoardBuilder = BoardBuilder(rotatorCells = Set(rotatorCell))
        board.rotatorCells should contain(rotatorCell)
      }
    }

    describe("when a block cell is added") {
      it("should contain the added block cell") {
        val blockCell: PushableCell =
          PushableCell(Push.Vertical)(position)
        val board: BoardBuilder = BoardBuilder(blockCells = Set(blockCell))
        board.blockCells should contain(blockCell)
      }
    }

    describe("when a enemy cell is added") {
      it("should contain the added enemy cell") {
        val enemyCell: Cell = Cell(position)
        val board: BoardBuilder = BoardBuilder(enemyCells = Set(enemyCell))
        board.enemyCells should contain(enemyCell)
      }
    }

    describe("when a wall cell is added") {
      it("should contain the added wall cell") {
        val wallCell: Cell = Cell(position)
        val board: BoardBuilder = BoardBuilder(wallCells = Set(wallCell))
        board.wallCells should contain(wallCell)
      }
    }

    describe("when created by copy constructor") {
      it("should have the same properties of the cloned board") {
        val board: BoardBuilder = BoardBuilder(dimensions = Some(dimensions), playableArea = Some(PlayableArea(dimensions)(position)))
        val newBoard: BoardBuilder = board.copy()
        newBoard shouldBe board
      }
    }
  }
}
