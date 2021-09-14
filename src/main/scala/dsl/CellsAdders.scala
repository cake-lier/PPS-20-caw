package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.*
import it.unibo.pps.caw.dsl.words.{AtWord, FacingWord, InAnAreaWord, PushableWord, RotatingWord}

import scala.collection.mutable.ListBuffer

/** Adds all methods to the DSL that are able to addi new cells to a [[BoardBuilder]].
  *
  * The methods in this module can add a single cell or an area of cells of the same type, for each type of cells in the game.
  */
trait CellsAdders extends PropertiesWord {

  /* Contains helper methods for creating multiple cells at once. */
  private object CellsDuplicators {

    /* Allows to duplicate any cell given a builder for the cell and the Dimensions and the Position of the upper left corner
     * of the area in which place the duplicated cells.
     */
    private def duplicateCells[A <: Cell](cellBuilder: Position => A)(dimensions: Dimensions, position: Position): Iterable[A] =
      for {
        x <- 0 until dimensions.width
        y <- 0 until dimensions.height
      } yield cellBuilder(Position(position.x + x, position.y + y))

    /* Allows to duplicate OrientableCells given a builder for the cell and the Dimensions and the Position of the upper left
     * corner of the area in which place the duplicated cells.
     */
    def duplicateOrientableCells(dimensions: Dimensions)(orientation: Orientation)(position: Position): Iterable[OrientableCell] =
      duplicateCells(OrientableCell(orientation))(dimensions, position)

    /* Allows to duplicate RotatableCells given a builder for the cell and the Dimensions and the Position of the upper left
     * corner of the area in which place the duplicated cells.
     */
    def duplicateRotatableCells(dimensions: Dimensions)(rotation: Rotation)(position: Position): Iterable[RotatableCell] =
      duplicateCells(RotatableCell(rotation))(dimensions, position)

    /* Allows to duplicate PushableCells given a builder for the cell and the Dimensions and the Position of the upper left
     * corner of the area in which place the duplicated cells.
     */
    def duplicatePushableCells(dimensions: Dimensions)(push: Push)(position: Position): Iterable[PushableCell] =
      duplicateCells(PushableCell(push))(dimensions, position)

    /* Allows to duplicate Cells given a builder for the cell and the Dimensions and the Position of the upper left
     * corner of the area in which place the duplicated cells.
     */
    def duplicateBaseCells(dimensions: Dimensions)(position: Position): Iterable[Cell] =
      duplicateCells(Cell.apply)(dimensions, position)
  }

  /* Contains helper methods for appending cell adding operations to the sequence of operations the user specifies. */
  private object CellsAddersHelpers {

    /* Append an OrientableCell adding operation to the sequence of operations specified by the user. */
    private def addOrientableCells(
      ops: ListBuffer[BoardBuilder => BoardBuilder],
      build: BoardBuilder => Orientation => Position => BoardBuilder
    ): FacingWord =
      FacingWord(o => AtWord(p => ops += (b => build(b)(o)(p))))

    /* Append a "mover" cell adding operation to the sequence of operations specified by the user. */
    def addMoverCells(
      ops: ListBuffer[BoardBuilder => BoardBuilder],
      build: Orientation => Position => Iterable[OrientableCell]
    ): FacingWord =
      addOrientableCells(ops, b => o => p => b.copy(moverCells = b.moverCells ++ build(o)(p)))

    /* Append a "generator" cell adding operation to the sequence of operations specified by the user. */
    def addGeneratorCells(
      ops: ListBuffer[BoardBuilder => BoardBuilder],
      build: Orientation => Position => Iterable[OrientableCell]
    ): FacingWord =
      addOrientableCells(ops, b => o => p => b.copy(generatorCells = b.generatorCells ++ build(o)(p)))

    /* Append a RotatableCell adding operation to the sequence of operations specified by the user. */
    private def addRotatableCells(
      ops: ListBuffer[BoardBuilder => BoardBuilder],
      build: BoardBuilder => Rotation => Position => BoardBuilder
    ): RotatingWord =
      RotatingWord(d => AtWord(p => ops += (b => build(b)(d)(p))))

    /* Append a "rotator" cell adding operation to the sequence of operations specified by the user. */
    def addRotatorCells(
      ops: ListBuffer[BoardBuilder => BoardBuilder],
      build: Rotation => Position => Iterable[RotatableCell]
    ): RotatingWord =
      addRotatableCells(ops, b => d => p => b.copy(rotatorCells = b.rotatorCells ++ build(d)(p)))

    /* Append a PushableCell adding operation to the sequence of operations specified by the user. */
    private def addPushableCells(
      ops: ListBuffer[BoardBuilder => BoardBuilder],
      build: BoardBuilder => Push => Position => BoardBuilder
    ): PushableWord =
      PushableWord(m => AtWord(p => ops += (b => build(b)(m)(p))))

    /* Append a "block" cell adding operation to the sequence of operations specified by the user. */
    def addBlockCells(
      ops: ListBuffer[BoardBuilder => BoardBuilder],
      build: Push => Position => Iterable[PushableCell]
    ): PushableWord =
      addPushableCells(ops, b => m => p => b.copy(blockCells = b.blockCells ++ build(m)(p)))

    /* Append a Cell adding operation to the sequence of operations specified by the user. */
    private def addCells(ops: ListBuffer[BoardBuilder => BoardBuilder], build: BoardBuilder => Position => BoardBuilder): AtWord =
      AtWord(p => ops += (b => build(b)(p)))

    /* Append an "enemy" cell adding operation to the sequence of operations specified by the user. */
    def addEnemyCells(ops: ListBuffer[BoardBuilder => BoardBuilder], build: Position => Iterable[Cell]): AtWord =
      addCells(ops, b => p => b.copy(enemyCells = b.enemyCells ++ build(p)))

    /* Append a "wall" cell adding operation to the sequence of operations specified by the user. */
    def addWallCells(ops: ListBuffer[BoardBuilder => BoardBuilder], build: Position => Iterable[Cell]): AtWord =
      addCells(ops, b => p => b.copy(wallCells = b.wallCells ++ build(p)))
  }

  import CellsAddersHelpers.*
  import CellsDuplicators.*

  /** Allows to start the sentence for adding a "mover" cell to the [[BoardBuilder]] currently being used. It returns a
    * [[FacingWord]] so as to allow the user to continue the sentence and specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[FacingWord]] so as to allow the user to continue the sentence
    */
  def hasMoverCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): FacingWord =
    addMoverCells(ops, o => p => Set(OrientableCell(o)(p)))

  /** Allows to start the sentence for adding multiple "mover" cells to the [[BoardBuilder]] currently being used. It returns an
    * [[InAnAreaWord]] so as to allow the user to continue the sentence and specify the area in which placing the cells and the
    * properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasMoverCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d => addMoverCells(ops, o => p => duplicateOrientableCells(d)(o)(p)))

  /** Allows to start the sentence for adding a "generator" cell to the [[BoardBuilder]] currently being used. It returns a
    * [[FacingWord]] so as to allow the user to continue the sentence and specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[FacingWord]] so as to allow the user to continue the sentence
    */
  def hasGeneratorCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): FacingWord =
    addGeneratorCells(ops, o => p => Set(OrientableCell(o)(p)))

  /** Allows to start the sentence for adding multiple "generator" cells to the [[BoardBuilder]] currently being used. It returns
    * an [[InAnAreaWord]] so as to allow the user to continue the sentence and specify the area in which placing the cells and the
    * properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasGeneratorCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d => addGeneratorCells(ops, o => p => duplicateOrientableCells(d)(o)(p)))

  /** Allows to start the sentence for adding a "rotator" cell to the [[BoardBuilder]] currently being used. It returns a
    * [[RotatingWord]] so as to allow the user to continue the sentence and specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[RotatingWord]] so as to allow the user to continue the sentence
    */
  def hasRotatorCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): RotatingWord =
    addRotatorCells(ops, d => p => Set(RotatableCell(d)(p)))

  /** Allows to start the sentence for adding multiple "rotator" cells to the [[BoardBuilder]] currently being used. It returns an
    * [[InAnAreaWord]] so as to allow the user to continue the sentence and specify the area in which placing the cells and the
    * properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasRotatorCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[RotatingWord] =
    InAnAreaWord(w => addRotatorCells(ops, d => p => duplicateRotatableCells(w)(d)(p)))

  /** Allows to start the sentence for adding a "block" cell to the [[BoardBuilder]] currently being used. It returns a
    * [[RotatingWord]] so as to allow the user to continue the sentence and specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[RotatingWord]] so as to allow the user to continue the sentence
    */
  def hasBlockCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): PushableWord =
    addBlockCells(ops, m => p => Set(PushableCell(m)(p)))

  /** Allows to start the sentence for adding multiple "block" cells to the [[BoardBuilder]] currently being used. It returns an
    * [[InAnAreaWord]] so as to allow the user to continue the sentence and specify the area in which placing the cells and the
    * properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasBlockCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[PushableWord] =
    InAnAreaWord(d => addBlockCells(ops, m => p => duplicatePushableCells(d)(m)(p)))

  /** Allows to start the sentence for adding a "enemy" cell to the [[BoardBuilder]] currently being used. It returns a
    * [[RotatingWord]] so as to allow the user to continue the sentence and specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[RotatingWord]] so as to allow the user to continue the sentence
    */
  def hasEnemyCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): AtWord = addEnemyCells(ops, p => Set(Cell(p)))

  /** Allows to start the sentence for adding multiple "enemy" cells to the [[BoardBuilder]] currently being used. It returns an
    * [[InAnAreaWord]] so as to allow the user to continue the sentence and specify the area in which placing the cells and the
    * properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasEnemyCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[AtWord] =
    InAnAreaWord(d => addEnemyCells(ops, p => duplicateBaseCells(d)(p)))

  /** Allows to start the sentence for adding a "wall" cell to the [[BoardBuilder]] currently being used. It returns a
    * [[RotatingWord]] so as to allow the user to continue the sentence and specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[RotatingWord]] so as to allow the user to continue the sentence
    */
  def hasWallCell(using ops: ListBuffer[BoardBuilder => BoardBuilder]): AtWord =
    addWallCells(ops, p => Set(Cell(p)))

  /** Allows to start the sentence for adding multiple "wall" cells to the [[BoardBuilder]] currently being used. It returns an
    * [[InAnAreaWord]] so as to allow the user to continue the sentence and specify the area in which placing the cells and the
    * properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasWallCells(using ops: ListBuffer[BoardBuilder => BoardBuilder]): InAnAreaWord[AtWord] =
    InAnAreaWord(d => addWallCells(ops, p => duplicateBaseCells(d)(p)))
}
