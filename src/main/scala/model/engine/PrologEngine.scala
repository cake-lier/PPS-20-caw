package engine
import alice.tuprolog.*

import scala.util.{Success, Try}
import scala.language.implicitConversions
import scala.Int

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

/** Object that represent a prolog game engine */
object PrologEngine {

  /** extract a [[Term]] from a [[Term]]
    * @param t:
    *   the all [[Term]]
    * @param i:
    *   the index of the [[Term]] to be extracted
    * @return
    *   the i-th [[Term]] of the given [[Term]]
    */
  def extractTerm(t: Term, i: Int): Term = t.asInstanceOf[Struct].getArg(i).getTerm

  /** implicit conversion from [[String]] to [[Term]]
    * @return
    *   the converted [[Term]]
    */
  given Conversion[String, Term] = Term.createTerm(_)

  /** implicit conversion from [[Seq]] to [[Term]]
    * @return
    *   the builted [[Term]]
    */
  given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")

  /** create a function that map from [[Term]] to goal
    * @param clauses:
    *   list of [[Clause]] s, the [[Theory]]
    * @return
    *   a function [[Term]] to [[Term]] that is the result of goal
    */
  def apply(clauses: Clause*): Term => Term = goal => {
    val engine: Prolog = Prolog()
    engine.setTheory(Theory(clauses map (_.value) mkString (" ")))
    engine.solve(goal).getSolution
  }
}
