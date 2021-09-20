package it.unibo.pps.caw
import it.unibo.pps.caw.game.controller.Deserializer
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.caw.game.model.*

import scala.io.Source
import scala.util.Success

class DeserializerTest extends AnyFunSpec with Matchers {
//  describe("A JSON") {
//    describe("when empty") {
//      it("should produce IllegalArgumentException") {
//        Deserializer.deserializeLevel("") match {
//          case Left(x: IllegalArgumentException) => succeed
//          case _                                 => fail("Left should be IllegalArgumentException")
//        }
//      }
//    }
//    describe("when with wrong json format") {
//      it("should produce IllegalArgumentException") {
//        Deserializer.deserializeLevel("{invalid level}") match {
//          case Left(x: IllegalArgumentException) => succeed
//          case _                                 => fail("Left should be IllegalArgumentException")
//        }
//      }
//    }
//    describe("when with nearly correct  json format") {
//      it("should produce IllegalArgumentException") {
//        Deserializer.deserializeLevel(Source.fromResource("invalid_test_level.json").getLines.mkString) match {
//          case Left(x: IllegalArgumentException) => succeed
//          case _                                 => fail("Left should be IllegalArgumentException")
//        }
//      }
//    }
//    describe("when with correct json") {
//      it("should produce a Level") {
//        val jsonLevel = Source.fromResource("test_level.json").getLines.mkString
//        Deserializer.deserializeLevel(jsonLevel).getOrElse(None) shouldBe Level(
//          50,
//          60,
//          Board(
//            Set(
//              SetupMoverCell(Position(1, 2), Orientation.Right, true),
//              SetupMoverCell(Position(0, 0), Orientation.Top, false),
//              SetupGeneratorCell(Position(1, 2), Orientation.Right, true),
//              SetupGeneratorCell(Position(0, 0), Orientation.Top, false)
//            )
//          ),
//          PlayableArea(Position(1, 2), 20, 30)
//        )
//      }
//    }
//  }
}
