package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.common.model.Dimensions

/** The "withDimensions" word, used for specifying the [[it.unibo.pps.caw.common.model.Dimensions]] of an area.
  *
  * This word must appear before an [[AtWord]]. It must be constructed through its companion object.
  */
trait WithDimensionsWord {

  /** Allows to use the "withDimensions" word in a sentence written in the DSL, specifying the
    * [[it.unibo.pps.caw.common.model.Dimensions]] of an area. It returns the [[AtWord]] to use next for completing the sentence.
    *
    * @param width
    *   the width of the area
    * @param height
    *   the height of the area
    * @return
    *   the [[AtWord]] that the user must then use for completing the sentence
    */
  def withDimensions(width: Int, height: Int): AtWord
}

/** Companion object of the [[WithDimensionsWord]] trait, containing its factory method. */
object WithDimensionsWord {

  /* Default implementation of the WithDimensionsWord trait. */
  private class WithDimensionsWordImpl(fun: Dimensions => AtWord) extends WithDimensionsWord {

    /* Calls the given function with the Dimensions constructed through the values given by the user. */
    def withDimensions(width: Int, height: Int): AtWord = fun((width, height))
  }

  /** Returns a new instance of the [[WithDimensionsWord]] trait. It needs a function which can consume the
    * [[it.unibo.pps.caw.common.model.Dimensions]] that the user specifies through the use of this "withDimensions" word and which
    * can return an [[AtWord]] that the user must then use for completing the sentence.
    *
    * @param fun
    *   the function for consuming the [[it.unibo.pps.caw.common.model.Dimensions]] the user specifies through this
    *   "withDimensions" word and which returns the [[AtWord]] to use next in the sentence
    * @return
    *   a new instance of the [[WithDimensionsWord]] trait
    */
  def apply(fun: Dimensions => AtWord): WithDimensionsWord = WithDimensionsWordImpl(fun)
}
