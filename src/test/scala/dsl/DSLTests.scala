package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.*
import it.unibo.pps.caw.dsl.CellsAtWorkDSL.*
import it.unibo.pps.caw.dsl.errors.BoardBuilderError
import it.unibo.pps.caw.dsl.BoardSerializer
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayOutputStream

class DSLTests extends AnyFunSpec with Matchers {
  private val boardDimensions: Dimensions = Dimensions(30, 40)
  private val playableAreaDimensions: Dimensions = Dimensions(10, 20)
  private val playableAreaPosition: Position = Position(0, 0)
  private val playableArea: PlayableArea = PlayableArea(playableAreaDimensions)(playableAreaPosition)
  private val moverOrientation: OrientationWord = right
  private val mover: OrientableCell = OrientableCell(moverOrientation.orientation)(Position(1, 2))
  private val generatorOrientation: OrientationWord = left
  private val generator: OrientableCell = OrientableCell(generatorOrientation.orientation)(Position(3, 4))
  private val rotatorRotation: RotationWord = clockwise
  private val rotator: RotatableCell = RotatableCell(rotatorRotation.rotation)(Position(5, 6))
  private val blockPush: PushWord = vertically
  private val block: PushableCell = PushableCell(blockPush.push)(Position(7, 8))
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
        out.toString shouldBe BoardSerializer.serialize(
          Board(
            boardDimensions,
            playableArea,
            Set(mover),
            Set(generator),
            Set(rotator),
            Set(block),
            Set(enemy),
            Set(wall)
          )
        )
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
              .facing(moverOrientation)
              .at(mover.position.x, mover.position.y)
            hasGeneratorCells
              .inAnArea(cellsArea.width, cellsArea.height)
              .facing(generatorOrientation)
              .at(generator.position.x, generator.position.y)
            hasRotatorCells
              .inAnArea(cellsArea.width, cellsArea.height)
              .rotating(rotatorRotation)
              .at(rotator.position.x, rotator.position.y)
            hasBlockCells
              .inAnArea(cellsArea.width, cellsArea.height)
              .pushable(blockPush)
              .at(block.position.x, block.position.y)
            hasEnemyCells inAnArea (cellsArea.width, cellsArea.height) at (enemy.position.x, enemy.position.y)
            hasWallCells inAnArea (cellsArea.width, cellsArea.height) at (wall.position.x, wall.position.y)
            printIt
          }
        }
        out.toString shouldBe BoardSerializer.serialize(
          Board(
            boardDimensions,
            playableArea,
            duplicateCells(OrientableCell(mover.orientation), mover.position),
            duplicateCells(OrientableCell(generator.orientation), generator.position),
            duplicateCells(RotatableCell(rotator.rotation), rotator.position),
            duplicateCells(PushableCell(block.push), block.position),
            duplicateCells(Cell.apply, enemy.position),
            duplicateCells(Cell.apply, wall.position)
          )
        )
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

    import java.util.stream.Collectors
    import java.io.{File, InputStreamReader, BufferedReader, InputStream}
    import java.nio.file.{Files, Paths}
    import scala.util.Using

    describe("when asked to save a board to file") {
      it("should produce the correct file") {
        val fileName: String = "level.json"
        val path: String = System.getProperty("user.home") + File.separator + fileName
        board {
          withDimensions(boardDimensions.width, boardDimensions.height)
          hasPlayableArea
            .withDimensions(playableArea.dimensions.width, playableArea.dimensions.height)
            .at(playableArea.position.x, playableArea.position.y)
          hasMoverCell facing (moverOrientation) at (mover.position.x, mover.position.y)
          hasGeneratorCell facing (generatorOrientation) at (generator.position.x, generator.position.y)
          hasRotatorCell rotating (rotatorRotation) at (rotator.position.x, rotator.position.y)
          hasBlockCell pushable (blockPush) at (block.position.x, block.position.y)
          hasEnemyCell at (enemy.position.x, enemy.position.y)
          hasWallCell at (wall.position.x, wall.position.y)
          saveIt(path)
        }
        Using(new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(fileName)))) { r =>
          Files.readString(Paths.get(path)) shouldBe r.lines.collect(Collectors.joining(System.lineSeparator))
        }
        Files.delete(Paths.get(path))
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
      hasMoverCell facing moverOrientation at (mover.position.x, mover.position.y)
      hasGeneratorCell facing generatorOrientation at (generator.position.x, generator.position.y)
      hasRotatorCell rotating rotatorRotation at (rotator.position.x, rotator.position.y)
      hasBlockCell pushable blockPush at (block.position.x, block.position.y)
      hasEnemyCell at (enemy.position.x, enemy.position.y)
      hasWallCell at (wall.position.x, wall.position.y)
      printIt
    }
  }
}
