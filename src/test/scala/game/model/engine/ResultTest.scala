package it.unibo.pps.caw
package game.model.engine

import alice.tuprolog.InvalidTermException
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for class [[Result]] */
class ResultTest extends AnyFunSpec with Matchers {
  describe("Result") {
    describe("when created") {
      it("doesn't change value") {
        Result("test(0)").value shouldBe "test(0)"
      }
      it("can return only last term") {
        Result("test(1,2)").extractLastTerm shouldBe "2"
      }
      describe("when extractLastTerm is called and the result is in invalid format") {
        it("throw InvaliTermException") {
          the[InvalidTermException] thrownBy Result("test()").extractLastTerm
        }
      }
    }
  }
}
