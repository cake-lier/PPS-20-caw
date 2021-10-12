package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.cell.{Orientation, PlayableCell, PlayableEnemyCell, PlayableMoverCell, PlayableWallCell}
import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[LevelBuilder]] trait. */
class LevelBuilderTest extends AnyFunSpec with Matchers {
  private val dimensions: Dimensions = Dimensions(10, 8)
  private val board: Board[PlayableCell] = Board(
    PlayableMoverCell(Orientation.Right)((3, 2))(playable = true),
    PlayableWallCell((1, 1))(playable = false),
    PlayableEnemyCell((9, 7))(playable = false)
  )
  private val playableArea: PlayableArea = PlayableArea((7, 5))((1, 1))

  describe("A level builder") {
    describe("when first created with dimensions and board") {
      it("should return the given dimensions and board") {
        val levelBuilder: LevelBuilder = LevelBuilder(dimensions)(board)
        levelBuilder.dimensions shouldBe dimensions
        levelBuilder.board shouldBe board
        levelBuilder.playableArea shouldBe None
      }
    }

    describe("when first created with playable area, dimensions and board") {
      it("should return the given playable area, dimensions and board") {
        val levelBuilder: LevelBuilder = LevelBuilder(playableArea)(dimensions)(board)
        levelBuilder.dimensions shouldBe dimensions
        levelBuilder.board shouldBe board
        levelBuilder.playableArea.get shouldBe playableArea
      }
    }

    describe("when asked to produce a modified copy of itself") {
      it("should return a new level builder with the same properties as the original, unless specified otherwise") {
        val levelBuilder: LevelBuilder = LevelBuilder(playableArea)(dimensions)(board)
        val newDimensions: Dimensions = Dimensions(11, 11)
        val newBoard: Board[PlayableCell] = Board(
          PlayableMoverCell(Orientation.Top)((3, 2))(playable = true),
          PlayableEnemyCell((7, 7))(playable = false)
        )
        val newPlayableArea: PlayableArea = PlayableArea((4, 4))((2, 2))

        levelBuilder.copy(dimensions = newDimensions) shouldBe LevelBuilder(playableArea)(newDimensions)(board)
        levelBuilder.copy(dimensions = newDimensions, playableArea = Some(newPlayableArea)) shouldBe
          LevelBuilder(newPlayableArea)(newDimensions)(board)
        levelBuilder.copy(board = newBoard) shouldBe LevelBuilder(playableArea)(dimensions)(newBoard)
      }
    }
  }
}
