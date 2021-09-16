package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.game.Deserializer
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import scala.util.Failure

class DeserializerTest extends AnyFunSpec with Matchers {
  describe("A JSON") {
    describe("when empty") {
      it("should produce IllegalArgumentException") {
        Deserializer.deserializeLevel("") match {
          case Failure(x: IllegalArgumentException) => succeed
          case _                                    => fail("Shoud Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with wrong json format") {
      it("should produce IllegalArgumentException") {
        Deserializer.deserializeLevel("{invalid level}") match {
          case Failure(x: IllegalArgumentException) => succeed
          case _                                    => fail("Shoud Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with nearly correct  json format") {
      it("should produce IllegalArgumentException") {
        Deserializer.deserializeLevel(Source.fromResource("invalid_test_level.json").getLines.mkString) match {
          case Failure(x: IllegalArgumentException) => succeed
          case _                                    => fail("Shoud Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with correct json") {
      it("should produce a Level") {
        val jsonLevel = Source.fromResource("test_level.json").getLines.mkString
        Deserializer.deserializeLevel(jsonLevel).getOrElse(None) shouldBe Level(
          50,
          60,
          Set(
            MoverCell(Position(1, 2), true, Orientation.Right),
            MoverCell(Position(0, 0), false, Orientation.Top),
            GeneratorCell(Position(1, 2), true, Orientation.Right),
            GeneratorCell(Position(0, 0), false, Orientation.Top)
          ),
          PlayableArea(Position(1, 2), 20, 30)
        )
      }
    }
  }
}
