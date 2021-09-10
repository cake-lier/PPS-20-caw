package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.{BoardBuilder, Dimensions, PlayableArea}
import it.unibo.pps.caw.dsl.words.{AtWord, WithDimensionsWord}

import scala.collection.mutable.ListBuffer

object CellsAtWorkDSL extends CellsAdders {
  def board(fun: ListBuffer[BoardBuilder => BoardBuilder] ?=> Unit): Unit = {
    given ops: ListBuffer[BoardBuilder => BoardBuilder] = ListBuffer()
    fun
    ops.foldLeft(BoardBuilder())((b, op) => op(b))
  }

  def withDimensions(width: Int, height: Int)(using ops: ListBuffer[BoardBuilder => BoardBuilder]): Unit =
    ops += (_.copy(dimensions = Some(Dimensions(width, height))))

  def hasPlayableArea(using ops: ListBuffer[BoardBuilder => BoardBuilder]): WithDimensionsWord =
    WithDimensionsWord(d => AtWord(p => ops += (_.copy(playableArea = Some(PlayableArea(d)(p))))))

  import it.unibo.pps.caw.dsl.errors.ErrorChecker.checkBoard

  def printIt(using ops: ListBuffer[BoardBuilder => BoardBuilder]): Unit =
    ops += (b => {
      checkBoard(b) match {
        case Right(v) => print(v)
        case Left(e)  => Console.err.print(e.message)
      }
      b
    })
}
