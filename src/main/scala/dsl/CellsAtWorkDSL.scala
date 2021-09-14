package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.{BoardBuilder, Dimensions, PlayableArea}
import it.unibo.pps.caw.dsl.words.{AtWord, WithDimensionsWord}

import scala.collection.mutable.ListBuffer

/** The Domain Specific Language to be used for creating new levels for the game.
  *
  * This object is the one to be imported fully for using the language specifically designed for creating new levels of the "Cells
  * At Work" game.
  */
object CellsAtWorkDSL extends CellsAdders with BoardDisplayers {

  /** Allows to start writing a new specification for a [[Board]]. The specification must be written inside the block opened for
    * calling this function.
    *
    * @param fun
    *   the function which will contain the new [[Board]] definition
    */
  def board(fun: ListBuffer[BoardBuilder => BoardBuilder] ?=> Unit): Unit = {
    given ops: ListBuffer[BoardBuilder => BoardBuilder] = ListBuffer()
    fun
    ops.foldLeft(BoardBuilder())((b, op) => op(b))
  }

  /** Allows to specify the dimensions of the [[Board]] which is currently being defined.
    *
    * @param width
    *   the width of the [[Board]]
    * @param height
    *   the height of the [[Board]]
    * @param ops
    *   the list of operations to which add this specific operation
    */
  def withDimensions(width: Int, height: Int)(using ops: ListBuffer[BoardBuilder => BoardBuilder]): Unit =
    ops += (_.copy(dimensions = Some(Dimensions(width, height))))

  /** Allows to specify the properties of the [[PlayableArea]] of the [[Board]] currently being defined. It returns a
    * [[WithDimensionsWord]] which can then be used by the user for specifying the [[Dimensions]] and the [[Position]] of the
    * [[PlayableArea]] and continuing the sentence.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[WithDimensionsWord]] which can then be used by the user for continuing the sentence
    */
  def hasPlayableArea(using ops: ListBuffer[BoardBuilder => BoardBuilder]): WithDimensionsWord =
    WithDimensionsWord(d => AtWord(p => ops += (_.copy(playableArea = Some(PlayableArea(d)(p))))))
}
