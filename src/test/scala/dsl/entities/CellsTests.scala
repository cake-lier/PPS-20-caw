package it.unibo.pps.caw.dsl.entities

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[Cell]] trait and all its subtypes. */
class CellsTests extends AnyFunSpec with Matchers {
  private val position: Position = Position(1, 2)

  describe("A generic cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: Cell = Cell(position)
        cell.position shouldBe position
      }
    }
  }

  describe("An oriented cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: OrientableCell = OrientableCell(orientation)(position)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
      }
    }
  }

  describe("A directed cell") {
    describe("when first created") {
      it("should return the given rotation and position") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: RotatableCell = RotatableCell(rotation)(position)
        cell.position shouldBe position
        cell.rotation shouldBe rotation
      }
    }
  }

  describe("A movable cell") {
    describe("when first created") {
      it("should return the given push and position") {
        val push: Push = Push.Vertical
        val cell: PushableCell = PushableCell(push)(position)
        cell.position shouldBe position
        cell.push shouldBe push
      }
    }
  }
}
