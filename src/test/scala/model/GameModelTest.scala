package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.{Board, Dimensions, PlayableArea}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for class [[GameModel]] */
class GameModelTest extends AnyFunSpec with Matchers {
  private val dimensions: Dimensions = (4, 4)
  private def convertBoard(board: Board[BaseCell]): Board[SetupCell] =
    Board(
      board
        .cells
        .map(_ match {
          case BaseRotatorCell(p, r)   => SetupRotatorCell((p.x + 1, p.y + 1), r)
          case BaseGeneratorCell(p, o) => SetupGeneratorCell((p.x + 1, p.y + 1), o)
          case BaseEnemyCell(p)        => SetupEnemyCell((p.x + 1, p.y + 1))
          case BaseMoverCell(p, o)     => SetupMoverCell((p.x + 1, p.y + 1), o)
          case BaseBlockCell(p, d)     => SetupBlockCell((p.x + 1, p.y + 1), d)
          case BaseWallCell(p)         => SetupWallCell((p.x + 1, p.y + 1))
        }) ++
        (0 to dimensions.width + 1).map(i => SetupWallCell((i, 0))) ++
        (0 to dimensions.width + 1).map(i => SetupWallCell((i, dimensions.height + 1))) ++
        (1 to dimensions.height).map(i => SetupWallCell((0, i))) ++
        (1 to dimensions.height).map(i => SetupWallCell((dimensions.width + 1, i)))
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
                case SetupRotatorCell(p, r, _)   => SetupRotatorCell(p, r)
                case SetupGeneratorCell(p, o, _) => SetupGeneratorCell(p, o)
                case SetupEnemyCell(p, _)        => SetupEnemyCell(p)
                case SetupMoverCell(p, o, _)     => SetupMoverCell(p, o)
                case SetupBlockCell(p, d, _)     => SetupBlockCell(p, d)
                case SetupWallCell(p, _)         => SetupWallCell(p)
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
