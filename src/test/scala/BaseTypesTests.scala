package it.unibo.pps.caw.dsl

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class BaseTypesTests extends AnyFunSpec with Matchers {
  describe("A dimensionable") {
    describe("when first created") {
      it("should return the given width and height") {
        val width: Int = 10
        val height: Int = 20
        val d: Dimensions = Dimensions(width, height)
        d.width shouldBe width
        d.height shouldBe height
      }
    }
  }

  describe("A positionable") {
    describe("when first created") {
      it("should return the given x coordinate and the given y coordinate") {
        val x: Int = 1
        val y: Int = 2
        val p: Position = Position(x, y)
        p.x shouldBe x
        p.y shouldBe y
      }
    }
  }

  describe("A playable area") {
    describe("when first created") {
      it("should return the given position and the given dimensions") {
        val width: Int = 10
        val height: Int = 20
        val x: Int = 1
        val y: Int = 2
        val a: PlayableArea = PlayableArea(width, height)(x, y)
        a.width shouldBe width
        a.height shouldBe height
        a.x shouldBe x
        a.y shouldBe y
      }
    }
  }
}
