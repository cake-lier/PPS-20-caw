package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.*
import it.unibo.pps.caw.dsl.words.{AtWord, FacingWord, InAnAreaWord, PushableWord, RotatingWord}

import scala.collection.mutable.ListBuffer

trait CellsAdders {
  private object CellsDuplicators {
    private def duplicateCells[A <: Cell](cellBuilder: Position => A)(dimensions: Dimensions, position: Position): Iterable[A] =
      for {
        x <- 0 until dimensions.width
        y <- 0 until dimensions.height
      } yield cellBuilder(Position(position.x + x, position.y + y))

    def duplicateOrientableCells(dimensions: Dimensions)(orientation: Orientation)(position: Position): Iterable[OrientableCell] =
      duplicateCells(OrientableCell(orientation))(dimensions, position)

    def duplicateRotatableCells(dimensions: Dimensions)(rotation: Rotation)(position: Position): Iterable[RotatableCell] =
      duplicateCells(RotatableCell(rotation))(dimensions, position)

    def duplicatePushableCells(dimensions: Dimensions)(push: Push)(position: Position): Iterable[PushableCell] =
      duplicateCells(PushableCell(push))(dimensions, position)

    def duplicateBaseCells(dimensions: Dimensions)(position: Position): Iterable[Cell] =
      duplicateCells(Cell.apply)(dimensions, position)
  }

  private object CellsAddersHelpers {
    private def addOrientableCells(
        ops: ListBuffer[Board => Board],
        build: Board => Orientation => Position => Board
    ): FacingWord =
      FacingWord(o => AtWord(p => ops += (b => build(b)(o)(p))))

    def addMoverCells(ops: ListBuffer[Board => Board], build: Orientation => Position => Iterable[OrientableCell]): FacingWord =
      addOrientableCells(ops, b => o => p => b.copy(moverCells = b.moverCells ++ build(o)(p)))

    def addGeneratorCells(
        ops: ListBuffer[Board => Board],
        build: Orientation => Position => Iterable[OrientableCell]
    ): FacingWord =
      addOrientableCells(ops, b => o => p => b.copy(generatorCells = b.generatorCells ++ build(o)(p)))

    private def addRotatableCells(ops: ListBuffer[Board => Board], build: Board => Rotation => Position => Board): RotatingWord =
      RotatingWord(d => AtWord(p => ops += (b => build(b)(d)(p))))

    def addRotatorCells(ops: ListBuffer[Board => Board], build: Rotation => Position => Iterable[RotatableCell]): RotatingWord =
      addRotatableCells(ops, b => d => p => b.copy(rotatorCells = b.rotatorCells ++ build(d)(p)))

    private def addPushableCells(ops: ListBuffer[Board => Board], build: Board => Push => Position => Board): PushableWord =
      PushableWord(m => AtWord(p => ops += (b => build(b)(m)(p))))

    def addBlockCells(ops: ListBuffer[Board => Board], build: Push => Position => Iterable[PushableCell]): PushableWord =
      addPushableCells(ops, b => m => p => b.copy(blockCells = b.blockCells ++ build(m)(p)))

    private def addCells(ops: ListBuffer[Board => Board], build: Board => Position => Board): AtWord =
      AtWord(p => ops += (b => build(b)(p)))

    def addEnemyCells(ops: ListBuffer[Board => Board], build: Position => Iterable[Cell]): AtWord =
      addCells(ops, b => p => b.copy(enemyCells = b.enemyCells ++ build(p)))

    def addWallCells(ops: ListBuffer[Board => Board], build: Position => Iterable[Cell]): AtWord =
      addCells(ops, b => p => b.copy(wallCells = b.wallCells ++ build(p)))
  }

  import CellsAddersHelpers.*
  import CellsDuplicators.*

  def hasMoverCell(using ops: ListBuffer[Board => Board]): FacingWord = addMoverCells(ops, o => p => Set(OrientableCell(o)(p)))

  def hasMoverCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d => addMoverCells(ops, o => p => duplicateOrientableCells(d)(o)(p)))

  def hasGeneratorCell(using ops: ListBuffer[Board => Board]): FacingWord =
    addGeneratorCells(ops, o => p => Set(OrientableCell(o)(p)))

  def hasGeneratorCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d => addGeneratorCells(ops, o => p => duplicateOrientableCells(d)(o)(p)))

  def hasRotatorCell(using ops: ListBuffer[Board => Board]): RotatingWord =
    addRotatorCells(ops, d => p => Set(RotatableCell(d)(p)))

  def hasRotatorCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[RotatingWord] =
    InAnAreaWord(w => addRotatorCells(ops, d => p => duplicateRotatableCells(w)(d)(p)))

  def hasBlockCell(using ops: ListBuffer[Board => Board]): PushableWord =
    addBlockCells(ops, m => p => Set(PushableCell(m)(p)))

  def hasBlockCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[PushableWord] =
    InAnAreaWord(d => addBlockCells(ops, m => p => duplicatePushableCells(d)(m)(p)))

  def hasEnemyCell(using ops: ListBuffer[Board => Board]): AtWord = addEnemyCells(ops, p => Set(Cell(p)))

  def hasEnemyCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[AtWord] =
    InAnAreaWord(d => addEnemyCells(ops, p => duplicateBaseCells(d)(p)))

  def hasWallCell(using ops: ListBuffer[Board => Board]): AtWord =
    addWallCells(ops, p => Set(Cell(p)))

  def hasWallCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[AtWord] =
    InAnAreaWord(d => addWallCells(ops, p => duplicateBaseCells(d)(p)))
}
