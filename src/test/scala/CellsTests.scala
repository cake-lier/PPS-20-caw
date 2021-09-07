package it.unibo.pps.caw.dsl

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CellsTests extends AnyFunSpec with Matchers {
  private val x: Int = 1
  private val y: Int = 2

  describe("A generic cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: Cell = Cell(x, y)
        cell.x shouldBe x
        cell.y shouldBe y
      }
    }
  }

  describe("An oriented cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: OrientedCell = OrientedCell(orientation, x, y)
        cell.x shouldBe x
        cell.y shouldBe y
        cell.orientation shouldBe orientation
      }
    }
  }

  describe("A directed cell") {
    describe("when first created") {
      it("should return the given direction and position") {
        val direction: Direction = Direction.Clockwise
        val cell: DirectedCell = DirectedCell(direction, x, y)
        cell.x shouldBe x
        cell.y shouldBe y
        cell.direction shouldBe direction
      }
    }
  }

  describe("A movable cell") {
    describe("when first created") {
      it("should return the given direction of movement and position") {
        val movementDirection: MovementDirection = MovementDirection.Vertical
        val cell: MovableCell = MovableCell(movementDirection, x, y)
        cell.x shouldBe x
        cell.y shouldBe y
        cell.movementDirection shouldBe movementDirection
      }
    }
  }
}
