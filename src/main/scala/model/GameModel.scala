package model

import engine.GameEngine
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
      val updatableCell = Seq(
        currentBoard.cells.filter(_.isInstanceOf[GeneratorCell]).toSeq.sorted,
        currentBoard.cells.filter(_.isInstanceOf[RotatorCell]).toSeq.sorted,
        currentBoard.cells.filter(_.isInstanceOf[MoverCell]).toSeq.sorted
      ).flatten

      println(updatableCell)

      def update(cells: Seq[Cell], board: Board[Cell]): Board[Cell] = cells match {
        case h :: t => update(t, GameEngine().nextState(board, h))
        case _      => board
      }
      GameModel(initialBoard, update(updatableCell, currentBoard))
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
