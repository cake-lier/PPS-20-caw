package it.unibo.pps.caw

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import engine.GameEngine
import model.{AllowedMovement, BlockCell, Board, GeneratorCell, MoverCell, Orientation, RotationDirection, RotatorCell}
import scala.language.implicitConversions

/** Tests for [[GameEngine]] */
class GameEngineTest extends AnyFunSpec with Matchers {
  private val moverRightBoard: Board = Board(
    MoverCell((0, 0), Orientation.Right),
    BlockCell((1, 0), AllowedMovement.Horizontal),
    BlockCell((10, 0), AllowedMovement.Both)
  )
  private val moverLeftBoard: Board = Board(
    MoverCell((10, 0), Orientation.Left),
    BlockCell((9, 0), AllowedMovement.Horizontal),
    BlockCell((0, 0), AllowedMovement.Both)
  )
  private val moverTopBoard: Board = Board(
    MoverCell((0, 0), Orientation.Top),
    BlockCell((0, 1), AllowedMovement.Vertical),
    BlockCell((0, 10), AllowedMovement.Both)
  )
  private val moverDownBoard: Board = Board(
    MoverCell((0, 10), Orientation.Down),
    BlockCell((0, 9), AllowedMovement.Vertical),
    BlockCell((0, 0), AllowedMovement.Both)
  )
  private val rotatorLeftBoard: Board = Board(
    RotatorCell((1, 1), RotationDirection.Left),
    BlockCell((1, 0), AllowedMovement.Horizontal),
    BlockCell((1, 2), AllowedMovement.Horizontal),
    BlockCell((2, 1), AllowedMovement.Horizontal),
    BlockCell((0, 1), AllowedMovement.Horizontal)
  )
  private val rotatorRightBoard: Board = Board(
    RotatorCell((1, 1), RotationDirection.Right),
    BlockCell((1, 0), AllowedMovement.Horizontal),
    BlockCell((1, 2), AllowedMovement.Horizontal),
    BlockCell((2, 1), AllowedMovement.Horizontal),
    BlockCell((0, 1), AllowedMovement.Horizontal)
  )
  private val generatorRightBoard: Board = Board(
    BlockCell((0, 0), AllowedMovement.Horizontal),
    GeneratorCell((1, 0), Orientation.Right),
    BlockCell((10, 0), AllowedMovement.Both)
  )
  private val generatorLeftBoard: Board = Board(
    BlockCell((10, 0), AllowedMovement.Horizontal),
    GeneratorCell((9, 0), Orientation.Left),
    BlockCell((0, 0), AllowedMovement.Both)
  )
  private val generatorTopBoard: Board = Board(
    BlockCell((0, 0), AllowedMovement.Vertical),
    GeneratorCell((0, 1), Orientation.Top),
    BlockCell((0, 10), AllowedMovement.Both)
  )
  private val generatorDownBoard: Board = Board(
    BlockCell((0, 10), AllowedMovement.Vertical),
    GeneratorCell((0, 9), Orientation.Down),
    BlockCell((0, 0), AllowedMovement.Both)
  )

  describe("Game Engine") {
    it("should be created correctly") {
      createEngine
    }
    describe("when mover right cell is used") {
      it("should update the game") {
        createEngine.nextState(moverRightBoard, MoverCell((0, 0), Orientation.Right)) shouldBe Board(
          MoverCell((1, 0), Orientation.Right),
          BlockCell((2, 0), AllowedMovement.Horizontal),
          BlockCell((10, 0), AllowedMovement.Both)
        )
      }
    }
    describe("when mover left cell is used") {
      it("should update the game") {
        createEngine.nextState(moverLeftBoard, MoverCell((10, 0), Orientation.Left)) shouldBe Board(
          MoverCell((9, 0), Orientation.Left),
          BlockCell((8, 0), AllowedMovement.Horizontal),
          BlockCell((0, 0), AllowedMovement.Both)
        )
      }
    }
    describe("when mover top cell is used") {
      it("should update the game") {
        createEngine.nextState(moverTopBoard, MoverCell((0, 0), Orientation.Top)) shouldBe Board(
          MoverCell((0, 1), Orientation.Top),
          BlockCell((0, 2), AllowedMovement.Vertical),
          BlockCell((0, 10), AllowedMovement.Both)
        )
      }
    }
    describe("when mover down cell is used") {
      it("should update the game") {
        createEngine.nextState(moverDownBoard, MoverCell((0, 10), Orientation.Down)) shouldBe Board(
          MoverCell((0, 9), Orientation.Down),
          BlockCell((0, 8), AllowedMovement.Vertical),
          BlockCell((0, 0), AllowedMovement.Both)
        )
      }
    }
    describe("when generator right cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorRightBoard, GeneratorCell((1, 0), Orientation.Right)) shouldBe Board(
          BlockCell((0, 0), AllowedMovement.Horizontal),
          GeneratorCell((1, 0), Orientation.Right),
          BlockCell((2, 0), AllowedMovement.Horizontal),
          BlockCell((10, 0), AllowedMovement.Both)
        )
      }
    }
    describe("when generator left cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorLeftBoard, GeneratorCell((9, 0), Orientation.Left)) shouldBe Board(
          BlockCell((10, 0), AllowedMovement.Horizontal),
          GeneratorCell((9, 0), Orientation.Left),
          BlockCell((8, 0), AllowedMovement.Horizontal),
          BlockCell((0, 0), AllowedMovement.Both)
        )
      }
    }
    describe("when generator top cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorTopBoard, GeneratorCell((0, 1), Orientation.Top)) shouldBe Board(
          BlockCell((0, 0), AllowedMovement.Vertical),
          GeneratorCell((0, 1), Orientation.Top),
          BlockCell((0, 2), AllowedMovement.Vertical),
          BlockCell((0, 10), AllowedMovement.Both)
        )
      }
    }
    describe("when generator down cell is used") {
      it("should update the game") {
        createEngine.nextState(generatorDownBoard, GeneratorCell((0, 9), Orientation.Down)) shouldBe Board(
          BlockCell((0, 10), AllowedMovement.Vertical),
          GeneratorCell((0, 9), Orientation.Down),
          BlockCell((0, 8), AllowedMovement.Vertical),
          BlockCell((0, 0), AllowedMovement.Both)
        )
      }
    }
    describe("when rotator left cell is used") {
      it("should update the game") {
        createEngine.nextState(rotatorLeftBoard, RotatorCell((1, 1), RotationDirection.Left)) shouldBe Board(
          RotatorCell((1, 1), RotationDirection.Left),
          BlockCell((1, 0), AllowedMovement.Vertical),
          BlockCell((1, 2), AllowedMovement.Vertical),
          BlockCell((2, 1), AllowedMovement.Vertical),
          BlockCell((0, 1), AllowedMovement.Vertical)
        )
      }
    }
    describe("when rotator right cell is used") {
      it("should update the game") {
        createEngine.nextState(rotatorRightBoard, RotatorCell((1, 1), RotationDirection.Right)) shouldBe Board(
          RotatorCell((1, 1), RotationDirection.Right),
          BlockCell((1, 0), AllowedMovement.Vertical),
          BlockCell((1, 2), AllowedMovement.Vertical),
          BlockCell((2, 1), AllowedMovement.Vertical),
          BlockCell((0, 1), AllowedMovement.Vertical)
        )
      }
    }
  }

  private def createEngine: GameEngine = GameEngine()
}
