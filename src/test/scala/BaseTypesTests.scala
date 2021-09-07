package it.unibo.pps.caw.dsl

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class BaseTypesTests extends AnyFunSpec with Matchers {
  describe("A dimensionable") {
    describe("when first created") {
      it("should not have neither a width nor an height") {
        val d: Dimensionable = Dimensionable()
        d.width shouldBe empty
        d.height shouldBe empty
      }
    }

    describe("when set with a width and an height") {
      it("should return the given width and height") {
        val width: Int = 10
        val height: Int = 20
        val d: Dimensionable = Dimensionable(width, height)
        d.width should contain(width)
        d.height should contain(height)
      }
    }
  }

  describe("A positionable") {
    describe("when first created") {
      it("should not have neither an x coordinate nor a y coordinate") {
        val p: Positionable = Positionable()
        p.x shouldBe empty
        p.y shouldBe empty
      }
    }

    describe("when set with an x coordinate and a y coordinate") {
      it("should return the given x coordinate and the given y coordinate") {
        val x: Int = 1
        val y: Int = 2
        val p: Positionable = Positionable(x, y)
        p.x should contain(x)
        p.y should contain(y)
      }
    }
  }
}
