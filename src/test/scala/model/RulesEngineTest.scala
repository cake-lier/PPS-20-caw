package it.unibo.pps.caw.model

import it.unibo.pps.caw.common.model.Board
import it.unibo.pps.caw.common.model.cell._
import it.unibo.pps.caw.common.model.cell.{BaseCell, Orientation, Push, Rotation}
import it.unibo.pps.caw.game.model.*
import it.unibo.pps.caw.game.model.engine.RulesEngine
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import scala.util.Using

/** Tests for [[RulesEngine]] */
class RulesEngineTest extends AnyFunSpec with Matchers {

  private val rulesEngine: RulesEngine = RulesEngine(Using(Source.fromResource("cellmachine.pl")) {
    _.getLines.mkString(" ")
  }.get)
  private val maxId: Long = 4
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

  describe("Game Engine") {
    describe("when mover right cell is used") {
      it("should  the game") {
        rulesEngine.update(moverRightBoard) shouldBe Board(
          BaseMoverCell(Orientation.Right)((1, 0)),
          BaseBlockCell(Push.Horizontal)((2, 0)),
          BaseBlockCell(Push.Both)((10, 0))
        )
      }
    }
    describe("when mover left cell is used") {
      it("should  the game") {
        rulesEngine.update(moverLeftBoard) shouldBe Board(
          BaseMoverCell(Orientation.Left)((9, 0)),
          BaseBlockCell(Push.Horizontal)((8, 0)),
          BaseBlockCell(Push.Both)((0, 0))
        )
      }
    }
    describe("when mover top cell is used") {
      it("should  the game") {
        rulesEngine.update(moverTopBoard) shouldBe Board(
          BaseMoverCell(Orientation.Top)((0, 1)),
          BaseBlockCell(Push.Vertical)((0, 0)),
          BaseBlockCell(Push.Both)((0, 10))
        )
      }
    }
    describe("when mover down cell is used") {
      it("should  the game") {
        rulesEngine.update(moverDownBoard) shouldBe Board(
          BaseMoverCell(Orientation.Down)((0, 10)),
          BaseBlockCell(Push.Vertical)((0, 11)),
          BaseBlockCell(Push.Both)((0, 0))
        )
      }
    }
    describe("when generator right cell is used") {
      it("should  the game") {
        rulesEngine.update(generatorRightBoard) shouldBe Board(
          BaseBlockCell(Push.Horizontal)((2, 0)),
          BaseBlockCell(Push.Horizontal)((0, 0)),
          BaseGeneratorCell(Orientation.Right)((1, 0)),
          BaseBlockCell(Push.Both)((10, 0))
        )
      }
    }
    describe("when generator left cell is used") {
      it("should  the game") {
        rulesEngine.update(generatorLeftBoard) shouldBe Board(
          BaseBlockCell(Push.Horizontal)((8, 0)),
          BaseBlockCell(Push.Horizontal)((10, 0)),
          BaseGeneratorCell(Orientation.Left)((9, 0)),
          BaseBlockCell(Push.Both)((0, 0))
        )
      }
    }
    describe("when generator top cell is used") {
      it("should  the game") {
        rulesEngine.update(generatorTopBoard) shouldBe Board(
          BaseBlockCell(Push.Vertical)((0, 1)),
          BaseBlockCell(Push.Vertical)((0, 3)),
          BaseGeneratorCell(Orientation.Top)((0, 2)),
          BaseBlockCell(Push.Both)((0, 10))
        )
      }
    }
    describe("when generator down cell is used") {
      it("should  the game") {
        rulesEngine.update(generatorDownBoard) shouldBe Board(
          BaseBlockCell(Push.Vertical)((0, 11)),
          BaseBlockCell(Push.Vertical)((0, 9)),
          BaseGeneratorCell(Orientation.Down)((0, 10)),
          BaseBlockCell(Push.Both)((0, 0))
        )
      }
    }
    describe("when rotator left cell is used") {
      it("should  the game") {
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
      it("should  the game") {
        rulesEngine.update(rotatorRightBoard) shouldBe Board(
          BaseBlockCell(Push.Vertical)((0, 1)),
          BaseBlockCell(Push.Vertical)((1, 0)),
          BaseBlockCell(Push.Vertical)((2, 1)),
          BaseBlockCell(Push.Vertical)((1, 2)),
          BaseRotatorCell(Rotation.Clockwise)((1, 1))
        )
      }
    }
  }
}
