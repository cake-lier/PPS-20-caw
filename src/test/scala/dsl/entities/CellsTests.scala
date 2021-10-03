package it.unibo.pps.caw.dsl.entities

import it.unibo.pps.caw.common.model.Position
import it.unibo.pps.caw.common.model.cell.*
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[Cell]] trait and all its subtypes. */
class CellsTests extends AnyFunSpec with Matchers {
  private val position: Position = Position(1, 2)

  describe("An enemy cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: BaseEnemyCell = BaseEnemyCell(position)
        cell.position shouldBe position
      }
    }
  }

  describe("A wall cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: BaseWallCell = BaseWallCell(position)
        cell.position shouldBe position
      }
    }
  }

  describe("A mover cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: BaseMoverCell = BaseMoverCell(orientation)(position)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
      }
    }
  }

  describe("A generator cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: BaseGeneratorCell = BaseGeneratorCell(orientation)(position)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
      }
    }
  }

  describe("A rotation cell") {
    describe("when first created") {
      it("should return the given rotation and position") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: BaseRotatorCell = BaseRotatorCell(rotation)(position)
        cell.position shouldBe position
        cell.rotation shouldBe rotation
      }
    }
  }

  describe("A block cell") {
    describe("when first created") {
      it("should return the given push and position") {
        val push: Push = Push.Vertical
        val cell: BaseBlockCell = BaseBlockCell(push)(position)
        cell.position shouldBe position
        cell.push shouldBe push
      }
    }
  }
}
