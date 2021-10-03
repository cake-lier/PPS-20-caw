package it.unibo.pps.caw.editor

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}
import it.unibo.pps.caw.common.model.cell.{BaseEnemyCell, PlayableEnemyCell}
import it.unibo.pps.caw.editor.model.{LevelBuilder, LevelEditorModel}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LevelEditorModelTest extends AnyFunSpec with Matchers {
  private val dimensions: Dimensions = (20, 20)
  private val playableAreaDimensions: Dimensions = (5, 5)
  private val playableAreaPosition: Position = (0, 0)
  private val emptyLevel: LevelBuilder = LevelBuilder(dimensions)(Board.empty)
  private val enemy1: BaseEnemyCell = BaseEnemyCell((1, 1))
  private val playableEnemy1: PlayableEnemyCell = PlayableEnemyCell((1, 1))(true)
  private val enemy2: BaseEnemyCell = BaseEnemyCell((2, 2))
  private val playableEnemy2: PlayableEnemyCell = PlayableEnemyCell((2, 2))(true)
  private val enemy3: BaseEnemyCell = BaseEnemyCell((3, 3))
  private val playableEnemy3: PlayableEnemyCell = PlayableEnemyCell((3, 3))(true)

  describe("LevelEditorModel") {
    describe("when new") {
      it("should contain an empty LevelBuilder") {
        LevelEditorModel(dimensions.width, dimensions.height).currentLevel shouldBe emptyLevel
      }
    }
    describe("when resetted") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height).resetLevel should not equals
          LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should contain an empty LevelBuilder") {
        LevelEditorModel(dimensions.width, dimensions.height).resetLevel.currentLevel shouldBe emptyLevel
      }
    }
    describe("when a cell is set") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height).setCell(enemy1) should not equals
          LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilder") {
        LevelEditorModel(dimensions.width, dimensions.height).setCell(enemy1).currentLevel shouldBe
          LevelBuilder(dimensions)(Board(playableEnemy1))
      }
    }
    describe("when a cell is removed") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height)
          .setCell(enemy1)
          .unsetCell(enemy1.position) should not equals LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilder") {
        LevelEditorModel(dimensions.width, dimensions.height)
          .setCell(enemy1)
          .setCell(enemy2)
          .setCell(enemy3)
          .unsetCell(enemy1.position)
          .currentLevel shouldBe LevelBuilder(dimensions)(Board(playableEnemy2, playableEnemy3))
      }
    }
    describe("when a PlayableArea is set") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height)
          .setPlayableArea(playableAreaPosition, playableAreaDimensions) should not equals
          LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilder") {
        LevelEditorModel(dimensions.width, dimensions.height)
          .setPlayableArea(playableAreaPosition, playableAreaDimensions)
          .currentLevel shouldBe LevelBuilder(PlayableArea(playableAreaDimensions)(playableAreaPosition))(dimensions)(Board.empty)
      }
    }
    describe("when a PlayableArea is removed") {
      it("should return another instance of itself") {
        LevelEditorModel(dimensions.width, dimensions.height).unsetPlayableArea should not equals
          LevelEditorModel(dimensions.width, dimensions.height)
      }
      it("should update the LevelBuilder") {
        LevelEditorModel(dimensions.width, dimensions.height).unsetPlayableArea.currentLevel shouldBe
          LevelBuilder(dimensions)(Board.empty)
      }
    }
  }
}
