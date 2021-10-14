package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.common.model.Dimensions

/** The "inAnArea" word, used for specifying the [[Dimensions]] of an area of the [[it.unibo.pps.caw.dsl.entities.Board]] which is
  * populated with the same type of cells.
  *
  * This word must appear before any word that can follow the "inAnArea" word, hence any [[FollowingInAnAreaWord]] words. It must
  * be constructed through its companion object.
  * @tparam A
  *   the type of [[FollowingInAnAreaWord]] which should follow this [[InAnAreaWord]] in a DSL sentence
  */
trait InAnAreaWord[A <: FollowingInAnAreaWord] {

  /** Allows to use the "inAnArea" word in a sentence written in the DSL, specifying the [[Dimensions]] of an area of the
    * [[it.unibo.pps.caw.dsl.entities.Board]] which is populated with the same type of cells. It returns the
    * [[FollowingInAnAreaWord]] to use next for continuing the sentence.
    *
    * @param width
    *   the width of the area which is populated with the same type of cells
    * @param height
    *   the height of the area which is populated with the same type of cells
    * @return
    *   the [[FollowingInAnAreaWord]] that the user must then use for continuing the sentence
    */
  def inAnArea(width: Int, height: Int): A
}

/** Companion object of the [[InAnAreaWord]] trait, containing its factory method. */
object InAnAreaWord {

  /* Default implementation of the InAnAreaWord trait. */
  private class InAnAreaWordImpl[A <: FollowingInAnAreaWord](fun: Dimensions => A) extends InAnAreaWord[A] {

    /* Calls the given function with the Dimensions constructed through the values given by the user. */
    def inAnArea(width: Int, height: Int): A = fun((width, height))
  }

  /** Returns a new instance of the [[InAnAreaWord]] trait. It needs a function which can consume the [[Dimensions]] that the user
    * specifies through the use of this "inAnArea" word and which can return a [[FollowingInAnAreaWord]] that the user must then
    * use for continuing the sentence.
    *
    * @param fun
    *   the function for consuming the [[Dimensions]] the user specifies through this "inAnArea" word and which returns the
    *   [[FollowingInAnAreaWord]] to use next in the sentence
    * @tparam A
    *   the specific type of [[FollowingInAnAreaWord]] that the function must return
    * @return
    *   a new instance of the [[InAnAreaWord]] trait
    */
  def apply[A <: FollowingInAnAreaWord](fun: Dimensions => A): InAnAreaWord[A] = InAnAreaWordImpl(fun)
}
