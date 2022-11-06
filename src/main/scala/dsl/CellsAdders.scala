package it.unibo.pps.caw
package dsl

import common.model.{Dimensions, Position}
import common.model.cell.*
import dsl.entities.LevelBuilderState
import dsl.words.*

import scala.collection.mutable.ListBuffer

/* Adds all methods to the DSL that are able to add new cells to a LevelBuilderState.
 *
 * The methods in this module can add a single cell or an area of cells of the same type, for each type of cells in the game.
 */
private trait CellsAdders extends PropertiesWord {

  /* Allows to duplicate any cell given a builder for the cell and the Dimensions and the Position of the upper left corner
   * of the area in which place the duplicated cells.
   */
  private def duplicateCells[A <: Cell](builder: Position => A)(dimensions: Dimensions, position: Position): Iterable[A] =
    for {
      x <- 0 until dimensions.width
      y <- 0 until dimensions.height
    } yield builder(Position(position.x + x, position.y + y))

  /* Contains helper methods for appending cell adding operations to the sequence of operations the user specifies. */
  private object CellsAddersHelpers {

    /* Append an "orientable" cell adding operation to the sequence of operations specified by the user. */
    private def addOrientableCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: LevelBuilderState => Orientation => Position => LevelBuilderState
    ): FacingWord =
      FacingWord(o => AtWord(p => ops += (b => build(b)(o)(p))))

    /* Append a BaseMoverCell adding operation to the sequence of operations specified by the user. */
    def addMoverCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: Orientation => Position => Iterable[BaseMoverCell]
    ): FacingWord =
      addOrientableCells(ops, b => o => p => b.copy(cells = b.cells ++ build(o)(p)))

    /* Append a BaseGeneratorCell cell adding operation to the sequence of operations specified by the user. */
    def addGeneratorCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: Orientation => Position => Iterable[BaseGeneratorCell]
    ): FacingWord =
      addOrientableCells(ops, b => o => p => b.copy(cells = b.cells ++ build(o)(p)))

    /* Append a "rotatable" cell adding operation to the sequence of operations specified by the user. */
    private def addRotatableCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: LevelBuilderState => Rotation => Position => LevelBuilderState
    ): RotatingWord =
      RotatingWord(d => AtWord(p => ops += (b => build(b)(d)(p))))

    /* Append a BaseRotatorCell adding operation to the sequence of operations specified by the user. */
    def addRotatorCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: Rotation => Position => Iterable[BaseRotatorCell]
    ): RotatingWord =
      addRotatableCells(ops, b => d => p => b.copy(cells = b.cells ++ build(d)(p)))

    /* Append a "pushable" cell adding operation to the sequence of operations specified by the user. */
    private def addPushableCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: LevelBuilderState => Push => Position => LevelBuilderState
    ): PushableWord =
      PushableWord(m => AtWord(p => ops += (b => build(b)(m)(p))))

    /* Append a BaseBlockCell adding operation to the sequence of operations specified by the user. */
    def addBlockCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: Push => Position => Iterable[BaseBlockCell]
    ): PushableWord =
      addPushableCells(ops, b => m => p => b.copy(cells = b.cells ++ build(m)(p)))

    /* Append a simple cell adding operation to the sequence of operations specified by the user. */
    private def addCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: LevelBuilderState => Position => LevelBuilderState
    ): AtWord =
      AtWord(p => ops += (b => build(b)(p)))

    /* Append a BaseEnemyCell adding operation to the sequence of operations specified by the user. */
    def addEnemyCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: Position => Iterable[BaseEnemyCell]
    ): AtWord =
      addCells(ops, b => p => b.copy(cells = b.cells ++ build(p)))

    /* Append a BaseWallCell adding operation to the sequence of operations specified by the user. */
    def addWallCells(ops: ListBuffer[LevelBuilderState => LevelBuilderState], build: Position => Iterable[BaseWallCell]): AtWord =
      addCells(ops, b => p => b.copy(cells = b.cells ++ build(p)))

    /* Append a BaseDeleterCell adding operation to the sequence of operations specified by the user. */
    def addDeleterCells(
      ops: ListBuffer[LevelBuilderState => LevelBuilderState],
      build: Position => Iterable[BaseDeleterCell]
    ): AtWord =
      addCells(ops, b => p => b.copy(cells = b.cells ++ build(p)))
  }

  import CellsAddersHelpers.*

  /** Allows to start the sentence for adding a "mover" cell to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] currently
    * being used. It returns a [[it.unibo.pps.caw.dsl.words.FacingWord]] so as to allow the user to continue the sentence and
    * specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[it.unibo.pps.caw.dsl.words.FacingWord]] so as to allow the user to continue the sentence
    */
  def hasMoverCell(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): FacingWord =
    addMoverCells(ops, o => p => Set(BaseMoverCell(o)(p)))

  /** Allows to start the sentence for adding multiple "mover" cells to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]]
    * currently being used. It returns an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the
    * sentence and specify the area in which placing the cells and the properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasMoverCells(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d => addMoverCells(ops, o => p => duplicateCells(BaseMoverCell(o))(d, p)))

  /** Allows to start the sentence for adding a "generator" cell to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]]
    * currently being used. It returns a [[it.unibo.pps.caw.dsl.words.FacingWord]] so as to allow the user to continue the
    * sentence and specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[it.unibo.pps.caw.dsl.words.FacingWord]] so as to allow the user to continue the sentence
    */
  def hasGeneratorCell(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): FacingWord =
    addGeneratorCells(ops, o => p => Set(BaseGeneratorCell(o)(p)))

  /** Allows to start the sentence for adding multiple "generator" cells to the
    * [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] currently being used. It returns an
    * [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the sentence and specify the area in which
    * placing the cells and the properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasGeneratorCells(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d => addGeneratorCells(ops, o => p => duplicateCells(BaseGeneratorCell(o))(d, p)))

  /** Allows to start the sentence for adding a "rotator" cell to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]]
    * currently being used. It returns a [[it.unibo.pps.caw.dsl.words.RotatingWord]] so as to allow the user to continue the
    * sentence and specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[it.unibo.pps.caw.dsl.words.RotatingWord]] so as to allow the user to continue the sentence
    */
  def hasRotatorCell(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): RotatingWord =
    addRotatorCells(ops, r => p => Set(BaseRotatorCell(r)(p)))

  /** Allows to start the sentence for adding multiple "rotator" cells to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]]
    * currently being used. It returns an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the
    * sentence and specify the area in which placing the cells and the properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasRotatorCells(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): InAnAreaWord[RotatingWord] =
    InAnAreaWord(d => addRotatorCells(ops, r => p => duplicateCells(BaseRotatorCell(r))(d, p)))

  /** Allows to start the sentence for adding a "block" cell to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] currently
    * being used. It returns a [[it.unibo.pps.caw.dsl.words.PushableWord]] so as to allow the user to continue the sentence and
    * specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   a [[it.unibo.pps.caw.dsl.words.PushableWord]] so as to allow the user to continue the sentence
    */
  def hasBlockCell(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): PushableWord =
    addBlockCells(ops, m => p => Set(BaseBlockCell(m)(p)))

  /** Allows to start the sentence for adding multiple "block" cells to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]]
    * currently being used. It returns an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the
    * sentence and specify the area in which placing the cells and the properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasBlockCells(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): InAnAreaWord[PushableWord] =
    InAnAreaWord(d => addBlockCells(ops, m => p => duplicateCells(BaseBlockCell(m))(d, p)))

  /** Allows to start the sentence for adding a "enemy" cell to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] currently
    * being used. It returns an [[it.unibo.pps.caw.dsl.words.AtWord]] so as to allow the user to continue the sentence and specify
    * the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.AtWord]] so as to allow the user to continue the sentence
    */
  def hasEnemyCell(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): AtWord =
    addEnemyCells(ops, p => Set(BaseEnemyCell(p)))

  /** Allows to start the sentence for adding multiple "enemy" cells to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]]
    * currently being used. It returns an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the
    * sentence and specify the area in which placing the cells and the properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasEnemyCells(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): InAnAreaWord[AtWord] =
    InAnAreaWord(d => addEnemyCells(ops, p => duplicateCells(BaseEnemyCell.apply)(d, p)))

  /** Allows to start the sentence for adding a "wall" cell to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] currently
    * being used. It returns an [[it.unibo.pps.caw.dsl.words.AtWord]] so as to allow the user to continue the sentence and specify
    * the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.AtWord]] so as to allow the user to continue the sentence
    */
  def hasWallCell(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): AtWord =
    addWallCells(ops, p => Set(BaseWallCell(p)))

  /** Allows to start the sentence for adding multiple "wall" cells to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]]
    * currently being used. It returns an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the
    * sentence and specify the area in which placing the cells and the properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasWallCells(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): InAnAreaWord[AtWord] =
    InAnAreaWord(d => addWallCells(ops, p => duplicateCells(BaseWallCell.apply)(d, p)))

  /** Allows to start the sentence for adding a "deleter" cell to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]]
    * currently being used. It returns an [[it.unibo.pps.caw.dsl.words.AtWord]] so as to allow the user to continue the sentence
    * and specify the other properties of the cell.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.AtWord]] so as to allow the user to continue the sentence
    */
  def hasDeleterCell(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): AtWord =
    addDeleterCells(ops, p => Set(BaseDeleterCell(p)))

  /** Allows to start the sentence for adding multiple "deleter" cells to the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]]
    * currently being used. It returns an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the
    * sentence and specify the area in which placing the cells and the properties of the cell being duplicated all over the area.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    * @return
    *   an [[it.unibo.pps.caw.dsl.words.InAnAreaWord]] so as to allow the user to continue the sentence
    */
  def hasDeleterCells(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): InAnAreaWord[AtWord] =
    InAnAreaWord(d => addDeleterCells(ops, p => duplicateCells(BaseDeleterCell.apply)(d, p)))
}
