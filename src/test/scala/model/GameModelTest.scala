package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, Level, PlayableArea}
import it.unibo.pps.caw.common.model.cell.{
  BaseBlockCell,
  BaseCell,
  BaseEnemyCell,
  BaseGeneratorCell,
  BaseMoverCell,
  BaseRotatorCell,
  BaseWallCell,
  Orientation,
  PlayableBlockCell,
  PlayableCell,
  PlayableEnemyCell,
  PlayableGeneratorCell,
  PlayableMoverCell,
  PlayableRotatorCell,
  PlayableWallCell,
  Push
}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for class [[GameModel]] */
class GameModelTest extends AnyFunSpec with Matchers {
  private val dimensions: Dimensions = (4, 4)
  private def convertBoard(board: Board[BaseCell]): Board[PlayableCell] =
    Board(
      board
        .cells
        .map(_ match {
          case BaseRotatorCell(p, r)   => PlayableRotatorCell((p.x + 1, p.y + 1), r)
          case BaseGeneratorCell(p, o) => PlayableGeneratorCell((p.x + 1, p.y + 1), o)
          case BaseEnemyCell(p)        => PlayableEnemyCell((p.x + 1, p.y + 1))
          case BaseMoverCell(p, o)     => PlayableMoverCell((p.x + 1, p.y + 1), o)
          case BaseBlockCell(p, d)     => PlayableBlockCell((p.x + 1, p.y + 1), d)
          case BaseWallCell(p)         => PlayableWallCell((p.x + 1, p.y + 1))
        }) ++
        (0 to dimensions.width + 1).map(i => PlayableWallCell((i, 0))) ++
        (0 to dimensions.width + 1).map(i => PlayableWallCell((i, dimensions.height + 1))) ++
        (1 to dimensions.height).map(i => PlayableWallCell((0, i))) ++
        (1 to dimensions.height).map(i => PlayableWallCell((dimensions.width + 1, i)))
    )
  private val gameModel: GameModel = GameModel(
    Level(
      dimensions,
      Board(
        BaseMoverCell((1, 1), Orientation.Right),
        BaseBlockCell((3, 3), Push.Both),
        BaseEnemyCell((4, 4))
      ),
      PlayableArea((0, 0), dimensions)
    ),
    None
  )
  describe("The game model") {
    describe("when first created") {
      it("should have the current state level equals to initial state level") {
        gameModel.state.currentStateLevel shouldBe Level(
          gameModel.state.currentStateLevel.dimensions,
          Board(
            gameModel
              .state
              .initialStateLevel
              .board
              .cells
              .map(_ match {
                case PlayableRotatorCell(p, r, _)   => PlayableRotatorCell(p, r)
                case PlayableGeneratorCell(p, o, _) => PlayableGeneratorCell(p, o)
                case PlayableEnemyCell(p, _)        => PlayableEnemyCell(p)
                case PlayableMoverCell(p, o, _)     => PlayableMoverCell(p, o)
                case PlayableBlockCell(p, d, _)     => PlayableBlockCell(p, d)
                case PlayableWallCell(p, _)         => PlayableWallCell(p)
              })
          ),
          gameModel.state.currentStateLevel.playableArea
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
        gameModel should not equals gameModel.updateCell((2, 2), (3, 3))
      }
      it("should update the board with the same cell in the new position") {
        gameModel.updateCell((2, 2), (3, 3)).state.currentStateLevel.board shouldBe convertBoard(
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
        gameModel.update.state.currentStateLevel.board shouldBe convertBoard(
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
