package it.unibo.pps.caw.model

import it.unibo.pps.caw.common.{PlayableArea, Board}
import it.unibo.pps.caw.game.controller.Deserializer
import it.unibo.pps.caw.game.model.*
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import scala.util.{Failure, Success}

class DeserializerTest extends AnyFunSpec with Matchers {
  describe("A JSON") {
    describe("when empty") {
      it("should produce IllegalArgumentException") {
        Deserializer.deserializeLevel("") match {
          case Failure(x: IllegalArgumentException) => succeed
          case _                                    => fail("Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with wrong json format") {
      it("should produce IllegalArgumentException") {
        Deserializer.deserializeLevel("{invalid level}") match {
          case Failure(x: IllegalArgumentException) => succeed
          case _                                    => fail("Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with nearly correct  json format") {
      it("should produce IllegalArgumentException") {
        Deserializer.deserializeLevel(Source.fromResource("invalid_test_level.json").getLines.mkString) match {
          case Failure(x: IllegalArgumentException) => succeed
          case _                                    => fail("Left should be IllegalArgumentException")
        }
      }
    }
    describe("when with correct json") {
      it("should produce a Level") {
        val jsonLevel = Source.fromResource("test_level.json").getLines.mkString
        Deserializer.deserializeLevel(jsonLevel).getOrElse(None) shouldBe Level(
          (50, 60),
          Board(
            SetupMoverCell((1, 2), Orientation.Right, true),
            SetupMoverCell((0, 0), Orientation.Top, false),
            SetupGeneratorCell((1, 2), Orientation.Right, true),
            SetupGeneratorCell((0, 0), Orientation.Top, false)
          ),
          PlayableArea((1, 2), (20, 30))
        )
      }
    }
  }
}
