package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.dsl.entities.Orientation

trait FacingWord {
  def facing(orientation: Orientation): AtWord
}

object FacingWord {
  private class FacingWordImpl(fun: Orientation => AtWord) extends FacingWord {
    def facing(orientation: Orientation): AtWord = fun(orientation)
  }
  
  def apply(fun: Orientation => AtWord): FacingWord = FacingWordImpl(fun)
}