package it.unibo.pps.caw.game.model.engine

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for class [[Goal]] */
class GoalTest  extends AnyFunSpec with Matchers {
  describe("Goal") {
    describe("when created") {
      it("doesn't change value") {
        Goal("test(X)").value shouldBe "test(X)"
      }
    }
  }
}
