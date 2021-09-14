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
   * @return the current [[Board]] */
  val currentBoard: Board
  /** Get the initial [[SetupBoard]]
   * @return the initial [[SetupBoard]]*/
  val initialBoard: SetupBoard
}

/** Companion object for trai [[GameModel]]*/
object GameModel {
  private case class GameModelImpl(initialBoard: SetupBoard, optionCurrentBoard: Option[Board]) extends GameModel {
    override val currentBoard: Board = optionCurrentBoard.getOrElse(
      Board(
        initialBoard.cells
          .map(CellConverter.fromSetup)
          .toSet
      )
    )

    override def update(): GameModel = GameModel(initialBoard,currentBoard)

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
  def apply(initialBoard: SetupBoard, optionCurrentBoard: Board): GameModel =
    GameModelImpl(initialBoard: SetupBoard, Some(optionCurrentBoard))

  def apply(initialBoard: SetupBoard): GameModel =
    GameModelImpl(initialBoard: SetupBoard, None)
}
