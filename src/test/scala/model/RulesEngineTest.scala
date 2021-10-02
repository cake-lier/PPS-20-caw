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
    BaseMoverCell((0, 0), Orientation.Right),
    BaseBlockCell((1, 0), Push.Horizontal),
    BaseBlockCell((10, 0), Push.Both)
  )
  private val moverLeftBoard: Board[BaseCell] = Board(
    BaseMoverCell((10, 0), Orientation.Left),
    BaseBlockCell((9, 0), Push.Horizontal),
    BaseBlockCell((0, 0), Push.Both)
  )
  private val moverTopBoard: Board[BaseCell] = Board(
    BaseMoverCell((0, 2), Orientation.Top),
    BaseBlockCell((0, 1), Push.Vertical),
    BaseBlockCell((0, 10), Push.Both)
  )
  private val moverDownBoard: Board[BaseCell] = Board(
    BaseMoverCell((0, 9), Orientation.Down),
    BaseBlockCell((0, 10), Push.Vertical),
    BaseBlockCell((0, 0), Push.Both)
  )
  private val rotatorLeftBoard: Board[BaseCell] = Board(
    BaseRotatorCell((1, 1), Rotation.Counterclockwise),
    BaseBlockCell((1, 0), Push.Horizontal),
    BaseBlockCell((1, 2), Push.Horizontal),
    BaseBlockCell((2, 1), Push.Horizontal),
    BaseBlockCell((0, 1), Push.Horizontal)
  )
  private val rotatorRightBoard: Board[BaseCell] = Board(
    BaseRotatorCell((1, 1), Rotation.Clockwise),
    BaseBlockCell((1, 0), Push.Horizontal),
    BaseBlockCell((1, 2), Push.Horizontal),
    BaseBlockCell((2, 1), Push.Horizontal),
    BaseBlockCell((0, 1), Push.Horizontal)
  )
  private val generatorRightBoard: Board[BaseCell] = Board(
    BaseBlockCell((0, 0), Push.Horizontal),
    BaseGeneratorCell((1, 0), Orientation.Right),
    BaseBlockCell((10, 0), Push.Both)
  )
  private val generatorLeftBoard: Board[BaseCell] = Board(
    BaseBlockCell((10, 0), Push.Horizontal),
    BaseGeneratorCell((9, 0), Orientation.Left),
    BaseBlockCell((0, 0), Push.Both)
  )
  private val generatorTopBoard: Board[BaseCell] = Board(
    BaseBlockCell((0, 3), Push.Vertical),
    BaseGeneratorCell((0, 2), Orientation.Top),
    BaseBlockCell((0, 10), Push.Both)
  )
  private val generatorDownBoard: Board[BaseCell] = Board(
    BaseBlockCell((0, 9), Push.Vertical),
    BaseGeneratorCell((0, 10), Orientation.Down),
    BaseBlockCell((0, 0), Push.Both)
  )

  describe("Game Engine") {
    describe("when mover right cell is used") {
      it("should  the game") {
        rulesEngine.update(moverRightBoard) shouldBe Board(
          BaseMoverCell((1, 0), Orientation.Right),
          BaseBlockCell((2, 0), Push.Horizontal),
          BaseBlockCell((10, 0), Push.Both)
        )
      }
    }
    describe("when mover left cell is used") {
      it("should  the game") {
        rulesEngine.update(moverLeftBoard) shouldBe Board(
          BaseMoverCell((9, 0), Orientation.Left),
          BaseBlockCell((8, 0), Push.Horizontal),
          BaseBlockCell((0, 0), Push.Both)
        )
      }
    }
    describe("when mover top cell is used") {
      it("should  the game") {
        rulesEngine.update(moverTopBoard) shouldBe Board(
          BaseMoverCell((0, 1), Orientation.Top),
          BaseBlockCell((0, 0), Push.Vertical),
          BaseBlockCell((0, 10), Push.Both)
        )
      }
    }
    describe("when mover down cell is used") {
      it("should  the game") {
        rulesEngine.update(moverDownBoard) shouldBe Board(
          BaseMoverCell((0, 10), Orientation.Down),
          BaseBlockCell((0, 11), Push.Vertical),
          BaseBlockCell((0, 0), Push.Both)
        )
      }
    }
    describe("when generator right cell is used") {
      it("should  the game") {
        rulesEngine.update(generatorRightBoard) shouldBe Board(
          BaseBlockCell((2, 0), Push.Horizontal),
          BaseBlockCell((0, 0), Push.Horizontal),
          BaseGeneratorCell((1, 0), Orientation.Right),
          BaseBlockCell((10, 0), Push.Both)
        )
      }
    }
    describe("when generator left cell is used") {
      it("should  the game") {
        rulesEngine.update(generatorLeftBoard) shouldBe Board(
          BaseBlockCell((8, 0), Push.Horizontal),
          BaseBlockCell((10, 0), Push.Horizontal),
          BaseGeneratorCell((9, 0), Orientation.Left),
          BaseBlockCell((0, 0), Push.Both)
        )
      }
    }
    describe("when generator top cell is used") {
      it("should  the game") {
        rulesEngine.update(generatorTopBoard) shouldBe Board(
          BaseBlockCell((0, 1), Push.Vertical),
          BaseBlockCell((0, 3), Push.Vertical),
          BaseGeneratorCell((0, 2), Orientation.Top),
          BaseBlockCell((0, 10), Push.Both)
        )
      }
    }
    describe("when generator down cell is used") {
      it("should  the game") {
        rulesEngine.update(generatorDownBoard) shouldBe Board(
          BaseBlockCell((0, 11), Push.Vertical),
          BaseBlockCell((0, 9), Push.Vertical),
          BaseGeneratorCell((0, 10), Orientation.Down),
          BaseBlockCell((0, 0), Push.Both)
        )
      }
    }
    describe("when rotator left cell is used") {
      it("should  the game") {
        rulesEngine.update(rotatorLeftBoard) shouldBe Board(
          BaseBlockCell((1, 0), Push.Vertical),
          BaseBlockCell((0, 1), Push.Vertical),
          BaseRotatorCell((1, 1), Rotation.Counterclockwise),
          BaseBlockCell((2, 1), Push.Vertical),
          BaseBlockCell((1, 2), Push.Vertical)
        )
      }
    }
    describe("when rotator right cell is used") {
      it("should  the game") {
        rulesEngine.update(rotatorRightBoard) shouldBe Board(
          BaseBlockCell((0, 1), Push.Vertical),
          BaseBlockCell((1, 0), Push.Vertical),
          BaseBlockCell((2, 1), Push.Vertical),
          BaseBlockCell((1, 2), Push.Vertical),
          BaseRotatorCell((1, 1), Rotation.Clockwise)
        )
      }
    }
  }
}
