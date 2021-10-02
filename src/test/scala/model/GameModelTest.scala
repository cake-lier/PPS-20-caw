package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, Level, PlayableArea}
import it.unibo.pps.caw.common.model.Board.toPlayableCells
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.game.model.engine.RulesEngine
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import scala.util.Using

/** Tests for class [[GameModel]] */
class GameModelTest extends AnyFunSpec with Matchers {
  private val dimensions: Dimensions = (4, 4)
  private val rulesEngine: RulesEngine = RulesEngine(Using(Source.fromResource("cellmachine.pl")) {
    _.getLines.mkString(" ")
  }.get)
  private def convertBoard(board: Board[BaseCell]): Board[PlayableCell] =
    board
      .map(_ match {
        case BaseRotatorCell(p, r)   => BaseRotatorCell((p.x + 1, p.y + 1), r)
        case BaseGeneratorCell(p, o) => BaseGeneratorCell((p.x + 1, p.y + 1), o)
        case BaseEnemyCell(p)        => BaseEnemyCell((p.x + 1, p.y + 1))
        case BaseMoverCell(p, o)     => BaseMoverCell((p.x + 1, p.y + 1), o)
        case BaseBlockCell(p, d)     => BaseBlockCell((p.x + 1, p.y + 1), d)
        case BaseWallCell(p)         => BaseWallCell((p.x + 1, p.y + 1))
      })
      .toPlayableCells(_ => false) ++
      (0 to dimensions.width + 1).map(i => PlayableWallCell((i, 0), false)) ++
      (0 to dimensions.width + 1).map(i => PlayableWallCell((i, dimensions.height + 1), false)) ++
      (1 to dimensions.height).map(i => PlayableWallCell((0, i), false)) ++
      (1 to dimensions.height).map(i => PlayableWallCell((dimensions.width + 1, i), false))
  private val gameModel: GameModel = GameModel(
    rulesEngine,
    Level(
      dimensions,
      Board(
        BaseMoverCell((1, 1), Orientation.Right),
        BaseBlockCell((3, 3), Push.Both),
        BaseEnemyCell((4, 4))
      ),
      PlayableArea((0, 0), dimensions)
    )
  )
  describe("The game model") {
    describe("when first created") {
      it("should have the current state level equals to initial state level") {
        gameModel.state.levelCurrentState shouldBe Level(
          gameModel.state.levelCurrentState.dimensions,
          Board(
            gameModel.state.levelInitialState.board.cells
              .map(_ match {
                case PlayableRotatorCell(p, r, _)   => PlayableRotatorCell(p, r, false)
                case PlayableGeneratorCell(p, o, _) => PlayableGeneratorCell(p, o, false)
                case PlayableEnemyCell(p, _)        => PlayableEnemyCell(p, false)
                case PlayableMoverCell(p, o, _)     => PlayableMoverCell(p, o, false)
                case PlayableBlockCell(p, d, _)     => PlayableBlockCell(p, d, false)
                case PlayableWallCell(p, _)         => PlayableWallCell(p, false)
              })
          ),
          gameModel.state.levelCurrentState.playableArea
        )
      }
    }
    describe("after calling reset") {
      it("should create a new instance of itself") {
        gameModel should not equals gameModel.reset
      }
    }
    describe("after updating a cell position") {
      it("should create a new instance of itself") {
        gameModel should not equals gameModel.moveCell((2, 2))((3, 3))
      }
      it("should update the board with the same cell in the new position") {
        gameModel.moveCell((2, 2))((3, 3)).state.levelCurrentState.board shouldBe convertBoard(
          Board(
            BaseMoverCell((2, 2), Orientation.Right),
            BaseBlockCell((3, 3), Push.Both),
            BaseEnemyCell((4, 4))
          )
        )
      }
    }
    describe("after updating the game") {
      it("should create a new instance of itself") {
        gameModel should not equals gameModel.update
      }
      it("should update the board") {
        gameModel.update.state.levelCurrentState.board shouldBe convertBoard(
          Board(
            BaseMoverCell((2, 1), Orientation.Right),
            BaseBlockCell((3, 3), Push.Both),
            BaseEnemyCell((4, 4))
          )
        )
      }
    }
  }
}
