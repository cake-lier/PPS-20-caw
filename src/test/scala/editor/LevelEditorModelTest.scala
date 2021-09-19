package it.unibo.pps.caw.editor

import it.unibo.pps.caw.editor.model.{EnemyCell, Level, LevelEditorModel, PlayableArea, Position}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LevelEditorModelTest extends AnyFunSpec with Matchers {
  private val width = 20
  private val height = 20
  private val playableAreaWidth = 5
  private val playableAreaHeight = 5
  private val playableAreaPosition = Position(0, 0)
  private val emptyLevel = Level(width, height, Set.empty)
  private val enemy1 = EnemyCell(Position(1, 1), false)
  private val enemy2 = EnemyCell(Position(2, 2), false)
  private val enemy3 = EnemyCell(Position(3, 3), false)

  describe("LevelEditorModel") {
    describe("when new") {
      it("should contain an empty Level") {
        createLevelEditorModel.currentLevel shouldBe emptyLevel
      }
    }
    describe("when resetted") {
      it("should return another instance of itself") {
        createLevelEditorModel.resetLevel should not equals createLevelEditorModel
      }
      it("should contain an empty Level") {
        createLevelEditorModel.resetLevel.currentLevel shouldBe emptyLevel
      }
    }
    describe("when a cell is set") {
      it("should return another instance of itself") {
        createLevelEditorModel.setCell(enemy1) should not equals createLevelEditorModel
      }
      it("should update the Level") {
        createLevelEditorModel.setCell(enemy1).currentLevel shouldBe Level(width, height, Set(enemy1))
      }
    }
    describe("when a cell is removed") {
      it("should return another instance of itself") {
        createLevelEditorModel
          .setCell(enemy1)
          .removeCell(enemy1.position) should not equals createLevelEditorModel
      }
      it("should update the Level") {
        createLevelEditorModel
          .setCell(enemy1)
          .setCell(enemy2)
          .setCell(enemy3)
          .removeCell(enemy1.position)
          .currentLevel shouldBe Level(width, height, Set(enemy2, enemy3))
      }
    }
    describe("when a PlayableArea is set") {
      it("should return another instance of itself") {
        createLevelEditorModel
          .setPlayableArea(playableAreaPosition, playableAreaWidth, playableAreaHeight) should not equals createLevelEditorModel
      }
      it("should update the Level") {
        createLevelEditorModel
          .setPlayableArea(playableAreaPosition, playableAreaWidth, playableAreaHeight)
          .currentLevel shouldBe Level(
          width,
          height,
          Set.empty,
          PlayableArea(playableAreaPosition, playableAreaWidth, playableAreaHeight)
        )
      }
    }
    describe("when a PlayableArea is removed") {
      it("should return another instance of itself") {
        createLevelEditorModel.removePlayableArea should not equals createLevelEditorModel
      }
      it("should update the Level") {
        createLevelEditorModel.removePlayableArea.currentLevel shouldBe Level(width, height, Set.empty)
      }
    }
  }
  private def createLevelEditorModel = LevelEditorModel(width, height)
}
