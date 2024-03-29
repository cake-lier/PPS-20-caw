package it.unibo.pps.caw
package common

import common.model.*
import common.model.cell.*
import common.storage.FileStorage

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class LevelParserTest extends AnyFunSpec with Matchers {
  private val fileStorage: FileStorage = FileStorage()
  private val levelParser: LevelParser = LevelParser(fileStorage)
  private val allCellsLevel: Level[BaseCell] = Level(
    (4, 5),
    Board(
      BaseMoverCell(Orientation.Left)((2, 0)),
      BaseBlockCell(Push.Vertical)((1, 3)),
      BaseGeneratorCell(Orientation.Top)((3, 1)),
      BaseMoverCell(Orientation.Right)((0, 0)),
      BaseMoverCell(Orientation.Top)((3, 0)),
      BaseEnemyCell((0, 4)),
      BaseMoverCell(Orientation.Down)((1, 0)),
      BaseGeneratorCell(Orientation.Left)((2, 1)),
      BaseWallCell((1, 4)),
      BaseDeleterCell((2, 4)),
      BaseRotatorCell(Rotation.Counterclockwise)((1, 2)),
      BaseBlockCell(Push.Both)((0, 3)),
      BaseRotatorCell(Rotation.Clockwise)((0, 2)),
      BaseBlockCell(Push.Horizontal)((2, 3)),
      BaseGeneratorCell(Orientation.Right)((0, 1)),
      BaseGeneratorCell(Orientation.Down)((1, 1))
    ),
    PlayableArea((1, 2))((3, 3))
  )

  describe("LevelParser") {
    describe("serializing all possible cells in game") {
      it("should produce a valid json") {
        levelParser.deserializeLevel(levelParser.serializeLevel(allCellsLevel)) match {
          case Success(l) => l shouldBe allCellsLevel
          case _          => fail()
        }
      }
    }
  }

  describe("A level") {
    describe("when empty") {
      it("should produce IllegalArgumentException") {
        levelParser.deserializeLevel("") match {
          case Failure(_: IllegalArgumentException) => succeed
          case _                                    => fail("Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with invalid format") {
      it("should produce IllegalArgumentException") {
        levelParser.deserializeLevel(fileStorage.loadResource("invalid_test_level.json").get) match {
          case Failure(_: IllegalArgumentException) => succeed
          case _                                    => fail("Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with correct format") {
      it("should produce a LevelBuilderState") {
        val jsonLevel: String = fileStorage.loadResource("all_cells_valid_file.json").get
        levelParser.deserializeLevel(jsonLevel) match {
          case Success(l) => l shouldBe allCellsLevel
          case _          => fail()
        }
      }
    }
  }
}
