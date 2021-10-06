package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.model.*
import it.unibo.pps.caw.common.model.cell.{Orientation, PlayableGeneratorCell, PlayableMoverCell}
import it.unibo.pps.caw.common.storage.FileStorage
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success}

class DeserializerTest extends AnyFunSpec with Matchers {
  val fileStorage: FileStorage = FileStorage()
  val levelParser: LevelParser = LevelParser(fileStorage)

  describe("A JSON") {
    describe("when empty") {
      it("should produce IllegalArgumentException") {
        levelParser.deserializeLevel("") match {
          case Failure(x: IllegalArgumentException) => succeed
          case _                                    => fail("Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with wrong json format") {
      it("should produce IllegalArgumentException") {
        levelParser.deserializeLevel("{invalid level}") match {
          case Failure(x: IllegalArgumentException) => succeed
          case _                                    => fail("Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with nearly correct  json format") {
      it("should produce IllegalArgumentException") {
        levelParser.deserializeLevel(fileStorage.loadResource("invalid_test_level.json").get) match {
          case Failure(x: IllegalArgumentException) => succeed
          case _                                    => fail("Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with correct json") {
      it("should produce a LevelBuilder") {
        val jsonLevel: String = fileStorage.loadResource("test_level.json").get
        levelParser.deserializeLevel(jsonLevel) match {
          case Success(l) =>
            l shouldBe Level(
              (50, 60),
              Board(
                Set(
                  PlayableMoverCell(Orientation.Right)((1, 2))(true),
                  PlayableMoverCell(Orientation.Top)((0, 0))(false),
                  PlayableGeneratorCell(Orientation.Right)((1, 2))(true),
                  PlayableGeneratorCell(Orientation.Top)((0, 0))(false)
                )
              ),
              PlayableArea((20, 30))((1, 2))
            )
          case _ => fail()
        }
      }
    }
  }
}
