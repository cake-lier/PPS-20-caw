package it.unibo.pps.caw.dsl

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

  def hasPlayableArea(using ops: ListBuffer[Board => Board]): Dimensions => Position => Unit =
    d => p => ops += (_.copy(playableArea = Some(PlayableArea(d)(p))))

  def hasMoverCell(using ops: ListBuffer[Board => Board]): Orientation => Position => Unit =
    o => p => ops += (b => b.copy(moverCells = b.moverCells + OrientedCell(o)(p)))

  def hasGeneratorCell(using ops: ListBuffer[Board => Board]): Orientation => Position => Unit =
    o => p => ops += (b => b.copy(generatorCells = b.generatorCells + OrientedCell(o)(p)))

  def hasRotatorCell(using ops: ListBuffer[Board => Board]): Direction => Position => Unit =
    d => p => ops += (b => b.copy(rotatorCells = b.rotatorCells + DirectedCell(d)(p)))

  def hasBlockCell(using ops: ListBuffer[Board => Board]): MovementDirection => Position => Unit =
    d => p => ops += (b => b.copy(blockCells = b.blockCells + MovableCell(d)(p)))

  def hasEnemyCell(using ops: ListBuffer[Board => Board]): Position => Unit =
    p => ops += (b => b.copy(enemyCells = b.enemyCells + Cell(p)))

  def hasWallCell(using ops: ListBuffer[Board => Board]): Position => Unit =
    p => ops += (b => b.copy(wallCells = b.wallCells + Cell(p)))

  extension (f: Dimensions => Position => Unit) {
    def withDimensions(width: Int, height: Int): Position => Unit = f(Dimensions(width, height))
  }

  extension (f: Orientation => Position => Unit) {
    def facing(orientation: Orientation): Position => Unit = f(orientation)
  }

  extension (f: Direction => Position => Unit) {
    def directed(direction: Direction): Position => Unit = f(direction)
  }

  extension (f: MovementDirection => Position => Unit) {
    def movable(movementDirection: MovementDirection): Position => Unit = f(movementDirection)
  }

  extension (f: Position => Unit) {
    def at(x: Int, y: Int): Unit = f(Position(x, y))
  }
}

import it.unibo.pps.caw.dsl.CellsAtWorkDSL.*
import Orientation.*
import Direction.*
import MovementDirection.*
