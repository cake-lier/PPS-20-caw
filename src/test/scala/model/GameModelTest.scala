package it.unibo.pps.caw
package model

import game.model.*

import it.unibo.pps.caw.common.{Board, PlayableArea}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.language.implicitConversions

/** Tests for class [[GameModel]] */
class GameModelTest extends AnyFunSpec with Matchers {
  private val setupBoard = Board(
    SetupMoverCell((1, 1), Orientation.Right, true),
    SetupBlockCell((3, 3), Push.Both, false),
    SetupEnemyCell((5, 5), false)
  )
  private val level = Level(10, 10, setupBoard, playableArea = PlayableArea((0, 0), 10, 10))
  private def gameModel = Model(level)
  describe("GameModel") {
    describe("At the beginning, before starting the game") {
      it("Should have current board equals to initial board") {
        gameModel.reset
        gameModel.currentBoard shouldBe Board(
          gameModel.initialLevel.setupBoard.cells
            .map(CellConverter.fromSetup)
            .toSet
        )
      }
    }
    describe("When calling reset") {
      it("Should create another instace of itself") {
        gameModel.reset
        gameModel should not equals gameModel.reset
      }
    }
    describe("When update cell position") {
      it("Should create another updated instace of itself") {
        gameModel.reset
        gameModel should not equals gameModel.updateCell((1, 1), (2, 2))
      }
      it("Should update the board") {
        gameModel.reset
        gameModel.updateCell((1, 1), (2, 2)).currentBoard shouldBe Board(
          MoverCell((2, 2), Orientation.Right),
          BlockCell((3, 3), Push.Both),
          EnemyCell((5, 5))
        )
      }
    }
    describe("When update game") {
      it("Should create another updated instace of itself") {
        gameModel.reset
        gameModel should not equals gameModel.update()
      }
    }
  }
}
