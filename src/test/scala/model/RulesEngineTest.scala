package it.unibo.pps.caw.model

import it.unibo.pps.caw.common.model.Board
import it.unibo.pps.caw.common.model.cell.{Orientation, Push, Rotation}
import it.unibo.pps.caw.game.model.*
import it.unibo.pps.caw.game.model.engine.RulesEngine
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for [[RulesEngine]] */
class RulesEngineTest extends AnyFunSpec with Matchers {

  private val maxId: Long = 4
  private val moverRightBoard: Board[UpdateCell] = Board(
    UpdateMoverCell((0, 0), Orientation.Right, 1, false),
    UpdateBlockCell((1, 0), Push.Horizontal, 2, false),
    UpdateBlockCell((10, 0), Push.Both, 3, false)
  )
  private val moverLeftBoard: Board[UpdateCell] = Board(
    UpdateMoverCell((10, 0), Orientation.Left, 1, false),
    UpdateBlockCell((9, 0), Push.Horizontal, 2, false),
    UpdateBlockCell((0, 0), Push.Both, 3, false)
  )
  private val moverTopBoard: Board[UpdateCell] = Board(
    UpdateMoverCell((0, 2), Orientation.Top, 1, false),
    UpdateBlockCell((0, 1), Push.Vertical, 2, false),
    UpdateBlockCell((0, 10), Push.Both, 3, false)
  )
  private val moverDownBoard: Board[UpdateCell] = Board(
    UpdateMoverCell((0, 9), Orientation.Down, 1, false),
    UpdateBlockCell((0, 10), Push.Vertical, 2, false),
    UpdateBlockCell((0, 0), Push.Both, 3, false)
  )
  private val rotatorLeftBoard: Board[UpdateCell] = Board(
    UpdateRotatorCell((1, 1), Rotation.Counterclockwise, 1, false),
    UpdateBlockCell((1, 0), Push.Horizontal, 2, false),
    UpdateBlockCell((1, 2), Push.Horizontal, 3, false),
    UpdateBlockCell((2, 1), Push.Horizontal, 4, false),
    UpdateBlockCell((0, 1), Push.Horizontal, 5, false)
  )
  private val rotatorRightBoard: Board[UpdateCell] = Board(
    UpdateRotatorCell((1, 1), Rotation.Clockwise, 1, false),
    UpdateBlockCell((1, 0), Push.Horizontal, 2, false),
    UpdateBlockCell((1, 2), Push.Horizontal, 3, false),
    UpdateBlockCell((2, 1), Push.Horizontal, 4, false),
    UpdateBlockCell((0, 1), Push.Horizontal, 5, false)
  )
  private val generatorRightBoard: Board[UpdateCell] = Board(
    UpdateBlockCell((0, 0), Push.Horizontal, 1, false),
    UpdateGeneratorCell((1, 0), Orientation.Right, 2, false),
    UpdateBlockCell((10, 0), Push.Both, 3, false)
  )
  private val generatorLeftBoard: Board[UpdateCell] = Board(
    UpdateBlockCell((10, 0), Push.Horizontal, 1, false),
    UpdateGeneratorCell((9, 0), Orientation.Left, 2, false),
    UpdateBlockCell((0, 0), Push.Both, 3, false)
  )
  private val generatorTopBoard: Board[UpdateCell] = Board(
    UpdateBlockCell((0, 3), Push.Vertical, 1, false),
    UpdateGeneratorCell((0, 2), Orientation.Top, 2, false),
    UpdateBlockCell((0, 10), Push.Both, 3, false)
  )
  private val generatorDownBoard: Board[UpdateCell] = Board(
    UpdateBlockCell((0, 9), Push.Vertical, 1, false),
    UpdateGeneratorCell((0, 10), Orientation.Down, 2, false),
    UpdateBlockCell((0, 0), Push.Both, 3, false)
  )

  describe("Game Engine") {
    it("should be created correctly") {
      createEngine
    }
    describe("when mover right cell is used") {
      it("should update the game") {
        createEngine.nextState(moverRightBoard, UpdateMoverCell((0, 0), Orientation.Right, 1, false)) shouldBe Board(
          UpdateMoverCell((1, 0), Orientation.Right, 1, true),
          UpdateBlockCell((2, 0), Push.Horizontal, 2, false),
          UpdateBlockCell((10, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when mover left cell is used") {
      it("should update the game") {
        createEngine.nextState(moverLeftBoard, UpdateMoverCell((10, 0), Orientation.Left, 1, false)) shouldBe Board(
          UpdateMoverCell((9, 0), Orientation.Left, 1, true),
          UpdateBlockCell((8, 0), Push.Horizontal, 2, false),
          UpdateBlockCell((0, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when mover top cell is used") {
      it("should update the game") {
        createEngine.nextState(moverTopBoard, UpdateMoverCell((0, 2), Orientation.Top, 1, false)) shouldBe Board(
          UpdateMoverCell((0, 1), Orientation.Top, 1, true),
          UpdateBlockCell((0, 0), Push.Vertical, 2, false),
          UpdateBlockCell((0, 10), Push.Both, 3, false)
        )
      }
    }
    describe("when mover down cell is used") {
      it("should update the game") {
        createEngine.nextState(moverDownBoard, UpdateMoverCell((0, 9), Orientation.Down, 1, false)) shouldBe Board(
          UpdateMoverCell((0, 10), Orientation.Down, 1, true),
          UpdateBlockCell((0, 11), Push.Vertical, 2, false),
          UpdateBlockCell((0, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when generator right cell is used") {
      it("should update the game") {
        createEngine.nextState(
          generatorRightBoard,
          UpdateGeneratorCell((1, 0), Orientation.Right, 2, false)
        ) shouldBe Board(
          UpdateBlockCell((2, 0), Push.Horizontal, 4, true),
          UpdateBlockCell((0, 0), Push.Horizontal, 1, false),
          UpdateGeneratorCell((1, 0), Orientation.Right, 2, true),
          UpdateBlockCell((10, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when generator left cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorLeftBoard, UpdateGeneratorCell((9, 0), Orientation.Left, 2, false)) shouldBe Board(
          UpdateBlockCell((8, 0), Push.Horizontal, 4, true),
          UpdateBlockCell((10, 0), Push.Horizontal, 1, false),
          UpdateGeneratorCell((9, 0), Orientation.Left, 2, true),
          UpdateBlockCell((0, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when generator top cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorTopBoard, UpdateGeneratorCell((0, 2), Orientation.Top, 2, false)) shouldBe Board(
          UpdateBlockCell((0, 1), Push.Vertical, 4, true),
          UpdateBlockCell((0, 3), Push.Vertical, 1, false),
          UpdateGeneratorCell((0, 2), Orientation.Top, 2, true),
          UpdateBlockCell((0, 10), Push.Both, 3, false)
        )
      }
    }
    describe("when generator down cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorDownBoard, UpdateGeneratorCell((0, 10), Orientation.Down, 2, false)) shouldBe Board(
          UpdateBlockCell((0, 11), Push.Vertical, 4, true),
          UpdateBlockCell((0, 9), Push.Vertical, 1, false),
          UpdateGeneratorCell((0, 10), Orientation.Down, 2, true),
          UpdateBlockCell((0, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when rotator left cell is used") {
      it("should update the game") {
        createEngine.nextState(
          rotatorLeftBoard,
          UpdateRotatorCell((1, 1), Rotation.Counterclockwise, 1, false)
        ) shouldBe Board(
          UpdateBlockCell((1, 0), Push.Vertical, 2, false),
          UpdateBlockCell((0, 1), Push.Vertical, 5, false),
          UpdateRotatorCell((1, 1), Rotation.Counterclockwise, 1, true),
          UpdateBlockCell((2, 1), Push.Vertical, 4, false),
          UpdateBlockCell((1, 2), Push.Vertical, 3, false)
        )
      }
    }
    describe("when rotator right cell is used") {
      it("should update the game") {
        createEngine.nextState(rotatorRightBoard, UpdateRotatorCell((1, 1), Rotation.Clockwise, 1, false)) shouldBe Board(
          UpdateBlockCell((0, 1), Push.Vertical, 5, false),
          UpdateBlockCell((1, 0), Push.Vertical, 2, false),
          UpdateBlockCell((2, 1), Push.Vertical, 4, false),
          UpdateBlockCell((1, 2), Push.Vertical, 3, false),
          UpdateRotatorCell((1, 1), Rotation.Clockwise, 1, true)
        )
      }
    }
  }

  private def createEngine: RulesEngine = RulesEngine()
}
