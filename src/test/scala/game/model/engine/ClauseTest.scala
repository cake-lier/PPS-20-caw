package it.unibo.pps.caw.game.model.engine

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for class [[Clause]] */
class ClauseTest extends AnyFunSpec with Matchers {
  describe("Clause") {
    describe("when created") {
      it("doesn't change value") {
        Clause("test(0).").value shouldBe "test(0)."
      }
    }
  }
}
