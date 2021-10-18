package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.common.model.{Dimensions, PlayableArea}
import it.unibo.pps.caw.dsl.entities.LevelBuilderState
import it.unibo.pps.caw.dsl.words.{AtWord, WithDimensionsWord}

import scala.collection.mutable.ListBuffer

/** The Domain Specific Language to be used for creating new levels for the game.
  *
  * This object is the one to be imported fully for using the language specifically designed for creating new levels of the "Cells
  * At Work" game.
  */
object CellsAtWorkDSL extends CellsAdders with LevelDisplayers {

  /** Allows to start writing a new specification for a [[it.unibo.pps.caw.common.model.Level]]. The specification must be written
    * inside the block opened for calling this function.
    *
    * @param fun
    *   the function which will contain the new [[it.unibo.pps.caw.common.model.Level]] definition
    */
  def board(fun: ListBuffer[LevelBuilderState => LevelBuilderState] ?=> Unit): Unit = {
    given ops: ListBuffer[LevelBuilderState => LevelBuilderState] = ListBuffer()
    fun
    ops.foldLeft(LevelBuilderState())((b, op) => op(b))
  }

  /** Allows to specify the dimensions of the [[it.unibo.pps.caw.common.model.Level]] which is currently being defined.
    *
    * @param width
    *   the width of the [[it.unibo.pps.caw.common.model.Level]]
    * @param height
    *   the height of the [[it.unibo.pps.caw.common.model.Level]]
    * @param ops
    *   the list of operations to which add this specific operation
    */
  def withDimensions(width: Int, height: Int)(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): Unit =
    ops += (_.copy(dimensions = Some((width, height))))

  /** Allows to specify the properties of the [[it.unibo.pps.caw.common.model.PlayableArea]] of the
    * [[it.unibo.pps.caw.common.model.Level]] currently being defined. It returns a
    * [[it.unibo.pps.caw.dsl.words.WithDimensionsWord]] which can then be used by the user for specifying the
    * [[it.unibo.pps.caw.common.model.Dimensions]] and the [[it.unibo.pps.caw.common.model.Position]] of the playable area and
    * continuing the sentence.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[it.unibo.pps.caw.dsl.words.WithDimensionsWord]] which can then be used by the user for continuing the sentence
    */
  def hasPlayableArea(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): WithDimensionsWord =
    WithDimensionsWord(d => AtWord(p => ops += (_.copy(playableArea = Some(PlayableArea(d)(p))))))
}
