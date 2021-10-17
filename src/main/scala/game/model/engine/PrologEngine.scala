package it.unibo.pps.caw.game.model.engine

import alice.tuprolog.*

import scala.util.{Success, Try}

/* A goal to be solved by the [[PrologEngine]]. */
private type Goal = String

/* A clause which is part of a PROLOG theory. */
private type Clause = String

/* Represents a PROLOG engine, an entity capable of solving goals given a theory following the specifications of the PROLOG
 * language.
 *
 * This entity is a wrapper over a PROLOG library capable of evaluating PROLOG code. Being so, it should be initialized with a
 * theory, which will be used for solving every goal submitted to the engine. Every submission will result in a [[Result]] of
 * that resolution. It is assumed that every resolution will have a compound term as a result and not a simple term or just the
 * satisfiability of the given goal. It must be constructed through its companion object.
 */
private trait PrologEngine {

  /* Solves the given [[Goal]] and returns the [[Result]] of the resolution, assuming it is a compound term. */
  def solve(goal: Goal): Result
}

/* The companion object of the trait PrologEngine, containing its factory method. */
private object PrologEngine {

  /* Default implementation of the PrologEngine trait. */
  private case class PrologEngineImpl(clauses: Seq[Clause]) extends PrologEngine {
    private val engine: Prolog = Prolog()

    engine.setTheory(Theory(clauses.mkString("\n")))

    override def solve(goal: Goal): Result = Result(engine.solve(Term.createTerm(goal)).getSolution.toString)
  }

  /* Returns a new instance of [[PrologEngine]] given the clauses which are part of the PROLOG theory to put into the engine. */
  def apply(clauses: Clause*): PrologEngine = PrologEngineImpl(clauses)
}
