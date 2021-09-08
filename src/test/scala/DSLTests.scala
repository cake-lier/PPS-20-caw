package it.unibo.pps.caw

import it.unibo.pps.caw.dsl.CellsAtWorkDSL.*
import it.unibo.pps.caw.dsl.*
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import java.io.ByteArrayOutputStream

class DSLTests extends AnyFunSpec with Matchers {
  describe("The DSL") {
    describe("when asked to print a board") {
      it("should correctly print the constructed board") {
        val boardDimensions: Dimensions = Dimensions(30, 40)
        val playableArea: PlayableArea = PlayableArea(Dimensions(10, 20))(Position(0, 0))
        val moverCell: OrientedCell = OrientedCell(Orientation.Right)(Position(1, 2))
        val generatorCell: OrientedCell = OrientedCell(Orientation.Left)(Position(3, 4))
        val rotatorCell: DirectedCell = DirectedCell(Direction.Clockwise)(Position(5, 6))
        val blockCell: MovableCell = MovableCell(MovementDirection.Vertical)(Position(7, 8))
        val enemyCell: Cell = Cell(Position(9, 10))
        val wallCell: Cell = Cell(Position(11, 12))
        val effectiveBoard: Board = Board(
          Some(boardDimensions),
          Some(playableArea),
          Set(moverCell),
          Set(generatorCell),
          Set(rotatorCell),
          Set(blockCell),
          Set(enemyCell),
          Set(wallCell)
        )
        val out: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(out) {
          board {
            withDimensions(boardDimensions.width, boardDimensions.height)
            hasPlayableArea
              .withDimensions(playableArea.dimensions.width, playableArea.dimensions.height)
              .at(playableArea.position.x, playableArea.position.y)
            hasMoverCell facing moverCell.orientation at (moverCell.position.x, moverCell.position.y)
            hasGeneratorCell facing generatorCell.orientation at (generatorCell.position.x, generatorCell.position.y)
            hasRotatorCell directed rotatorCell.direction at (rotatorCell.position.x, rotatorCell.position.y)
            hasBlockCell movable blockCell.movementDirection at (blockCell.position.x, blockCell.position.y)
            hasEnemyCell at (enemyCell.position.x, enemyCell.position.y)
            hasWallCell at (wallCell.position.x, wallCell.position.y)
          }
        }
        out.toString shouldBe effectiveBoard.toString
      }
    }
  }
}
