package model

import engine.RulesEngine
import it.unibo.pps.caw.model._

/** Trait representing the model of the game. Changing the state creates another [[GameModel]] instance */
sealed trait GameModel {

  /** Update the position of a cell during setup phase
    * @param oldCellCordinates:
    *   previuos [[Position]] for previuos [[Cell]]
    * @param newCellCoordinates:
    *   new [[Position]] for [[Cell]]
    * @return
    *   updated instance of [[GameModel]]
    */
  def updateCell(oldCellCordinates: Position, newCellCoordinates: Position): GameModel

  /** Calculate the next state of the current [[Board]]
    * @return
    *   updated instance of [[GameModel]]
    */
  def update(): GameModel

  /** Set the current board as the initial [[Board]]
    * @return
    *   resetted instance of [[GameModel]]
    */
  def reset: GameModel

  /** Get the current [[Board]]
    * @return
    *   the current [[Board]]
    */
  val currentBoard: Board[Cell]

  /** Get the initial [[SetupBoard]]
    * @return
    *   the initial [[SetupBoard]]
    */
  val initialBoard: Board[SetupCell]
}

/** Companion object for trai [[GameModel]] */
object GameModel {
  private case class GameModelImpl(initialBoard: Board[SetupCell], optionCurrentBoard: Option[Board[Cell]]) extends GameModel {
    override val currentBoard: Board[Cell] = optionCurrentBoard.getOrElse(
      Board(
        initialBoard.cells
          .map(CellConverter.fromSetup)
          .toSet
      )
    )

    override def update(): GameModel = {
      // parse Board[Cell] to Board[IdCell]
      val it = Iterator.range(1, currentBoard.cells.size)
      val idCells: Set[IdCell] = currentBoard.cells.map(cell => CellConverter.toId(cell, it.next()))
      val idBoard = Board(idCells)

      val updatableCells: Seq[IdCell] = Seq(
        idBoard.cells.filter(_.isInstanceOf[GeneratorCell]).toSeq.sorted,
        idBoard.cells.filter(_.isInstanceOf[RotatorCell]).toSeq.sorted,
        idBoard.cells.filter(_.isInstanceOf[MoverCell]).toSeq.sorted
      ).flatten

      def update(cells: Seq[IdCell], board: Board[IdCell]): Board[IdCell] = cells match {
        case h :: t if (! h.updated) => update(t, RulesEngine().nextState(board, h))
        case h :: t => update(t, board) // ignore already updated cells
        case _      => board
      }

      // parse Board[IdCell] to Board[Cell]
      val updatedIdBoard = update(updatableCells, idBoard)
      val updatedBoard = Board(updatedIdBoard.cells.map(cell => CellConverter.fromId(cell)))
      GameModel(initialBoard, updatedBoard)
    }

    override def reset: GameModel = GameModel(initialBoard)

    override def updateCell(oldCellCordinates: Position, newCellCoordinates: Position): GameModel = {
      val updatedCell: Cell = currentBoard.cells
        .find(_.position == oldCellCordinates)
        .map(_ match {
          case _: WallCell                       => WallCell(newCellCoordinates)
          case _: EnemyCell                      => EnemyCell(newCellCoordinates)
          case RotatorCell(_, rotationDirection) => RotatorCell(newCellCoordinates, rotationDirection)
          case GeneratorCell(_, orientation)     => GeneratorCell(newCellCoordinates, orientation)
          case MoverCell(_, orientation)         => MoverCell(newCellCoordinates, orientation)
        })
        .get
      GameModelImpl(initialBoard, Some(Board(currentBoard.cells.filter(_.position != oldCellCordinates).toSet + updatedCell)))
    }
  }
  def apply(initialBoard: Board[SetupCell], optionCurrentBoard: Board[Cell]): GameModel =
    GameModelImpl(initialBoard: Board[SetupCell], Some(optionCurrentBoard))

  def apply(initialBoard: Board[SetupCell]): GameModel =
    GameModelImpl(initialBoard: Board[SetupCell], None)
}
