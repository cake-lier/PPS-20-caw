package it.unibo.pps.caw
package game.model.engine

import common.model.Board
import common.model.cell.*
import common.storage.FileStorage

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import scala.util.Using

/** Tests for [[RulesEngine]] */
class RulesEngineTest extends AnyFunSpec with Matchers {
  private val fileStorage: FileStorage = FileStorage()
  private val rulesEngine: RulesEngine = RulesEngine(fileStorage.loadResource("cellmachine.pl").get)

  private val moverRightBoard: Board[BaseCell] = Board(
    BaseMoverCell(Orientation.Right)((0, 0)),
    BaseBlockCell(Push.Horizontal)((1, 0)),
    BaseBlockCell(Push.Both)((10, 0))
  )
  private val moverLeftBoard: Board[BaseCell] = Board(
    BaseMoverCell(Orientation.Left)((10, 0)),
    BaseBlockCell(Push.Horizontal)((9, 0)),
    BaseBlockCell(Push.Both)((0, 0))
  )
  private val moverTopBoard: Board[BaseCell] = Board(
    BaseMoverCell(Orientation.Top)((0, 2)),
    BaseBlockCell(Push.Vertical)((0, 1)),
    BaseBlockCell(Push.Both)((0, 10))
  )
  private val moverDownBoard: Board[BaseCell] = Board(
    BaseMoverCell(Orientation.Down)((0, 9)),
    BaseBlockCell(Push.Vertical)((0, 10)),
    BaseBlockCell(Push.Both)((0, 0))
  )
  private val rotatorLeftBoard: Board[BaseCell] = Board(
    BaseRotatorCell(Rotation.Counterclockwise)((1, 1)),
    BaseBlockCell(Push.Horizontal)((1, 0)),
    BaseBlockCell(Push.Horizontal)((1, 2)),
    BaseBlockCell(Push.Horizontal)((2, 1)),
    BaseBlockCell(Push.Horizontal)((0, 1))
  )
  private val rotatorRightBoard: Board[BaseCell] = Board(
    BaseRotatorCell(Rotation.Clockwise)((1, 1)),
    BaseBlockCell(Push.Horizontal)((1, 0)),
    BaseBlockCell(Push.Horizontal)((1, 2)),
    BaseBlockCell(Push.Horizontal)((2, 1)),
    BaseBlockCell(Push.Horizontal)((0, 1))
  )
  private val generatorRightBoard: Board[BaseCell] = Board(
    BaseBlockCell(Push.Horizontal)((0, 0)),
    BaseGeneratorCell(Orientation.Right)((1, 0)),
    BaseBlockCell(Push.Both)((10, 0))
  )
  private val generatorLeftBoard: Board[BaseCell] = Board(
    BaseBlockCell(Push.Horizontal)((10, 0)),
    BaseGeneratorCell(Orientation.Left)((9, 0)),
    BaseBlockCell(Push.Both)((0, 0))
  )
  private val generatorTopBoard: Board[BaseCell] = Board(
    BaseBlockCell(Push.Vertical)((0, 3)),
    BaseGeneratorCell(Orientation.Top)((0, 2)),
    BaseBlockCell(Push.Both)((0, 10))
  )
  private val generatorDownBoard: Board[BaseCell] = Board(
    BaseBlockCell(Push.Vertical)((0, 9)),
    BaseGeneratorCell(Orientation.Down)((0, 10)),
    BaseBlockCell(Push.Both)((0, 0))
  )
  private val deleterCellBoardMover: Board[BaseCell] = Board(
    BaseMoverCell(Orientation.Right)((0, 0)),
    BaseDeleterCell((1, 0)),
    BaseBlockCell(Push.Both)((10, 0))
  )
  private val deleterCellBoardGenerator: Board[BaseCell] = Board(
    BaseGeneratorCell(Orientation.Right)((0, 0)),
    BaseGeneratorCell(Orientation.Right)((1, 0)),
    BaseDeleterCell((2, 0)),
    BaseBlockCell(Push.Both)((10, 0))
  )
  private val deleterCellBoardBlock: Board[BaseCell] = Board(
    BaseBlockCell(Push.Both)((1, 0)),
    BaseGeneratorCell(Orientation.Right)((0, 0)),
    BaseDeleterCell((2, 0)),
    BaseBlockCell(Push.Both)((10, 0))
  )
  private val deleterCellBoardWall: Board[BaseCell] = Board(
    BaseWallCell((0, 0)),
    BaseGeneratorCell(Orientation.Right)((1, 0)),
    BaseDeleterCell((2, 0)),
    BaseBlockCell(Push.Both)((10, 0))
  )
  private val deleterCellBoardRotator: Board[BaseCell] = Board(
    BaseRotatorCell(Rotation.Clockwise)((0, 0)),
    BaseGeneratorCell(Orientation.Right)((1, 0)),
    BaseDeleterCell((2, 0)),
    BaseBlockCell(Push.Both)((10, 0))
  )
  private val deleterCellBoardDeleter: Board[BaseCell] = Board(
    BaseDeleterCell((0, 0)),
    BaseGeneratorCell(Orientation.Right)((1, 0)),
    BaseDeleterCell((2, 0)),
    BaseBlockCell(Push.Both)((10, 0))
  )

  describe("Game Engine") {
    describe("when mover right cell is used") {
      it("should update the game") {
        rulesEngine.update(moverRightBoard) shouldBe Board(
          BaseMoverCell(Orientation.Right)((1, 0)),
          BaseBlockCell(Push.Horizontal)((2, 0)),
          BaseBlockCell(Push.Both)((10, 0))
        )
      }
    }
    describe("when mover left cell is used") {
      it("should update the game") {
        rulesEngine.update(moverLeftBoard) shouldBe Board(
          BaseMoverCell(Orientation.Left)((9, 0)),
          BaseBlockCell(Push.Horizontal)((8, 0)),
          BaseBlockCell(Push.Both)((0, 0))
        )
      }
    }
    describe("when mover top cell is used") {
      it("should update the game") {
        rulesEngine.update(moverTopBoard) shouldBe Board(
          BaseMoverCell(Orientation.Top)((0, 1)),
          BaseBlockCell(Push.Vertical)((0, 0)),
          BaseBlockCell(Push.Both)((0, 10))
        )
      }
    }
    describe("when mover down cell is used") {
      it("should update the game") {
        rulesEngine.update(moverDownBoard) shouldBe Board(
          BaseMoverCell(Orientation.Down)((0, 10)),
          BaseBlockCell(Push.Vertical)((0, 11)),
          BaseBlockCell(Push.Both)((0, 0))
        )
      }
    }
    describe("when generator right cell is used") {
      it("should update the game") {
        rulesEngine.update(generatorRightBoard) shouldBe Board(
          BaseBlockCell(Push.Horizontal)((2, 0)),
          BaseBlockCell(Push.Horizontal)((0, 0)),
          BaseGeneratorCell(Orientation.Right)((1, 0)),
          BaseBlockCell(Push.Both)((10, 0))
        )
      }
    }
    describe("when generator left cell is used") {
      it("should update the game") {
        rulesEngine.update(generatorLeftBoard) shouldBe Board(
          BaseBlockCell(Push.Horizontal)((8, 0)),
          BaseBlockCell(Push.Horizontal)((10, 0)),
          BaseGeneratorCell(Orientation.Left)((9, 0)),
          BaseBlockCell(Push.Both)((0, 0))
        )
      }
    }
    describe("when generator top cell is used") {
      it("should update the game") {
        rulesEngine.update(generatorTopBoard) shouldBe Board(
          BaseBlockCell(Push.Vertical)((0, 1)),
          BaseBlockCell(Push.Vertical)((0, 3)),
          BaseGeneratorCell(Orientation.Top)((0, 2)),
          BaseBlockCell(Push.Both)((0, 10))
        )
      }
    }
    describe("when generator down cell is used") {
      it("should update the game") {
        rulesEngine.update(generatorDownBoard) shouldBe Board(
          BaseBlockCell(Push.Vertical)((0, 11)),
          BaseBlockCell(Push.Vertical)((0, 9)),
          BaseGeneratorCell(Orientation.Down)((0, 10)),
          BaseBlockCell(Push.Both)((0, 0))
        )
      }
    }
    describe("when rotator left cell is used") {
      it("should update the game") {
        rulesEngine.update(rotatorLeftBoard) shouldBe Board(
          BaseBlockCell(Push.Vertical)((1, 0)),
          BaseBlockCell(Push.Vertical)((0, 1)),
          BaseRotatorCell(Rotation.Counterclockwise)((1, 1)),
          BaseBlockCell(Push.Vertical)((2, 1)),
          BaseBlockCell(Push.Vertical)((1, 2))
        )
      }
    }
    describe("when rotator right cell is used") {
      it("should update the game") {
        rulesEngine.update(rotatorRightBoard) shouldBe Board(
          BaseBlockCell(Push.Vertical)((0, 1)),
          BaseBlockCell(Push.Vertical)((1, 0)),
          BaseBlockCell(Push.Vertical)((2, 1)),
          BaseBlockCell(Push.Vertical)((1, 2)),
          BaseRotatorCell(Rotation.Clockwise)((1, 1))
        )
      }
    }
    describe("when DeleterCell is used") {
      it("should delete RotatorCell") {
        rulesEngine.update(deleterCellBoardRotator) shouldBe deleterCellBoardRotator
      }
      it("should delete GeneratorCell") {
        rulesEngine.update(deleterCellBoardGenerator) shouldBe deleterCellBoardGenerator
      }
      it("should delete DeleterCell") {
        rulesEngine.update(deleterCellBoardDeleter) shouldBe deleterCellBoardDeleter
      }
      it("should delete BlockCell") {
        rulesEngine.update(deleterCellBoardBlock) shouldBe deleterCellBoardBlock
      }
      it("should delete MoverCell") {
        rulesEngine.update(deleterCellBoardMover) shouldBe Board(
          BaseDeleterCell((1, 0)),
          BaseBlockCell(Push.Both)((10, 0))
        )
      }
      it("should delete WallCell") {
        rulesEngine.update(deleterCellBoardWall) shouldBe deleterCellBoardWall
      }
    }
  }
}
