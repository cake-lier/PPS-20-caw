package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.cell.{BaseEnemyCell, PlayableCell, PlayableEnemyCell, PlayableWallCell}
import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[LevelEditorModel]] trait */
class LevelEditorModelTest extends AnyFunSpec with Matchers {
  private val dimensions: Dimensions = (20, 20)
  private val playableAreaDimensions: Dimensions = (5, 5)
  private val playableAreaPosition: Position = (0, 0)
  private val emptyLevel: LevelBuilderState = createLevelWithWalls(cells = Board.empty)
  private val enemy1: BaseEnemyCell = BaseEnemyCell((1, 1))
  private val playableEnemy1: PlayableEnemyCell = PlayableEnemyCell((1, 1))(true)
  private val enemy2: BaseEnemyCell = BaseEnemyCell((2, 2))
  private val playableEnemy2: PlayableEnemyCell = PlayableEnemyCell((2, 2))(true)
  private val enemy3: BaseEnemyCell = BaseEnemyCell((3, 3))
  private val playableEnemy3: PlayableEnemyCell = PlayableEnemyCell((3, 3))(true)

  describe("LevelEditorModel") {
    describe("when new") {
      it("should contain an empty LevelBuilderState") {
        LevelEditorModel(dimensions.width, dimensions.height).currentState shouldBe emptyLevel
      }
    }
    describe("when resetted") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height).resetLevel should not equals
          LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should contain an empty LevelBuilderState") {
        LevelEditorModel(dimensions.width, dimensions.height).resetLevel.currentState shouldBe emptyLevel
      }
    }
    describe("when a cell is set") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height).setCell(enemy1) should not equals
          LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilderState") {
        LevelEditorModel(dimensions.width, dimensions.height).setCell(enemy1).currentState shouldBe
          createLevelWithWalls(cells = Board(playableEnemy1))
      }
    }
    describe("when a cell is removed") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height)
          .setCell(enemy1)
          .unsetCell(enemy1.position) should not equals LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilderState") {
        LevelEditorModel(dimensions.width, dimensions.height)
          .setCell(enemy1)
          .setCell(enemy2)
          .setCell(enemy3)
          .unsetCell(enemy1.position)
          .currentState shouldBe createLevelWithWalls(cells = Board(playableEnemy2, playableEnemy3))
      }
    }
    describe("when a PlayableArea is set") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height)
          .setPlayableArea(playableAreaPosition, playableAreaDimensions) should not equals
          LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilderState") {
        LevelEditorModel(dimensions.width, dimensions.height)
          .setPlayableArea(playableAreaPosition, playableAreaDimensions)
          .currentState shouldBe
          createLevelWithWalls(
            playableArea = Some(PlayableArea(playableAreaDimensions)(playableAreaPosition)),
            cells = Board.empty
          )
      }
    }
    describe("when a PlayableArea is removed") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height).unsetPlayableArea should not equals
          LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilderState") {
        LevelEditorModel(dimensions.width, dimensions.height).unsetPlayableArea.currentState shouldBe
          createLevelWithWalls()
      }
    }
  }
  private def createLevelWithWalls(
    playableArea: Option[PlayableArea] = None,
    cells: Set[PlayableCell] = Set.empty
  ): LevelBuilderState =
    val walls: Board[PlayableCell] = Set(
      (0 to dimensions.width + 1).map(i => PlayableWallCell((i, 0))(playable = false)),
      (0 to dimensions.width + 1).map(i => PlayableWallCell((i, dimensions.height + 1))(playable = false)),
      (1 to dimensions.height).map(i => PlayableWallCell((0, i))(playable = false)),
      (1 to dimensions.height).map(i => PlayableWallCell((dimensions.width + 1, i))(playable = false))
    ).flatten ++ cells
    playableArea
      .map(p => LevelBuilderState(p)((dimensions.width + 2, dimensions.height + 2))(walls))
      .getOrElse(LevelBuilder((dimensions.width + 2, dimensions.height + 2))(walls))

}
