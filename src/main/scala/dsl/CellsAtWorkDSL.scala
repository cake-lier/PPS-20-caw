package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.{Board, Dimensions, PlayableArea}
import it.unibo.pps.caw.dsl.words.{AtWord, WithDimensionsWord}

import scala.collection.mutable.ListBuffer

object CellsAtWorkDSL extends CellsAdders {
  def board(fun: ListBuffer[Board => Board] ?=> Unit): Unit = {
    given ops: ListBuffer[Board => Board] = ListBuffer()
    fun
    ops.foldLeft(Board())((b, op) => op(b))
  }

  def withDimensions(width: Int, height: Int)(using ops: ListBuffer[Board => Board]): Unit =
    ops += (_.copy(dimensions = Some(Dimensions(width, height))))

  def hasPlayableArea(using ops: ListBuffer[Board => Board]): WithDimensionsWord =
    WithDimensionsWord(d => AtWord(p => ops += (_.copy(playableArea = Some(PlayableArea(d)(p))))))

  def printIt(using ops: ListBuffer[Board => Board]): Unit =
    ops += (b => { print(b); b })
}
