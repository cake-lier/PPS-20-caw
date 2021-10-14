package it.unibo.pps.caw.game.model

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
  private val fileStorage: FileStorage = FileStorage()
  private val initialLevel: Level[PlayableCell] =
    fileStorage
      .loadResource("level01.json")
      .flatMap(LevelParser(FileStorage()).deserializeLevel)
      .map(toPlayableLevel)
      .get
  private val currentLevel: Level[PlayableCell] = initialLevel
  private val currentLevelIndex: Int = 1
  private val hasNextLevel: Boolean = false
  private val didEnemyDie: Boolean = false
  private val isCurrentLevelCompleted: Boolean = false
  private val gameState: GameState =
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
    describe("when copy constructor is called") {
      it("should create a copy except for the new value") {
        gameState.copy(isCurrentLevelCompleted = true) shouldBe GameState(
          gameState.levelInitialState,
          gameState.levelCurrentState,
          gameState.currentLevelIndex,
          gameState.hasNextLevel,
          gameState.didEnemyDie,
          isCurrentLevelCompleted = true
        )
      }
    }

  }

  private def toPlayableLevel(level: Level[BaseCell]) =
    Level(level.dimensions, Board(level.board.cells.map(_.toPlayableCell(_ => true))), level.playableArea)
}
