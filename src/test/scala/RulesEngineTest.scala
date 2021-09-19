package it.unibo.pps.caw

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import engine.RulesEngine

import it.unibo.pps.caw.game.model.Board
import it.unibo.pps.caw.game.model._

import scala.language.implicitConversions

/** Tests for [[RulesEngine]] */
class RulesEngineTest extends AnyFunSpec with Matchers {

  private val maxId: Long = 4
  private val moverRightBoard: Board[IdCell] = Board(
    IdMoverCell((0, 0), Orientation.Right, 1, false),
    IdBlockCell((1, 0), Push.Horizontal, 2, false),
    IdBlockCell((10, 0), Push.Both, 3, false)
  )
  private val moverLeftBoard: Board[IdCell] = Board(
    IdMoverCell((10, 0), Orientation.Left, 1, false),
    IdBlockCell((9, 0), Push.Horizontal, 2, false),
    IdBlockCell((0, 0), Push.Both, 3, false)
  )
  private val moverTopBoard: Board[IdCell] = Board(
    IdMoverCell((0, 2), Orientation.Top, 1, false),
    IdBlockCell((0, 1), Push.Vertical, 2, false),
    IdBlockCell((0, 10), Push.Both, 3, false)
  )
  private val moverDownBoard: Board[IdCell] = Board(
    IdMoverCell((0, 9), Orientation.Down, 1, false),
    IdBlockCell((0, 10), Push.Vertical, 2, false),
    IdBlockCell((0, 0), Push.Both, 3, false)
  )
  private val rotatorLeftBoard: Board[IdCell] = Board(
    IdRotatorCell((1, 1), Rotation.Counterclockwise, 1, false),
    IdBlockCell((1, 0), Push.Horizontal, 2, false),
    IdBlockCell((1, 2), Push.Horizontal, 3, false),
    IdBlockCell((2, 1), Push.Horizontal, 4, false),
    IdBlockCell((0, 1), Push.Horizontal, 5, false)
  )
  private val rotatorRightBoard: Board[IdCell] = Board(
    IdRotatorCell((1, 1), Rotation.Clockwise, 1, false),
    IdBlockCell((1, 0), Push.Horizontal, 2, false),
    IdBlockCell((1, 2), Push.Horizontal, 3, false),
    IdBlockCell((2, 1), Push.Horizontal, 4, false),
    IdBlockCell((0, 1), Push.Horizontal, 5, false)
  )
  private val generatorRightBoard: Board[IdCell] = Board(
    IdBlockCell((0, 0), Push.Horizontal, 1, false),
    IdGeneratorCell((1, 0), Orientation.Right, 2, false),
    IdBlockCell((10, 0), Push.Both, 3, false)
  )
  private val generatorLeftBoard: Board[IdCell] = Board(
    IdBlockCell((10, 0), Push.Horizontal, 1, false),
    IdGeneratorCell((9, 0), Orientation.Left, 2, false),
    IdBlockCell((0, 0), Push.Both, 3, false)
  )
  private val generatorTopBoard: Board[IdCell] = Board(
    IdBlockCell((0, 3), Push.Vertical, 1, false),
    IdGeneratorCell((0, 2), Orientation.Top, 2, false),
    IdBlockCell((0, 10), Push.Both, 3, false)
  )
  private val generatorDownBoard: Board[IdCell] = Board(
    IdBlockCell((0, 9), Push.Vertical, 1, false),
    IdGeneratorCell((0, 10), Orientation.Down, 2, false),
    IdBlockCell((0, 0), Push.Both, 3, false)
  )

  describe("Game Engine") {
    it("should be created correctly") {
      createEngine
    }
    describe("when mover right cell is used") {
      it("should update the game") {
        createEngine.nextState(moverRightBoard, IdMoverCell((0, 0), Orientation.Right, 1, false)) shouldBe Board(
          IdMoverCell((1, 0), Orientation.Right, 1, true),
          IdBlockCell((2, 0), Push.Horizontal, 2, false),
          IdBlockCell((10, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when mover left cell is used") {
      it("should update the game") {
        createEngine.nextState(moverLeftBoard, IdMoverCell((10, 0), Orientation.Left, 1, false)) shouldBe Board(
          IdMoverCell((9, 0), Orientation.Left, 1, true),
          IdBlockCell((8, 0), Push.Horizontal, 2, false),
          IdBlockCell((0, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when mover top cell is used") {
      it("should update the game") {
        createEngine.nextState(moverTopBoard, IdMoverCell((0, 2), Orientation.Top, 1, false)) shouldBe Board(
          IdMoverCell((0, 1), Orientation.Top, 1, true),
          IdBlockCell((0, 0), Push.Vertical, 2, false),
          IdBlockCell((0, 10), Push.Both, 3, false)
        )
      }
    }
    describe("when mover down cell is used") {
      it("should update the game") {
        createEngine.nextState(moverDownBoard, IdMoverCell((0, 9), Orientation.Down, 1, false)) shouldBe Board(
          IdMoverCell((0, 10), Orientation.Down, 1, true),
          IdBlockCell((0, 11), Push.Vertical, 2, false),
          IdBlockCell((0, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when generator right cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorRightBoard, IdGeneratorCell((1, 0), Orientation.Right, 2, false)) shouldBe Board(
          IdBlockCell((2, 0), Push.Horizontal, 4, false),
          IdBlockCell((0, 0), Push.Horizontal, 1, false),
          IdGeneratorCell((1, 0), Orientation.Right, 2, true),
          IdBlockCell((10, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when generator left cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorLeftBoard, IdGeneratorCell((9, 0), Orientation.Left, 2, false)) shouldBe Board(
          IdBlockCell((8, 0), Push.Horizontal, 4, false),
          IdBlockCell((10, 0), Push.Horizontal, 1, false),
          IdGeneratorCell((9, 0), Orientation.Left, 2, true),
          IdBlockCell((0, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when generator top cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorTopBoard, IdGeneratorCell((0, 2), Orientation.Top, 2, false)) shouldBe Board(
          IdBlockCell((0, 1), Push.Vertical, 4, false),
          IdBlockCell((0, 3), Push.Vertical, 1, false),
          IdGeneratorCell((0, 2), Orientation.Top, 2, true),
          IdBlockCell((0, 10), Push.Both, 3, false)
        )
      }
    }
    describe("when generator down cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorDownBoard, IdGeneratorCell((0, 10), Orientation.Down, 2, false)) shouldBe Board(
          IdBlockCell((0, 11), Push.Vertical, 4, false),
          IdBlockCell((0, 9), Push.Vertical, 1, false),
          IdGeneratorCell((0, 10), Orientation.Down, 2, true),
          IdBlockCell((0, 0), Push.Both, 3, false)
        )
      }
    }
    describe("when rotator left cell is used") {
      it("should update the game") {
        createEngine.nextState(rotatorLeftBoard, IdRotatorCell((1, 1), Rotation.Counterclockwise, 1, false)) shouldBe Board(
          IdBlockCell((1, 0), Push.Vertical, 2, false),
          IdBlockCell((0, 1), Push.Vertical, 5, false),
          IdRotatorCell((1, 1), Rotation.Counterclockwise, 1, true),
          IdBlockCell((2, 1), Push.Vertical, 4, false),
          IdBlockCell((1, 2), Push.Vertical, 3, false)
        )
      }
    }
    describe("when rotator right cell is used") {
      it("should update the game") {
        createEngine.nextState(rotatorRightBoard, IdRotatorCell((1, 1), Rotation.Clockwise, 1, false)) shouldBe Board(
          IdBlockCell((0, 1), Push.Vertical, 5, false),
          IdBlockCell((1, 0), Push.Vertical, 2, false),
          IdBlockCell((2, 1), Push.Vertical, 4, false),
          IdBlockCell((1, 2), Push.Vertical, 3, false),
          IdRotatorCell((1, 1), Rotation.Clockwise, 1, true)
        )
      }
    }
  }

  private def createEngine: RulesEngine = RulesEngine()
}
