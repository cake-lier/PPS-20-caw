package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.dsl.entities.Dimensions

trait InAnAreaWord[A] {
  def inAnArea(width: Int, height: Int): A
}

object InAnAreaWord {
  private class InAnAreaWordImpl[A](fun: Dimensions => A) extends InAnAreaWord[A] {
    def inAnArea(width: Int, height: Int): A = fun(Dimensions(width, height))
  }

  def apply[A](fun: Dimensions => A): InAnAreaWord[A] = InAnAreaWordImpl(fun)
}
