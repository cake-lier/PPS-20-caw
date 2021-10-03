package it.unibo.pps.caw.common.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[Dimensions]] trait. */
class DimensionsTests extends AnyFunSpec with Matchers {
  describe("A dimensions object") {
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
}
