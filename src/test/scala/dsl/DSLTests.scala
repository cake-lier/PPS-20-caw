package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.*
import it.unibo.pps.caw.dsl.CellsAtWorkDSL.*
import it.unibo.pps.caw.dsl.errors.BoardBuilderError
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayOutputStream

class DSLTests extends AnyFunSpec with Matchers {
  private val boardDimensions: Dimensions = Dimensions(30, 40)
  private val playableAreaDimensions: Dimensions = Dimensions(10, 20)
  private val playableAreaPosition: Position = Position(0, 0)
  private val playableArea: PlayableArea = PlayableArea(playableAreaDimensions)(playableAreaPosition)
  private val mover: OrientableCell = OrientableCell(Orientation.Right)(Position(1, 2))
  private val generator: OrientableCell = OrientableCell(Orientation.Left)(Position(3, 4))
  private val rotator: RotatableCell = RotatableCell(Rotation.Clockwise)(Position(5, 6))
  private val block: PushableCell = PushableCell(Push.Vertical)(Position(7, 8))
  private val enemy: Cell = Cell(Position(9, 10))
  private val wall: Cell = Cell(Position(11, 12))
  private val cellsArea: Dimensions = Dimensions(2, 2)

  describe("The DSL") {
    describe("when asked to print a correctly constructed board") {
      it("should correctly print the constructed board") {
        val out: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(out) {
          buildBoardWithDSL()
        }
        out.toString shouldBe Board(
          boardDimensions,
          playableArea,
          Set(mover),
          Set(generator),
          Set(rotator),
          Set(block),
          Set(enemy),
          Set(wall)
        ).toString
      }
    }

    describe("when using the words for inserting multiple cells at the same time") {
      it("should correctly print the constructed board") {
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
              .rotating(rotator.rotation)
              .at(rotator.position.x, rotator.position.y)
            hasBlockCells
              .inAnArea(cellsArea.width, cellsArea.height)
              .pushable(block.push)
              .at(block.position.x, block.position.y)
            hasEnemyCells inAnArea (cellsArea.width, cellsArea.height) at (enemy.position.x, enemy.position.y)
            hasWallCells inAnArea (cellsArea.width, cellsArea.height) at (wall.position.x, wall.position.y)
            printIt
          }
        }
        out.toString shouldBe Board(
          boardDimensions,
          playableArea,
          duplicateCells(OrientableCell(mover.orientation), mover.position),
          duplicateCells(OrientableCell(generator.orientation), generator.position),
          duplicateCells(RotatableCell(rotator.rotation), rotator.position),
          duplicateCells(PushableCell(block.push), block.position),
          duplicateCells(Cell.apply, enemy.position),
          duplicateCells(Cell.apply, wall.position)
        ).toString
      }
    }

    describe("when asked to print a board without dimensions") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(boardDimensions = None)
        }
        err.toString shouldBe BoardBuilderError.DimensionsUnset.message
      }
    }

    describe("when asked to print a board without a playable area") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(playableArea = None)
        }
        err.toString shouldBe BoardBuilderError.PlayableAreaUnset.message
      }
    }

    describe("when asked to print a board with negative dimensions") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(boardDimensions = Some(Dimensions(-30, 40)))
        }
        err.toString shouldBe BoardBuilderError.NegativeDimensions.message
      }
    }

    describe("when asked to print a board with a playable area with negative dimensions") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(playableArea = Some(PlayableArea(Dimensions(-30, 40))(playableAreaPosition)))
        }
        err.toString shouldBe BoardBuilderError.NegativeDimensions.message
      }
    }

    describe("when asked to print a board with a playable area with a negative position") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(playableArea = Some(PlayableArea(playableAreaDimensions)(Position(-1, 0))))
        }
        err.toString shouldBe BoardBuilderError.NegativePosition.message
      }
    }

    describe("when asked to print a board with a playable area outside the board bounds") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(playableArea = Some(PlayableArea(playableAreaDimensions)(Position(25, 25))))
        }
        err.toString shouldBe BoardBuilderError.PlayableAreaNotInBounds.message
      }
    }

    describe("when asked to print a board with two cells in the same position") {
      it("should print an error on stderr") {
        val position: Position = Position(10, 10)
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(mover = OrientableCell(Orientation.Right)(position), enemy = Cell(position))
        }
        err.toString shouldBe BoardBuilderError.SamePositionForDifferentCells.message
      }
    }

    describe("when asked to print a board with a cell with a negative position") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(generator = OrientableCell(Orientation.Left)(Position(0, -5)))
        }
        err.toString shouldBe BoardBuilderError.NegativePosition.message
      }
    }

    describe("when asked to print a board with a cell outside the board bounds") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(block = PushableCell(Push.Vertical)(Position(50, 50)))
        }
        err.toString shouldBe BoardBuilderError.CellOutsideBounds.message
      }
    }
  }

  private def duplicateCells[A <: Cell](cellBuilder: Position => A, position: Position): Set[A] =
    Set.from(for {
      x <- 0 until cellsArea.width
      y <- 0 until cellsArea.height
    } yield cellBuilder(Position(position.x + x, position.y + y)))

  private def buildBoardWithDSL(
      boardDimensions: Option[Dimensions] = Some(boardDimensions),
      playableArea: Option[PlayableArea] = Some(playableArea),
      mover: OrientableCell = mover,
      generator: OrientableCell = generator,
      rotator: RotatableCell = rotator,
      block: PushableCell = block,
      enemy: Cell = enemy,
      wall: Cell = wall
  ): Unit = {
    board {
      boardDimensions.foreach(d => withDimensions(d.width, d.height))
      playableArea.foreach(a =>
        hasPlayableArea withDimensions (a.dimensions.width, a.dimensions.height) at (a.position.x, a.position.y)
      )
      hasMoverCell facing mover.orientation at (mover.position.x, mover.position.y)
      hasGeneratorCell facing generator.orientation at (generator.position.x, generator.position.y)
      hasRotatorCell rotating rotator.rotation at (rotator.position.x, rotator.position.y)
      hasBlockCell pushable block.push at (block.position.x, block.position.y)
      hasEnemyCell at (enemy.position.x, enemy.position.y)
      hasWallCell at (wall.position.x, wall.position.y)
      printIt
    }
  }
}
