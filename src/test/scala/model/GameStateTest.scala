package it.unibo.pps.caw
package model

import game.model.{GameModel, GameState}

import it.unibo.pps.caw.common.LevelParser
import it.unibo.pps.caw.common.model.{Board, Level}
import it.unibo.pps.caw.common.model.cell.{BaseCell, PlayableCell}
import it.unibo.pps.caw.common.storage.FileStorage
import it.unibo.pps.caw.common.model.cell.PlayableCell.toPlayableCell
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import scala.util.Using

/** Tests for class [[GameState]] */
class GameStateTest extends AnyFunSpec with Matchers {
  val initialLevel: Level[PlayableCell] =
    Using(Source.fromResource("level01.json")) { _.getLines.mkString(" ") }
      .flatMap(LevelParser(FileStorage()).deserializeLevel)
      .map(toPlayableLevel)
      .get
  val currentLevel: Level[PlayableCell] = initialLevel
  val currentLevelIndex: Int = 1
  val hasNextLevel: Boolean = false
  val didEnemyDie: Boolean = false
  val isCurrentLevelCompleted: Boolean = false
  val gameState: GameState =
    GameState(initialLevel, currentLevel, currentLevelIndex, hasNextLevel, didEnemyDie, isCurrentLevelCompleted)

  describe("GameState") {
    it("should return same levelCurrentState") {
      gameState.levelCurrentState shouldBe currentLevel
    }
    it("should return same levelInitialState") {
      gameState.levelInitialState shouldBe initialLevel
    }
    it("should return same currentLevelIndex") {
      gameState.currentLevelIndex shouldBe currentLevelIndex
    }
    it("should return same hasNextLevel") {
      gameState.hasNextLevel shouldBe hasNextLevel
    }
    it("should return same didEnemyDie") {
      gameState.didEnemyDie shouldBe didEnemyDie
    }
    it("should return same isCurrentLevelCompleted") {
      gameState.isCurrentLevelCompleted shouldBe isCurrentLevelCompleted
    }
  }

  private def toPlayableLevel(level: Level[BaseCell]) =
    Level(level.dimensions, Board(level.board.cells.map(_.toPlayableCell(_ => true))), level.playableArea)
}
