package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.dsl.entities.Direction

trait DirectedWord {
  def directed(direction: Direction): AtWord
}

object DirectedWord {
  private class DirectedWordImpl(fun: Direction => AtWord) extends DirectedWord {
    def directed(direction: Direction): AtWord = fun(direction)
  }
  
  def apply(fun: Direction => AtWord): DirectedWord = DirectedWordImpl(fun)
}
