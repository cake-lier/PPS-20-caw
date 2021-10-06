package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.{FilePicker, LevelParser}
import it.unibo.pps.caw.common.model.{Board, Dimensions, Level, PlayableArea}
import it.unibo.pps.caw.common.model.cell.PlayableCell.toPlayableCell
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.storage.FileStorage
import it.unibo.pps.caw.game.model.engine.RulesEngine
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import scala.util.Using

/** Tests for class [[GameModel]] */
class GameModelTest extends AnyFunSpec with Matchers {
  private val rulesEngine: RulesEngine = RulesEngine(loadFile("cellmachine.pl"))
  private val fileStorage: FileStorage = FileStorage()
  private val levelParser: LevelParser = LevelParser(fileStorage)
  private val level1: Level[BaseCell] = levelParser.deserializeLevel(loadFile("level01.json")).get
  private val level2: Level[BaseCell] = levelParser.deserializeLevel(loadFile("level02.json")).get
  private val gameModelSingleLevel: GameModel = GameModel(rulesEngine, level1)
  private val gameModelMultipleLevels: GameModel = GameModel(rulesEngine, Seq(level1, level2), 1)

  describe("The game model") {
    describe("when first created") {
      it("should have, with single level, the current state level equals to initial state level") {
        gameModelSingleLevel.state.levelCurrentState shouldBe Level(
          gameModelSingleLevel.state.levelCurrentState.dimensions,
          Board(
            gameModelSingleLevel.state.levelInitialState.board.cells
              .map(_ match {
                case PlayableRotatorCell(r, p, _)   => PlayableRotatorCell(r)(p)(false)
                case PlayableGeneratorCell(o, p, _) => PlayableGeneratorCell(o)(p)(false)
                case PlayableEnemyCell(p, _)        => PlayableEnemyCell(p)(false)
                case PlayableMoverCell(o, p, _)     => PlayableMoverCell(o)(p)(false)
                case PlayableBlockCell(d, p, _)     => PlayableBlockCell(d)(p)(false)
                case PlayableWallCell(p, _)         => PlayableWallCell(p)(false)
              })
          ),
          gameModelSingleLevel.state.levelCurrentState.playableArea
        )
      }
      it("should have, with multiple levels, the current state level equals to initial state level") {
        gameModelMultipleLevels.state.levelCurrentState shouldBe Level(
          gameModelMultipleLevels.state.levelCurrentState.dimensions,
          Board(
            gameModelMultipleLevels.state.levelInitialState.board.cells
              .map(_ match {
                case PlayableRotatorCell(r, p, _)   => PlayableRotatorCell(r)(p)(false)
                case PlayableGeneratorCell(o, p, _) => PlayableGeneratorCell(o)(p)(false)
                case PlayableEnemyCell(p, _)        => PlayableEnemyCell(p)(false)
                case PlayableMoverCell(o, p, _)     => PlayableMoverCell(o)(p)(false)
                case PlayableBlockCell(d, p, _)     => PlayableBlockCell(d)(p)(false)
                case PlayableWallCell(p, _)         => PlayableWallCell(p)(false)
              })
          ),
          gameModelMultipleLevels.state.levelCurrentState.playableArea
        )
      }
    }
    describe("after calling reset") {
      it("With single level, should create a new instance of itself") {
        gameModelSingleLevel should not equals gameModelSingleLevel.reset
      }
      it("With multiple levels, should create a new instance of itself") {
        gameModelMultipleLevels should not equals gameModelMultipleLevels.reset
      }
    }
    describe("after updating a cell position") {
      it("With single level, should create a new instance of itself") {
        gameModelSingleLevel should not equals gameModelSingleLevel.moveCell((2, 2))((3, 3))
      }
      it("With multiple levels, should create a new instance of itself") {
        gameModelMultipleLevels should not equals gameModelSingleLevel.moveCell((2, 2))((3, 3))
      }
      it("With single level, should update the board with the same cell in the new position") {
        gameModelSingleLevel
          .moveCell((2, 2))((3, 3))
          .state
          .levelCurrentState
          .board shouldBe convertBoard(
          gameModelSingleLevel.state.levelCurrentState.dimensions,
          Board(
            BaseMoverCell(Orientation.Right)((2, 2)),
            BaseEnemyCell((6, 3))
          )
        )
      }
      it("With multiple levels, should update the board with the same cell in the new position") {
        gameModelMultipleLevels
          .moveCell((2, 2))((3, 3))
          .state
          .levelCurrentState
          .board shouldBe convertBoard(
          gameModelMultipleLevels.state.levelCurrentState.dimensions,
          Board(
            BaseMoverCell(Orientation.Right)((2, 2)),
            BaseEnemyCell((6, 3))
          )
        )
      }
    }
    describe("after updating the game") {
      it("With single level, should create a new instance of itself") {
        gameModelSingleLevel should not equals gameModelSingleLevel.update
      }
      it("With multiple level, should create a new instance of itself") {
        gameModelMultipleLevels should not equals gameModelMultipleLevels.update
      }
      it("With single level, should update the board") {
        gameModelSingleLevel.update.state.levelCurrentState.board shouldBe convertBoard(
          gameModelSingleLevel.state.levelCurrentState.dimensions,
          Board(
            BaseMoverCell(Orientation.Right)((2, 1)),
            BaseEnemyCell((6, 3))
          )
        )
      }
      it("With multiple level, should update the board") {
        gameModelMultipleLevels.update.state.levelCurrentState.board shouldBe convertBoard(
          gameModelMultipleLevels.update.state.levelCurrentState.dimensions,
          Board(
            BaseMoverCell(Orientation.Right)((2, 1)),
            BaseEnemyCell((6, 3))
          )
        )
      }
    }
    describe("trying to load next level") {
      it("With single level, level not completed, should return the same level and same GameModel") {
        gameModelMultipleLevels.nextLevel shouldBe gameModelMultipleLevels
      }
      it("With multiple level, level not completed, should return the same level and same GameModel") {
        gameModelMultipleLevels.nextLevel shouldBe gameModelMultipleLevels
      }
      it("With single level, level completed, should return the same level and same GameModel") {
        val gameEnded = gameModelSingleLevel.moveCell((2, 2))((4, 4)).update.update.update
        gameEnded.nextLevel shouldBe gameEnded
      }
      it("With multiple level, level completed, should load next level") {
        val level2WallDimensions = (level2.dimensions.width + 2, level2.dimensions.height + 2)
        gameModelMultipleLevels.moveCell((2, 2))((4, 4)).update.update.update.nextLevel.state.levelCurrentState shouldBe Level(
          level2WallDimensions,
          convertBoard(level2WallDimensions, level2.board),
          PlayableArea(level2.playableArea.dimensions)((level2.playableArea.position.x + 1, level2.playableArea.position.y + 1))
        )
      }
    }
  }
  private def loadFile(path: String): String = fileStorage.loadFile(path).get

  private def convertBoard(levelWithWallsDimensions: Dimensions, board: Board[BaseCell]): Board[PlayableCell] =
    val levelDimensions: Dimensions = (levelWithWallsDimensions.width - 2, levelWithWallsDimensions.height - 2)
    board
      .map(_ match {
        case BaseRotatorCell(r, p)   => BaseRotatorCell(r)((p.x + 1, p.y + 1))
        case BaseGeneratorCell(o, p) => BaseGeneratorCell(o)((p.x + 1, p.y + 1))
        case BaseEnemyCell(p)        => BaseEnemyCell((p.x + 1, p.y + 1))
        case BaseMoverCell(o, p)     => BaseMoverCell(o)((p.x + 1, p.y + 1))
        case BaseBlockCell(d, p)     => BaseBlockCell(d)((p.x + 1, p.y + 1))
        case BaseWallCell(p)         => BaseWallCell((p.x + 1, p.y + 1))
        case _                       => throw IllegalArgumentException()
      })
      .map(_.toPlayableCell(_ => false)) ++
      (0 to levelDimensions.width + 1).map(i => PlayableWallCell((i, 0))(false)) ++
      (0 to levelDimensions.width + 1).map(i => PlayableWallCell((i, levelDimensions.height + 1))(false)) ++
      (1 to levelDimensions.height).map(i => PlayableWallCell((0, i))(false)) ++
      (1 to levelDimensions.height).map(i => PlayableWallCell((levelDimensions.width + 1, i))(false))
}
