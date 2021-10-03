package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, Level, PlayableArea}
import it.unibo.pps.caw.common.model.cell.PlayableCell.toPlayableCell
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
        case BaseRotatorCell(r, p)   => BaseRotatorCell(r)((p.x + 1, p.y + 1))
        case BaseGeneratorCell(o, p) => BaseGeneratorCell(o)((p.x + 1, p.y + 1))
        case BaseEnemyCell(p)        => BaseEnemyCell((p.x + 1, p.y + 1))
        case BaseMoverCell(o, p)     => BaseMoverCell(o)((p.x + 1, p.y + 1))
        case BaseBlockCell(d, p)     => BaseBlockCell(d)((p.x + 1, p.y + 1))
        case BaseWallCell(p)         => BaseWallCell((p.x + 1, p.y + 1))
      })
      .map(_.toPlayableCell(_ => false)) ++
      (0 to dimensions.width + 1).map(i => PlayableWallCell((i, 0))(false)) ++
      (0 to dimensions.width + 1).map(i => PlayableWallCell((i, dimensions.height + 1))(false)) ++
      (1 to dimensions.height).map(i => PlayableWallCell((0, i))(false)) ++
      (1 to dimensions.height).map(i => PlayableWallCell((dimensions.width + 1, i))(false))
  private val gameModel: GameModel = GameModel(
    rulesEngine,
    Level(
      dimensions,
      Board(
        BaseMoverCell(Orientation.Right)((1, 1)),
        BaseBlockCell(Push.Both)((3, 3)),
        BaseEnemyCell((4, 4))
      ),
      PlayableArea(dimensions)((0, 0))
    )
  )
  describe("The game model") {
    describe("when first created") {
      it("should have the current state level equals to initial state level") {
        gameModel.state.levelCurrentState shouldBe Level(
          gameModel.state.levelCurrentState.dimensions,
          Board(
            gameModel
              .state
              .levelInitialState
              .board
              .cells
              .map(_ match {
                case PlayableRotatorCell(r, p, _)   => PlayableRotatorCell(r)(p)(false)
                case PlayableGeneratorCell(o, p, _) => PlayableGeneratorCell(o)(p)(false)
                case PlayableEnemyCell(p, _)        => PlayableEnemyCell(p)(false)
                case PlayableMoverCell(o, p, _)     => PlayableMoverCell(o)(p)(false)
                case PlayableBlockCell(d, p, _)     => PlayableBlockCell(d)(p)(false)
                case PlayableWallCell(p, _)         => PlayableWallCell(p)(false)
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
            BaseMoverCell(Orientation.Right)((2, 2)),
            BaseBlockCell(Push.Both)((3, 3)),
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
            BaseMoverCell(Orientation.Right)((2, 1)),
            BaseBlockCell(Push.Both)((3, 3)),
            BaseEnemyCell((4, 4))
          )
        )
      }
    }
  }
}
