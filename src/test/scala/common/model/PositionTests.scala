package it.unibo.pps.caw
package common.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[Position]] trait. */
class PositionTests extends AnyFunSpec with Matchers {
  describe("A position object") {
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
}
