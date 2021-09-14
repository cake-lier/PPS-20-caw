package it.unibo.pps.caw
package model

sealed trait GameModel {
  def update(oldCellCordinates: Position, newCellCoordinates: Position): GameModel
  def reset: GameModel
  val currentBoard: Board
  val initialBoard: SetupBoard
}

object GameModel {
  private case class GameModelImpl(initialBoard: SetupBoard, optionCurrentBoard: Option[Board]) extends GameModel {
    override val currentBoard: Board = optionCurrentBoard.getOrElse(
      Board(
        initialBoard.cells
          .map(_ match {
            case SetupEnemyCell(position, _)                      => EnemyCell(position)
            case SetupWallCell(position, _)                       => WallCell(position)
            case SetupRotatorCell(position, rotationDirection, _) => RotatorCell(position, rotationDirection)
            case SetupMoverCell(position, orientation, _)         => MoverCell(position, orientation)
            case SetupGeneratorCell(position, orientation, _)     => GeneratorCell(position, orientation)
          })
          .toSet
      )
    )

    override def reset: GameModel = GameModel(initialBoard, None)

    override def update(oldCellCordinates: Position, newCellCoordinates: Position): GameModel = {
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
  def apply(initialBoard: SetupBoard, optionCurrentBoard: Option[Board]): GameModel =
    GameModelImpl(initialBoard: SetupBoard, optionCurrentBoard)

  def apply(initialBoard: SetupBoard): GameModel =
    GameModelImpl(initialBoard: SetupBoard, None)
}
