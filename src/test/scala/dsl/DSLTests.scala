package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.*
import it.unibo.pps.caw.dsl.CellsAtWorkDSL.*
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayOutputStream

class DSLTests extends AnyFunSpec with Matchers {
  private val boardDimensions: Dimensions = Dimensions(30, 40)
  private val playableArea: PlayableArea = PlayableArea(Dimensions(10, 20))(Position(0, 0))
  private val mover: OrientedCell = OrientedCell(Orientation.Right)(Position(1, 2))
  private val generator: OrientedCell = OrientedCell(Orientation.Left)(Position(3, 4))
  private val rotator: DirectedCell = DirectedCell(Direction.Clockwise)(Position(5, 6))
  private val block: MovableCell = MovableCell(MovementDirection.Vertical)(Position(7, 8))
  private val enemy: Cell = Cell(Position(9, 10))
  private val wall: Cell = Cell(Position(11, 12))
  private val cellsArea: Dimensions = Dimensions(2, 2)

  describe("The DSL") {
    describe("when asked to print a board") {
      it("should correctly print the constructed board") {
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

    describe("when using the words for inserting multiple cells at the same time") {
      it("should correctly print the constructed board") {
        val effectiveBoard: Board = Board(
          Some(boardDimensions),
          Some(playableArea),
          duplicateCells(OrientedCell(mover.orientation), mover.position),
          duplicateCells(OrientedCell(generator.orientation), generator.position),
          duplicateCells(DirectedCell(rotator.direction), rotator.position),
          duplicateCells(MovableCell(block.movementDirection), block.position),
          duplicateCells(Cell.apply, enemy.position),
          duplicateCells(Cell.apply, wall.position)
        )
        val out: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(out) {
          board {
            withDimensions(boardDimensions.width, boardDimensions.height)
            hasPlayableArea
              .withDimensions(playableArea.dimensions.width, playableArea.dimensions.height)
              .at(playableArea.position.x, playableArea.position.y)
            hasMoverCells
              .inAnArea(cellsArea.width, cellsArea.height)
              .facing(mover.orientation)
              .at(mover.position.x, mover.position.y)
            hasGeneratorCells
              .inAnArea(cellsArea.width, cellsArea.height)
              .facing(generator.orientation)
              .at(generator.position.x, generator.position.y)
            hasRotatorCells
              .inAnArea(cellsArea.width, cellsArea.height)
              .directed(rotator.direction)
              .at(rotator.position.x, rotator.position.y)
            hasBlockCells
              .inAnArea(cellsArea.width, cellsArea.height)
              .movable(block.movementDirection)
              .at(block.position.x, block.position.y)
            hasEnemyCells inAnArea (cellsArea.width, cellsArea.height) at (enemy.position.x, enemy.position.y)
            hasWallCells inAnArea (cellsArea.width, cellsArea.height) at (wall.position.x, wall.position.y)
          }
        }
        out.toString shouldBe effectiveBoard.toString
      }
    }
  }

  private def duplicateCells[A <: Cell](fun: Position => A, position: Position): Set[A] =
    Set.from(for {
      x <- 0 until cellsArea.width
      y <- 0 until cellsArea.height
    } yield fun(Position(position.x + x, position.y + y)))
}
