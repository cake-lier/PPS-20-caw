package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.common.model.{Dimensions, Level, PlayableArea, Position}
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.LevelParser
import it.unibo.pps.caw.common.storage.FileStorage
import it.unibo.pps.caw.dsl.CellsAtWorkDSL.*
import it.unibo.pps.caw.dsl.errors.BoardBuilderError
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.ByteArrayOutputStream

/** Tests for all the DSL operations, even when wrongly used. */
class DSLTests extends AnyFunSpec with Matchers {
  private val boardDimensions: Dimensions = (30, 40)
  private val playableAreaDimensions: Dimensions = (10, 20)
  private val playableAreaPosition: Position = (0, 0)
  private val playableArea: PlayableArea = PlayableArea(playableAreaDimensions)(playableAreaPosition)
  private val moverOrientation: OrientationWord = right
  private val mover: BaseMoverCell = BaseMoverCell(moverOrientation.orientation)((1, 2))
  private val generatorOrientation: OrientationWord = left
  private val generator: BaseGeneratorCell = BaseGeneratorCell(generatorOrientation.orientation)((3, 4))
  private val rotatorRotation: RotationWord = clockwise
  private val rotator: BaseRotatorCell = BaseRotatorCell(rotatorRotation.rotation)((5, 6))
  private val blockPush: PushWord = vertically
  private val block: BaseBlockCell = BaseBlockCell(blockPush.push)((7, 8))
  private val enemy: BaseEnemyCell = BaseEnemyCell((9, 10))
  private val wall: BaseWallCell = BaseWallCell((11, 12))
  private val deleter: BaseDeleterCell = BaseDeleterCell((13, 14))
  private val cellsArea: Dimensions = (2, 2)
  private val fileStorage: FileStorage = FileStorage()
  private val levelParser: LevelParser = LevelParser(fileStorage)

  describe("The DSL") {
    describe("when asked to print a correctly constructed board") {
      it("should correctly print the constructed board") {
        val out: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(out) {
          buildBoardWithDSL()
        }
        out.toString shouldBe levelParser.serializeLevel(
          Level(
            boardDimensions,
            Set(mover, generator, rotator, block, enemy, wall, deleter),
            playableArea
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
            hasDeleterCells inAnArea (cellsArea.width, cellsArea.height) at (deleter.position.x, deleter.position.y)
            printIt
          }
        }
        out.toString shouldBe levelParser.serializeLevel(
          Level(
            boardDimensions,
            Set(
              duplicateCells(BaseMoverCell(mover.orientation), mover.position),
              duplicateCells(BaseGeneratorCell(generator.orientation), generator.position),
              duplicateCells(BaseRotatorCell(rotator.rotation), rotator.position),
              duplicateCells(BaseBlockCell(block.push), block.position),
              duplicateCells(BaseEnemyCell.apply, enemy.position),
              duplicateCells(BaseWallCell.apply, wall.position),
              duplicateCells(BaseDeleterCell.apply, deleter.position)
            ).flatten,
            playableArea
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
          buildBoardWithDSL(mover = BaseMoverCell(Orientation.Right)(position), enemy = BaseEnemyCell(position))
        }
        err.toString shouldBe BoardBuilderError.SamePositionForDifferentCells.message
      }
    }

    describe("when asked to print a board with a cell with a negative position") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(generator = BaseGeneratorCell(Orientation.Left)(Position(0, -5)))
        }
        err.toString shouldBe BoardBuilderError.NegativePosition.message
      }
    }

    describe("when asked to print a board with a cell outside the board bounds") {
      it("should print an error on stderr") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          buildBoardWithDSL(block = BaseBlockCell(Push.Vertical)(Position(50, 50)))
        }
        err.toString shouldBe BoardBuilderError.CellOutsideBounds.message
      }
    }

    import java.nio.file.{Files, Paths}
    import scala.util.Using
    import scala.io.Source

    describe("when asked to save a board to file") {
      it("should produce the correct file") {
        val fileName: String = "level.json"
        val path: String = Paths.get(System.getProperty("user.home"), fileName).toString
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
          hasDeleterCell at (deleter.position.x, deleter.position.y)
          saveIt(path)
        }
        Using(Source.fromResource(fileName))(e =>
          Using(Source.fromFile(path))(a => a.mkString shouldBe e.mkString).recover(_ => fail())
        ).recover(_ => fail())
        Files.delete(Paths.get(path))
      }
    }
  }

  /* Duplicates a given cell given a function for building it and the needed properties. */
  private def duplicateCells[A <: BaseCell](builder: Position => A, position: Position): Set[A] =
    Set.from(for {
      x <- 0 until cellsArea.width
      y <- 0 until cellsArea.height
    } yield builder(Position(position.x + x, position.y + y)))

  /* Uses the DSL in a standard way so as to repeat with different parameters the use of the DSL operations. */
  private def buildBoardWithDSL(
    boardDimensions: Option[Dimensions] = Some(boardDimensions),
    playableArea: Option[PlayableArea] = Some(playableArea),
    mover: BaseMoverCell = mover,
    generator: BaseGeneratorCell = generator,
    rotator: BaseRotatorCell = rotator,
    block: BaseBlockCell = block,
    enemy: BaseEnemyCell = enemy,
    wall: BaseWallCell = wall,
    deleter: BaseDeleterCell = deleter
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
      hasDeleterCell at (deleter.position.x, deleter.position.y)
      printIt
    }
  }
}
