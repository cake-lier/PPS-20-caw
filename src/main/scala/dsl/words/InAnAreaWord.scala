package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.dsl.entities.Dimensions

trait InAnAreaWord[A <: FollowingInAnAreaWord] {
  def inAnArea(width: Int, height: Int): A
}

object InAnAreaWord {
  private class InAnAreaWordImpl[A <: FollowingInAnAreaWord](fun: Dimensions => A) extends InAnAreaWord[A] {
    def inAnArea(width: Int, height: Int): A = fun(Dimensions(width, height))
  }

  def apply[A <: FollowingInAnAreaWord](fun: Dimensions => A): InAnAreaWord[A] = InAnAreaWordImpl(fun)
}
