package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.*
import it.unibo.pps.caw.dsl.words.{AtWord, FacingWord, InAnAreaWord, PushableWord, RotatingWord}

import scala.collection.mutable.ListBuffer

trait CellsAdders {
  sealed trait OrientationWord(val orientation: Orientation)
  case object left extends OrientationWord(Orientation.Left)
  case object right extends OrientationWord(Orientation.Right)
  case object top extends OrientationWord(Orientation.Top)
  case object down extends OrientationWord(Orientation.Down)

  sealed trait RotationWord(val rotation: Rotation)
  case object clockwise extends RotationWord(Rotation.Clockwise)
  case object counterclockwise extends RotationWord(Rotation.Counterclockwise)

  sealed trait PushWord(val push: Push)
  case object vertically extends PushWord(Push.Vertical)
  case object horizontally extends PushWord(Push.Horizontal)
  case object inBothDirections extends PushWord(Push.Both)

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
        ops: ListBuffer[BoardBuilder => BoardBuilder],
        build: BoardBuilder => Orientation => Position => BoardBuilder
    ): FacingWord =
      FacingWord(o => AtWord(p => ops += (b => build(b)(o)(p))))

    def addMoverCells(
        ops: ListBuffer[BoardBuilder => BoardBuilder],
        build: Orientation => Position => Iterable[OrientableCell]
    ): FacingWord =
      addOrientableCells(ops, b => o => p => b.copy(moverCells = b.moverCells ++ build(o)(p)))

    def addGeneratorCells(
        ops: ListBuffer[BoardBuilder => BoardBuilder],
        build: Orientation => Position => Iterable[OrientableCell]
    ): FacingWord =
      addOrientableCells(ops, b => o => p => b.copy(generatorCells = b.generatorCells ++ build(o)(p)))

    private def addRotatableCells(
        ops: ListBuffer[BoardBuilder => BoardBuilder],
        build: BoardBuilder => Rotation => Position => BoardBuilder
    ): RotatingWord =
      RotatingWord(d => AtWord(p => ops += (b => build(b)(d)(p))))

    def addRotatorCells(
        ops: ListBuffer[BoardBuilder => BoardBuilder],
        build: Rotation => Position => Iterable[RotatableCell]
    ): RotatingWord =
      addRotatableCells(ops, b => d => p => b.copy(rotatorCells = b.rotatorCells ++ build(d)(p)))

    private def addPushableCells(
        ops: ListBuffer[BoardBuilder => BoardBuilder],
        build: BoardBuilder => Push => Position => BoardBuilder
    ): PushableWord =
      PushableWord(m => AtWord(p => ops += (b => build(b)(m)(p))))

    def addBlockCells(
        ops: ListBuffer[BoardBuilder => BoardBuilder],
        build: Push => Position => Iterable[PushableCell]
    ): PushableWord =
      addPushableCells(ops, b => m => p => b.copy(blockCells = b.blockCells ++ build(m)(p)))

    private def addCells(ops: ListBuffer[BoardBuilder => BoardBuilder], build: BoardBuilder => Position => BoardBuilder): AtWord =
      AtWord(p => ops += (b => build(b)(p)))

    def addEnemyCells(ops: ListBuffer[BoardBuilder => BoardBuilder], build: Position => Iterable[Cell]): AtWord =
      addCells(ops, b => p => b.copy(enemyCells = b.enemyCells ++ build(p)))

    def addWallCells(ops: ListBuffer[BoardBuilder => BoardBuilder], build: Position => Iterable[Cell]): AtWord =
      addCells(ops, b => p => b.copy(wallCells = b.wallCells ++ build(p)))
  }

  import CellsAddersHelpers.*
  import CellsDuplicators.*

  def hasMoverCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): FacingWord =
    addMoverCells(ops, o => p => Set(OrientableCell(o)(p)))

  def hasMoverCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d => addMoverCells(ops, o => p => duplicateOrientableCells(d)(o)(p)))

  def hasGeneratorCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): FacingWord =
    addGeneratorCells(ops, o => p => Set(OrientableCell(o)(p)))

  def hasGeneratorCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d => addGeneratorCells(ops, o => p => duplicateOrientableCells(d)(o)(p)))

  def hasRotatorCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): RotatingWord =
    addRotatorCells(ops, d => p => Set(RotatableCell(d)(p)))

  def hasRotatorCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[RotatingWord] =
    InAnAreaWord(w => addRotatorCells(ops, d => p => duplicateRotatableCells(w)(d)(p)))

  def hasBlockCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): PushableWord =
    addBlockCells(ops, m => p => Set(PushableCell(m)(p)))

  def hasBlockCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[PushableWord] =
    InAnAreaWord(d => addBlockCells(ops, m => p => duplicatePushableCells(d)(m)(p)))

  def hasEnemyCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): AtWord = addEnemyCells(ops, p => Set(Cell(p)))

  def hasEnemyCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[AtWord] =
    InAnAreaWord(d => addEnemyCells(ops, p => duplicateBaseCells(d)(p)))

  def hasWallCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): AtWord =
    addWallCells(ops, p => Set(Cell(p)))

  def hasWallCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[AtWord] =
    InAnAreaWord(d => addWallCells(ops, p => duplicateBaseCells(d)(p)))
}
