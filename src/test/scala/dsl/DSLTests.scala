package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.*
import it.unibo.pps.caw.dsl.CellsAtWorkDSL.*
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import scala.language.postfixOps
import java.io.ByteArrayOutputStream

class DSLTests extends AnyFunSpec with Matchers {
  describe("The DSL") {
    describe("when asked to print a board") {
      it("should correctly print the constructed board") {
        val boardDimensions: Dimensions = Dimensions(30, 40)
        val playableArea: PlayableArea = PlayableArea(Dimensions(10, 20))(Position(0, 0))
        val mover: OrientedCell = OrientedCell(Orientation.Right)(Position(1, 2))
        val generator: OrientedCell = OrientedCell(Orientation.Left)(Position(3, 4))
        val rotator: DirectedCell = DirectedCell(Direction.Clockwise)(Position(5, 6))
        val block: MovableCell = MovableCell(MovementDirection.Vertical)(Position(7, 8))
        val enemy: Cell = Cell(Position(9, 10))
        val wall: Cell = Cell(Position(11, 12))
        val effectiveBoard: Board = Board(
          Some(boardDimensions),
          Some(playableArea),
          Set(mover),
          Set(generator),
          Set(rotator),
          Set(block),
          Set(enemy),
          Set(wall)
        )
        val out: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(out) {
          board {
            withDimensions(boardDimensions.width, boardDimensions.height)
            hasPlayableArea
              .withDimensions(playableArea.dimensions.width, playableArea.dimensions.height)
              .at(playableArea.position.x, playableArea.position.y)
            hasMoverCell facing mover.orientation at (mover.position.x, mover.position.y)
            hasGeneratorCell facing generator.orientation at (generator.position.x, generator.position.y)
            hasRotatorCell directed rotator.direction at (rotator.position.x, rotator.position.y)
            hasBlockCell movable block.movementDirection at (block.position.x, block.position.y)
            hasEnemyCell at (enemy.position.x, enemy.position.y)
            hasWallCell at (wall.position.x, wall.position.y)
          }
        }
        out.toString shouldBe effectiveBoard.toString
      }
    }
  }
}
