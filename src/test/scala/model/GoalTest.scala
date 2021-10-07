package it.unibo.pps.caw
package model

import it.unibo.pps.caw.game.model.engine.Goal
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class GoalTest  extends AnyFunSpec with Matchers {
  describe("Goal") {
    describe("when created") {
      it("doesn't change value") {
        Goal("test(X)").value shouldBe "test(X)"
      }
    }
  }
}
