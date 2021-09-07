package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.Dimensionable
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class BaseTypesTests extends AnyFunSpec with Matchers {
  describe("A dimensionable") {
    describe("when first created") {
      it("should have neither a width nor an height") {
        val d: Dimensionable = Dimensionable()
        d.width should be(None)
        d.height should be(None)
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
}
