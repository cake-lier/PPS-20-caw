package it.unibo.pps.caw.game.model.engine

import alice.tuprolog.*

import scala.util.{Success, Try}

/** Represent a prolog clause */
sealed trait Clause {

  /** [[Clause]] as [[String]] */
  val value: String
}

/** a companion object of trait [[Clause]] */
object Clause {
  private case class ClauseImpl(value: String) extends Clause
  def apply(value: String): Clause = ClauseImpl(value)
}

sealed trait Result {
  /** [[Result]] as [[String]] */
  val value: String
  def getLastTerm: String
}
object Result {
  private case class ResultImpl(value: String) extends Result {
    override def getLastTerm: String = {
      val term = Term.createTerm(value).asInstanceOf[Struct]
      term.getArg(term.getArity - 1).getTerm.toString
    }
  }
  def apply(stringResult: String): Result = ResultImpl(stringResult)
}

sealed trait Goal {
  val stringGoal: String
}
object Goal {
  private case class GoalImpl(stringGoal: String) extends Goal
  def apply(stringGoal: String): Goal = GoalImpl(stringGoal)
}
sealed trait PrologEngine {
  def solve(goal: Goal): Result
}

/** Object that represent a prolog game engine */
object PrologEngine {
  private case class PrologEngineImpl(clauses: Clause*) extends PrologEngine {
    val engine: Prolog = Prolog()
    engine.setTheory(Theory(clauses map (_.value) mkString (" ")))

    /** create a function that map from [[Term]] to goal
      * @param clauses:
      *   list of [[Clause]] state, the [[Theory]]
      */
    override def solve(goal: Goal): Result =
      Result(engine.solve(Term.createTerm(goal.stringGoal)).getSolution.toString)
  }

  def apply(clause: Clause): PrologEngine = PrologEngineImpl(clause)
}
