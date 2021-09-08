package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.dsl.entities.MovementDirection

trait MovableWord {
  def movable(movementDirection: MovementDirection): AtWord
}

object MovableWord {
  private class MovableWordImpl(fun: MovementDirection => AtWord) extends MovableWord {
    def movable(movementDirection: MovementDirection): AtWord = fun(movementDirection)
  }
  
  def apply(fun: MovementDirection => AtWord): MovableWord = MovableWordImpl(fun)
}
