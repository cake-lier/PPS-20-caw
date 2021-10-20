package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.cell.{BaseEnemyCell, PlayableCell, PlayableEnemyCell, PlayableWallCell}
import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[EditorModel]] trait */
class LevelEditorModelTest extends AnyFunSpec with Matchers {
  private val dimensions: Dimensions = (20, 20)
  private val playableAreaDimensions: Dimensions = (5, 5)
  private val playableAreaPosition: Position = (0, 0)
  private val emptyLevel: EditorModelState = createLevelWithWalls(cells = Board.empty)
  private val enemy1: BaseEnemyCell = BaseEnemyCell((1, 1))
  private val playableEnemy1: PlayableEnemyCell = PlayableEnemyCell((1, 1))(true)
  private val enemy2: BaseEnemyCell = BaseEnemyCell((2, 2))
  private val playableEnemy2: PlayableEnemyCell = PlayableEnemyCell((2, 2))(true)
  private val enemy3: BaseEnemyCell = BaseEnemyCell((3, 3))
  private val playableEnemy3: PlayableEnemyCell = PlayableEnemyCell((3, 3))(true)

  describe("EditorModel") {
    describe("when new") {
      it("should contain an empty LevelBuilderState") {
        EditorModel(dimensions.width, dimensions.height).state shouldBe emptyLevel
      }
    }
    describe("when resetted") {
      it("should return another instance of itself") {
        EditorModel(dimensions.width, dimensions.height).resetLevel should not equals
          EditorModel(dimensions.width, dimensions.height)
      }
      it("should contain an empty LevelBuilderState") {
        EditorModel(dimensions.width, dimensions.height).resetLevel.state shouldBe emptyLevel
      }
    }
    describe("when a cell is set") {
      it("should return another instance of itself") {
        EditorModel(dimensions.width, dimensions.height).addCell(enemy1) should not equals
          EditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilderState") {
        EditorModel(dimensions.width, dimensions.height).addCell(enemy1).state shouldBe
          createLevelWithWalls(cells = Board(playableEnemy1))
      }
    }
    describe("when a cell is removed") {
      it("should return another instance of itself") {
        EditorModel(dimensions.width, dimensions.height)
          .addCell(enemy1)
          .removeCell(enemy1.position) should not equals EditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilderState") {
        EditorModel(dimensions.width, dimensions.height)
          .addCell(enemy1)
          .addCell(enemy2)
          .addCell(enemy3)
          .removeCell(enemy1.position)
          .state shouldBe createLevelWithWalls(cells = Board(playableEnemy2, playableEnemy3))
      }
    }
    describe("when a PlayableArea is set") {
      it("should return another instance of itself") {
        EditorModel(dimensions.width, dimensions.height)
          .addPlayableArea(playableAreaPosition, playableAreaDimensions) should not equals
          EditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilderState") {
        EditorModel(dimensions.width, dimensions.height)
          .addPlayableArea(playableAreaPosition, playableAreaDimensions)
          .state shouldBe
          createLevelWithWalls(
            playableArea = Some(PlayableArea(playableAreaDimensions)(playableAreaPosition)),
            cells = Board.empty
          )
      }
    }
    describe("when a PlayableArea is removed") {
      it("should return another instance of itself") {
        EditorModel(dimensions.width, dimensions.height).removePlayableArea should not equals
          EditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilderState") {
        EditorModel(dimensions.width, dimensions.height).removePlayableArea.state shouldBe
          createLevelWithWalls()
      }
    }
  }
  private def createLevelWithWalls(
    playableArea: Option[PlayableArea] = None,
    cells: Set[PlayableCell] = Set.empty
  ): EditorModelState =
    val walls: Board[PlayableCell] = Set(
      (0 to dimensions.width + 1).map(i => PlayableWallCell((i, 0))(playable = false)),
      (0 to dimensions.width + 1).map(i => PlayableWallCell((i, dimensions.height + 1))(playable = false)),
      (1 to dimensions.height).map(i => PlayableWallCell((0, i))(playable = false)),
      (1 to dimensions.height).map(i => PlayableWallCell((dimensions.width + 1, i))(playable = false))
    ).flatten ++ cells
    playableArea
      .map(p => EditorModelState(p)((dimensions.width + 2, dimensions.height + 2))(walls))
      .getOrElse(EditorModelState((dimensions.width + 2, dimensions.height + 2))(walls))

}
