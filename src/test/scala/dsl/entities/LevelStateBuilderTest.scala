package it.unibo.pps.caw.dsl.entities

import it.unibo.pps.caw.common.model.{Dimensions, PlayableArea, Position}
import it.unibo.pps.caw.common.model.cell.{
  BaseBlockCell,
  BaseEnemyCell,
  BaseGeneratorCell,
  BaseMoverCell,
  BaseRotatorCell,
  BaseWallCell,
  Orientation,
  Push,
  Rotation
}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[LevelBuilderState]] trait. */
class LevelStateBuilderTest extends AnyFunSpec with Matchers {
  private val position: Position = Position(1, 2)
  private val dimensions: Dimensions = Dimensions(10, 20)

  describe("A level builder state") {
    describe("when first created") {
      it("should be empty") {
        val state: LevelBuilderState = LevelBuilderState()
        state.dimensions shouldBe empty
        state.playableArea shouldBe empty
        state.moverCells shouldBe empty
        state.generatorCells shouldBe empty
        state.rotatorCells shouldBe empty
        state.blockCells shouldBe empty
        state.enemyCells shouldBe empty
        state.wallCells shouldBe empty
      }
    }

    describe("when dimensions are set") {
      it("should return the given dimensions") {
        val state: LevelBuilderState = LevelBuilderState(dimensions = Some(dimensions))
        state.dimensions should contain(dimensions)
      }
    }

    describe("when the playable area is set") {
      it("should return the given playable area") {
        val playableArea: PlayableArea = PlayableArea(dimensions)(position)
        val state: LevelBuilderState = LevelBuilderState(playableArea = Some(playableArea))
        state.playableArea should contain(playableArea)
      }
    }

    describe("when a mover cell is added") {
      it("should contain the added mover cell") {
        val moverCell: BaseMoverCell = BaseMoverCell(Orientation.Right)(position)
        val state: LevelBuilderState = LevelBuilderState(moverCells = Set(moverCell))
        state.moverCells should contain(moverCell)
      }
    }

    describe("when a generator cell is added") {
      it("should contain the added generator cell") {
        val generatorCell: BaseGeneratorCell = BaseGeneratorCell(Orientation.Right)(position)
        val state: LevelBuilderState = LevelBuilderState(generatorCells = Set(generatorCell))
        state.generatorCells should contain(generatorCell)
      }
    }

    describe("when a rotator cell is added") {
      it("should contain the added rotator cell") {
        val rotatorCell: BaseRotatorCell = BaseRotatorCell(Rotation.Clockwise)(position)
        val state: LevelBuilderState = LevelBuilderState(rotatorCells = Set(rotatorCell))
        state.rotatorCells should contain(rotatorCell)
      }
    }

    describe("when a block cell is added") {
      it("should contain the added block cell") {
        val blockCell: BaseBlockCell = BaseBlockCell(Push.Vertical)(position)
        val state: LevelBuilderState = LevelBuilderState(blockCells = Set(blockCell))
        state.blockCells should contain(blockCell)
      }
    }

    describe("when a enemy cell is added") {
      it("should contain the added enemy cell") {
        val enemyCell: BaseEnemyCell = BaseEnemyCell(position)
        val state: LevelBuilderState = LevelBuilderState(enemyCells = Set(enemyCell))
        state.enemyCells should contain(enemyCell)
      }
    }

    describe("when a wall cell is added") {
      it("should contain the added wall cell") {
        val wallCell: BaseWallCell = BaseWallCell(position)
        val state: LevelBuilderState = LevelBuilderState(wallCells = Set(wallCell))
        state.wallCells should contain(wallCell)
      }
    }

    describe("when created by copy constructor") {
      it("should have the same properties of the cloned level builder state") {
        val state: LevelBuilderState =
          LevelBuilderState(dimensions = Some(dimensions), playableArea = Some(PlayableArea(dimensions)(position)))
        state.copy() shouldBe state
      }
    }
  }
}
