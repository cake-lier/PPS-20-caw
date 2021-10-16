package it.unibo.pps.caw.game.model.engine

import alice.tuprolog.*

import scala.util.{Success, Try}

/** Represents a Prolog clause. */
sealed trait Clause {

  /** The [[Clause]] as [[String]] */
  val value: String
}

/** The companion object of the trait [[Clause]]. */
object Clause {
  private case class ClauseImpl(value: String) extends Clause

  /** Returns an instance of [[Clause]].
    * @param value
    *   the value of the [[Clause]]
    * @return
    *   a new instance of [[Clause]]
    */
  def apply(value: String): Clause = ClauseImpl(value)
}

/** Represents the result of a Prolog goal. */
sealed trait Result {

  /** The [[Result]] as [[String]]. */
  val value: String

  /** Returns the last term of a compound term.
    * @return
    *   the last term as [[String]]
    */
  def getLastTerm: String
}

/** The companion object of the trait [[Result]]. */
object Result {
  private case class ResultImpl(value: String) extends Result {
    override def getLastTerm: String = {
      val term = Term.createTerm(value).asInstanceOf[Struct]
      term.getArg(term.getArity - 1).getTerm.toString
    }
  }

  /** Returns an instance of [[Result]].
    * @param stringResult
    *   the result of a Prolog goal as [[String]]
    * @return
    *   a new instance of [[Result]]
    */
  def apply(stringResult: String): Result = ResultImpl(stringResult)
}

/** Represents a Prolog goal. */
sealed trait Goal {

  /** [[Goal]] as [[String]] */
  val value: String
}

/** The companion object of the trait [[Goal]]. */
object Goal {
  private case class GoalImpl(value: String) extends Goal

  /** Returns a new instance of [[Goal]].
    * @param stringGoal
    *   the Prolog goal as [[String]]
    * @return
    *   a new instance of [[Goal]]
    */
  def apply(stringGoal: String): Goal = GoalImpl(stringGoal)
}

/** Represents the Prolog engine of the game.
  *
  * It contains a Prolog theory, the collection of clauses needed to solve any [[Goal]] this engine may receive.
  */
sealed trait PrologEngine {

  /** Solves the given [[Goal]] and returns its [[Result]].
    * @param goal
    *   the given [[Goal]] to be solved
    * @return
    *   a new instance of [[Result]]
    */
  def solve(goal: Goal): Result
}

/** The companion object of the trait [[PrologEngine]]. */
object PrologEngine {
  private case class PrologEngineImpl(clauses: Clause*) extends PrologEngine {
    val engine: Prolog = Prolog()
    engine.setTheory(Theory(clauses map (_.value) mkString (" ")))

    override def solve(goal: Goal): Result =
      Result(engine.solve(Term.createTerm(goal.value)).getSolution.toString)
  }

  /** Returns a new instance of [[PrologEngine]].
    * @param clause
    *   the
    */
  def apply(clause: Clause): PrologEngine = PrologEngineImpl(clause)
}
