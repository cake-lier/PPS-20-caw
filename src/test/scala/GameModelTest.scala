import it.unibo.pps.caw.model.*
import model.GameModel
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.language.implicitConversions

/** Tests for class [[GameModel]] */
class GameModelTest extends AnyFunSpec with Matchers {
  private def gameModel = GameModel(setupBoard)
  private val setupBoard = Board(
    SetupMoverCell((1, 1), Orientation.Right, true),
    SetupBlockCell((3, 3), AllowedMovement.Both, false),
    SetupEnemyCell((5, 5), false)
  )
  describe("GameModel") {
    describe("At the beginning, before starting the game") {
      it("Should have current board equals to initial board") {
        resetGameModel
        gameModel.currentBoard shouldBe Board(
          gameModel.initialBoard.cells
            .map(CellConverter.fromSetup)
            .toSet
        )
      }
    }
    describe("When calling reset") {
      it("Should create another instace of itself") {
        resetGameModel
        gameModel should not equals gameModel.reset
      }
    }
    describe("When update cell position") {
      it("Should create another updated instace of itself") {
        resetGameModel
        gameModel should not equals gameModel.updateCell((1, 1), (2, 2))
      }
      it("Should update the board") {
        resetGameModel
        gameModel.updateCell((1, 1), (2, 2)).currentBoard shouldBe Board(
          MoverCell((2, 2), Orientation.Right),
          BlockCell((3, 3), AllowedMovement.Both),
          EnemyCell((5, 5))
        )
      }
    }
    describe("When update game") {
      it("Should create another updated instace of itself") {
        resetGameModel
        gameModel should not equals gameModel.update()
      }
    }
  }
  private def resetGameModel = gameModel.reset
}
