package it.unibo.pps.caw.game.model.engine

import alice.tuprolog.{Struct, Term}

/* The result of the resolution of a PROLOG goal.
 *
 * It is assumed that the result is made by a compound term, so no simple terms or other types of results will be accepted by
 * this entity. It must be constructed through its companion object.
 */
private trait Result {

  /* Returns this Result in a String form. */
  val value: String

  /* Returns the last term of this Result in a String form. */
  def extractLastTerm: String
}

/* The companion object of the trait Result. */
private object Result {

  /* Default implementation of Result trait. */
  private case class ResultImpl(value: String) extends Result {
    private val term: Struct = Term.createTerm(value).asInstanceOf[Struct]

    override def extractLastTerm: String = term.getArg(term.getArity - 1).getTerm.toString
  }

  /* Returns a new instance of the Result trait given itself in String form. */
  def apply(result: String): Result = ResultImpl(result)
}
