package it.unibo.pps.caw.editor

import it.unibo.pps.caw.common.model.{Board, PlayableArea, Position}
import it.unibo.pps.caw.common.model.cell.{BaseEnemyCell, PlayableEnemyCell}
import it.unibo.pps.caw.editor.model.{LevelBuilder, LevelEditorModel}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class LevelEditorModelTest extends AnyFunSpec with Matchers {
  private val width = 20
  private val height = 20
  private val playableAreaWidth = 5
  private val playableAreaHeight = 5
  private val playableAreaPosition = Position(0, 0)
  private val emptyLevel = LevelBuilder(width, height, Board.empty)
  private val enemy1 = BaseEnemyCell((1, 1))
  private val playableEnemy1 = PlayableEnemyCell((1, 1), true)
  private val enemy2 = BaseEnemyCell((2, 2))
  private val playableEnemy2 = PlayableEnemyCell((2, 2), true)
  private val enemy3 = BaseEnemyCell((3, 3))
  private val playableEnemy3 = PlayableEnemyCell((3, 3), true)

  describe("LevelEditorModel") {
    describe("when new") {
      it("should contain an empty LevelBuilder") {
        createLevelEditorModel.currentLevel shouldBe emptyLevel
      }
    }
    describe("when resetted") {
      it("should return another instance of itself") {
        createLevelEditorModel.resetLevel should not equals createLevelEditorModel
      }
      it("should contain an empty LevelBuilder") {
        createLevelEditorModel.resetLevel.currentLevel shouldBe emptyLevel
      }
    }
    describe("when a cell is set") {
      it("should return another instance of itself") {
        createLevelEditorModel.setCell(enemy1) should not equals createLevelEditorModel
      }
      it("should update the LevelBuilder") {
        createLevelEditorModel.setCell(enemy1).currentLevel shouldBe LevelBuilder(width, height, Board(playableEnemy1))
      }
    }
    describe("when a cell is removed") {
      it("should return another instance of itself") {
        createLevelEditorModel
          .setCell(enemy1)
          .unsetCell(enemy1.position) should not equals createLevelEditorModel
      }
      it("should update the LevelBuilder") {
        createLevelEditorModel
          .setCell(enemy1)
          .setCell(enemy2)
          .setCell(enemy3)
          .unsetCell(enemy1.position)
          .currentLevel shouldBe LevelBuilder(width, height, Board(playableEnemy2, playableEnemy3))
      }
    }
    describe("when a PlayableArea is set") {
      it("should return another instance of itself") {
        createLevelEditorModel
          .setPlayableArea(playableAreaPosition, (playableAreaWidth, playableAreaHeight)) should not equals createLevelEditorModel
      }
      it("should update the LevelBuilder") {
        createLevelEditorModel
          .setPlayableArea(playableAreaPosition, (playableAreaWidth, playableAreaHeight))
          .currentLevel shouldBe LevelBuilder(
          width,
          height,
          Board.empty,
          PlayableArea(playableAreaPosition, (playableAreaWidth, playableAreaHeight))
        )
      }
    }
    describe("when a PlayableArea is removed") {
      it("should return another instance of itself") {
        createLevelEditorModel.unsetPlayableArea should not equals createLevelEditorModel
      }
      it("should update the LevelBuilder") {
        createLevelEditorModel.unsetPlayableArea.currentLevel shouldBe LevelBuilder(width, height, Board.empty)
      }
    }
  }
  private def createLevelEditorModel = LevelEditorModel(width, height)
}
