package it.unibo.pps.caw
package dsl.entities

import common.model.*
import common.model.cell.*

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[LevelBuilderState]] trait. */
class LevelBuilderStateTest extends AnyFunSpec with Matchers {
  private val position: Position = Position(1, 2)
  private val dimensions: Dimensions = Dimensions(10, 20)

  describe("A level builder state") {
    describe("when first created") {
      it("should be empty") {
        val state: LevelBuilderState = LevelBuilderState()
        state.dimensions shouldBe empty
        state.playableArea shouldBe empty
        state.cells shouldBe empty
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
        val state: LevelBuilderState = LevelBuilderState(cells = Seq(moverCell))
        state.cells should contain(moverCell)
      }
    }

    describe("when a generator cell is added") {
      it("should contain the added generator cell") {
        val generatorCell: BaseGeneratorCell = BaseGeneratorCell(Orientation.Right)(position)
        val state: LevelBuilderState = LevelBuilderState(cells = Seq(generatorCell))
        state.cells should contain(generatorCell)
      }
    }

    describe("when a rotator cell is added") {
      it("should contain the added rotator cell") {
        val rotatorCell: BaseRotatorCell = BaseRotatorCell(Rotation.Clockwise)(position)
        val state: LevelBuilderState = LevelBuilderState(cells = Seq(rotatorCell))
        state.cells should contain(rotatorCell)
      }
    }

    describe("when a block cell is added") {
      it("should contain the added block cell") {
        val blockCell: BaseBlockCell = BaseBlockCell(Push.Vertical)(position)
        val state: LevelBuilderState = LevelBuilderState(cells = Seq(blockCell))
        state.cells should contain(blockCell)
      }
    }

    describe("when a enemy cell is added") {
      it("should contain the added enemy cell") {
        val enemyCell: BaseEnemyCell = BaseEnemyCell(position)
        val state: LevelBuilderState = LevelBuilderState(cells = Seq(enemyCell))
        state.cells should contain(enemyCell)
      }
    }

    describe("when a wall cell is added") {
      it("should contain the added wall cell") {
        val wallCell: BaseWallCell = BaseWallCell(position)
        val state: LevelBuilderState = LevelBuilderState(cells = Seq(wallCell))
        state.cells should contain(wallCell)
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
