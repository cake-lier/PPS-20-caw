package it.unibo.pps.caw
package model

import game.model.engine.Clause

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ClauseTest extends AnyFunSpec with Matchers {
  describe("Clause") {
    describe("when created") {
      it("doesn't change value") {
        Clause("test(0).").value shouldBe "test(0)."
      }
    }
  }
}
