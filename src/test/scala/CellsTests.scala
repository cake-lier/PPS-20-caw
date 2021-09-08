package it.unibo.pps.caw.dsl

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

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
        val cell: OrientedCell = OrientedCell(orientation)(position)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
      }
    }
  }

  describe("A directed cell") {
    describe("when first created") {
      it("should return the given direction and position") {
        val direction: Direction = Direction.Clockwise
        val cell: DirectedCell = DirectedCell(direction)(position)
        cell.position shouldBe position
        cell.direction shouldBe direction
      }
    }
  }

  describe("A movable cell") {
    describe("when first created") {
      it("should return the given direction of movement and position") {
        val movementDirection: MovementDirection = MovementDirection.Vertical
        val cell: MovableCell = MovableCell(movementDirection)(position)
        cell.position shouldBe position
        cell.movementDirection shouldBe movementDirection
      }
    }
  }
}
