package it.unibo.pps.caw.model

import it.unibo.pps.caw.common.model.{Board, Level, PlayableArea}
import it.unibo.pps.caw.common.model.cell.{Orientation, PlayableGeneratorCell, PlayableMoverCell}
import it.unibo.pps.caw.common.{FileStorage, LevelParser}
import it.unibo.pps.caw.game.model.*
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import scala.util.{Failure, Success}

class DeserializerTest extends AnyFunSpec with Matchers {
  val fileStorage = FileStorage()
  val levelParser = LevelParser(fileStorage)
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
        val jsonLevel = fileStorage.loadResource("test_level.json").get
        levelParser.deserializeLevel(jsonLevel).getOrElse(None) shouldBe Level(
          (50, 60),
          Board(
            PlayableMoverCell((1, 2), Orientation.Right, true),
            PlayableMoverCell((0, 0), Orientation.Top, false),
            PlayableGeneratorCell((1, 2), Orientation.Right, true),
            PlayableGeneratorCell((0, 0), Orientation.Top, false)
          ),
          PlayableArea((1, 2), (20, 30))
        )
      }
    }
  }
}
