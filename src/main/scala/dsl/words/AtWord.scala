package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.dsl.entities.Position

trait AtWord {
  def at(x: Int, y: Int): Unit
}

object AtWord {
  private class AtWordImpl(fun: Position => Unit) extends AtWord {
    def at(x: Int, y: Int): Unit = fun(Position(x, y))
  }
  
  def apply(fun: Position => Unit): AtWord = AtWordImpl(fun)
}
