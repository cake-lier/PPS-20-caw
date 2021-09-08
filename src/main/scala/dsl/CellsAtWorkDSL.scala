package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.*
import it.unibo.pps.caw.dsl.words.{AtWord, DirectedWord, FacingWord, MovableWord, WithDimensionsWord}

import scala.collection.mutable.ListBuffer

object CellsAtWorkDSL {
  def board(fun: ListBuffer[Board => Board] ?=> Unit): Unit = {
    given ops: ListBuffer[Board => Board] = ListBuffer()
    fun
    val built: Board = ops.foldLeft(Board())((b, op) => op(b))
    print(built)
  }

  def withDimensions(width: Int, height: Int)(using ops: ListBuffer[Board => Board]): Unit =
    ops += (_.copy(dimensions = Some(Dimensions(width, height))))

  def hasPlayableArea(using ops: ListBuffer[Board => Board]): WithDimensionsWord =
    WithDimensionsWord(d => AtWord(p => ops += (_.copy(playableArea = Some(PlayableArea(d)(p))))))

  def hasMoverCell(using ops: ListBuffer[Board => Board]): FacingWord =
    FacingWord(o => AtWord(p => ops += (b => b.copy(moverCells = b.moverCells + OrientedCell(o)(p)))))

  def hasGeneratorCell(using ops: ListBuffer[Board => Board]): FacingWord =
    FacingWord(o => AtWord(p => ops += (b => b.copy(generatorCells = b.generatorCells + OrientedCell(o)(p)))))

  def hasRotatorCell(using ops: ListBuffer[Board => Board]): DirectedWord =
    DirectedWord(d => AtWord(p => ops += (b => b.copy(rotatorCells = b.rotatorCells + DirectedCell(d)(p)))))

  def hasBlockCell(using ops: ListBuffer[Board => Board]): MovableWord =
    MovableWord(d => AtWord(p => ops += (b => b.copy(blockCells = b.blockCells + MovableCell(d)(p)))))

  def hasEnemyCell(using ops: ListBuffer[Board => Board]): AtWord =
    AtWord(p => ops += (b => b.copy(enemyCells = b.enemyCells + Cell(p))))

  def hasWallCell(using ops: ListBuffer[Board => Board]): AtWord =
    AtWord(p => ops += (b => b.copy(wallCells = b.wallCells + Cell(p))))
}
