package it.unibo.pps.caw
package game.model

import engine.RulesEngine
import it.unibo.pps.caw.common.{Board, Position}

/** Trait representing the model of the game. Changing the state creates another [[Model]] instance */
sealed trait Model {

  /** Update the position of a cell during setup phase
    * @param oldCellCordinates:
    *   previuos [[Position]] for previuos [[Cell]]
    * @param newCellCoordinates:
    *   new [[Position]] for [[Cell]]
    * @return
    *   updated instance of [[Model]]
    */
  def updateCell(oldCellCordinates: Position, newCellCoordinates: Position): Model

  /** Calculate the next state of the current [[Board]]
    * @return
    *   updated instance of [[Model]]
    */
  def update(): Model

  /** Set the current board as the initial [[Board]]
    * @return
    *   resetted instance of [[Model]]
    */
  def reset: Model

  /** Get the current [[Board]]
    * @return
    *   the current [[Board]]
    */
  val currentBoard: Board[Cell]

  val initialLevel: Level

  val isLevelCompleted: Boolean

  def nextLevelIndex(currentIndex: Int): Option[Int]

  val didEnemyExplode: Boolean
}

/** Companion object for trait [[Model]]. */
object Model {
  private case class GameModelImpl(level: Level, val didEnemyExplode: Boolean, optionCurrentBoard: Option[Board[Cell]])
    extends Model {
    override val initialLevel: Level = level

    override val currentBoard: Board[Cell] = optionCurrentBoard.getOrElse(
      Board(level.setupBoard.cells.map(CellConverter.fromSetup).toSet)
    )

    override def nextLevelIndex(currentIndex: Int): Option[Int] = Some(currentIndex + 1).filter(_ < 30)

    override val isLevelCompleted: Boolean = currentBoard.cells.filter(_.isInstanceOf[EnemyCell]).size == 0

    override def update(): Model = {
      // parse Board[Cell] to Board[IdCell]
      val idBoard = Board(currentBoard.cells.zipWithIndex.map((c, i) => CellConverter.toId(c, i)))

      def getUpdatableCells(cells: Seq[IdCell]): Seq[IdCell] = {
        Seq(
          cells.filter(_.isInstanceOf[IdGeneratorCell]).toSeq.sorted,
          cells.filter(_.isInstanceOf[IdRotatorCell]).toSeq.sorted,
          cells.filter(_.isInstanceOf[IdMoverCell]).toSeq.sorted
        ).flatten
      }

      def update(cells: Seq[IdCell], board: Board[IdCell]): Board[IdCell] = cells match {
        case h :: t if (!h.updated) => {
          val newBoard = RulesEngine().nextState(board, h)
          update(getUpdatableCells(newBoard.cells.toSeq), newBoard)
        }
        case h :: t => update(t, board) // ignore already updated cells
        case _      => board
      }

      // parse Board[IdCell] to Board[Cell]
      val updatedIdBoard = update(getUpdatableCells(idBoard.cells.toSeq), idBoard)
      val updatedBoard = Board(updatedIdBoard.cells.map(cell => CellConverter.fromId(cell)))
      GameModelImpl(
        initialLevel,
        currentBoard.cells.filter(_.isInstanceOf[EnemyCell]).size > updatedBoard.cells.filter(_.isInstanceOf[EnemyCell]).size,
        Some(updatedBoard)
      )
    }

    override def reset: Model = GameModelImpl(initialLevel, false, None)

    override def updateCell(oldCellCordinates: Position, newCellCoordinates: Position): Model = {
      val updatedCell: Cell =
        currentBoard
          .cells
          .find(_.position == oldCellCordinates)
          .map(_ match {
            case _: WallCell                       => WallCell(newCellCoordinates)
            case _: EnemyCell                      => EnemyCell(newCellCoordinates)
            case RotatorCell(_, rotationDirection) => RotatorCell(newCellCoordinates, rotationDirection)
            case GeneratorCell(_, orientation)     => GeneratorCell(newCellCoordinates, orientation)
            case MoverCell(_, orientation)         => MoverCell(newCellCoordinates, orientation)
            case BlockCell(_, push)                => BlockCell(newCellCoordinates, push)
          })
          .get
      GameModelImpl(
        initialLevel,
        didEnemyExplode,
        Some(Board(currentBoard.cells.filter(_.position != oldCellCordinates).toSet + updatedCell))
      )
    }
  }

  def apply(level: Level, optionCurrentBoard: Board[Cell]): Model = GameModelImpl(level, false, Some(optionCurrentBoard))

  def apply(level: Level): Model = GameModelImpl(level, false, None)
}
