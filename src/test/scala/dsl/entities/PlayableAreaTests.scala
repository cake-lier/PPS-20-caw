package it.unibo.pps.caw.dsl.entities

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[PlayableArea]] trait. */
class PlayableAreaTests extends AnyFunSpec with Matchers {
  describe("A playable area") {
    describe("when first created") {
      it("should return the given position and the given dimensions") {
        val dimensions: Dimensions = Dimensions(10, 20)
        val position: Position = Position(1, 2)
        val area: PlayableArea = PlayableArea(dimensions)(position)
        area.dimensions shouldBe dimensions
        area.position shouldBe position
      }
    }
  }
}
