package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.dsl.entities.Dimensions

trait WithDimensionsWord {
  def withDimensions(width: Int, height: Int): AtWord
}

object WithDimensionsWord {
  private class WithDimensionsWordImpl(fun: Dimensions => AtWord) extends WithDimensionsWord {
    def withDimensions(width: Int, height: Int): AtWord = fun(Dimensions(width, height))
  }
  
  def apply(fun: Dimensions => AtWord): WithDimensionsWord = WithDimensionsWordImpl(fun)
}
