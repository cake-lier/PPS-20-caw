package it.unibo.pps.caw.game.model.engine

import alice.tuprolog.{InvalidTermException, InvalidTheoryException, NoSolutionException}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for [[PrologEngine]] */
class PrologEngineTest extends AnyFunSpec with Matchers {
  private val prologEngine: PrologEngine = PrologEngine("tuple(45).")
  private val goal: Goal = "tuple(X)"
  private val invalidGoal: Goal = "t(X)"

  describe("PrologEngine") {
    describe("when a bad written Clause is passed") {
      it("should throw InvalidTheoryException") {
        the[InvalidTheoryException] thrownBy PrologEngine("wrong")
      }
    }
    describe("with correct goal") {
      it("should give expected result as Result") {
        prologEngine.solve(goal) shouldBe Result("tuple(45)")
      }
      it("should give expected result as lastTerm") {
        prologEngine.solve(goal).extractLastTerm shouldBe "45"
      }
    }
    describe("with incorrect goal") {
      it("should throw NoSolutionException") {
        the[NoSolutionException] thrownBy prologEngine.solve(invalidGoal)
      }
    }
  }
}
