package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.{Board, PlayableArea, Position}

sealed trait LevelEditorModel {
  val currentLevel: Level
  def resetLevel: LevelEditorModel
  def setCell(cell: SetupCell): LevelEditorModel
  def updateCellPosition(oldPosition: Position, newPosition: Position): LevelEditorModel
  def removeCell(position: Position): LevelEditorModel
  def setPlayableArea(position: Position, playableAreaWidth: Int, playableAreaHeight: Int): LevelEditorModel
  def removePlayableArea: LevelEditorModel
}

object LevelEditorModel {
  private case class LevelEditorModelImpl(width: Int, height: Int, level: Option[Level]) extends LevelEditorModel {
    override val currentLevel: Level = level.getOrElse(createEmptyLevel())

    override def resetLevel: LevelEditorModel = LevelEditorModel(width, height)

    override def updateCellPosition(oldPosition: Position, newPosition: Position): LevelEditorModel = {
      val updatedCell: SetupCell = currentLevel.board.cells
        .find(_.position == oldPosition)
        .map(_ match {
          case SetupWallCell(_, playable)                       => SetupWallCell(newPosition, playable)
          case SetupEnemyCell(_, playable)                      => SetupEnemyCell(newPosition, playable)
          case SetupRotatorCell(_, rotationDirection, playable) => SetupRotatorCell(newPosition, rotationDirection, playable)
          case SetupGeneratorCell(_, orientation, playable)     => SetupGeneratorCell(newPosition, orientation, playable)
          case SetupMoverCell(_, orientation, playable)         => SetupMoverCell(newPosition, orientation, playable)
          case SetupBlockCell(_, push, playable)                => SetupBlockCell(newPosition, push, playable)
        })
        .get

      createLevelEditorModel(
        width,
        height,
        Board(currentLevel.board.cells.filter(_.position != oldPosition) + updatedCell),
        currentLevel.playableArea
      )
    }

    override def setCell(cell: SetupCell): LevelEditorModel =
      createLevelEditorModel(width, height, Board(currentLevel.board.cells + cell), currentLevel.playableArea)

    override def removeCell(position: Position): LevelEditorModel =
      createLevelEditorModel(
        width,
        height,
        Board(currentLevel.board.cells.filter(_.position != position)),
        currentLevel.playableArea
      )

    override def setPlayableArea(position: Position, playableAreaWidth: Int, playableAreaHeight: Int): LevelEditorModel =
      createLevelEditorModel(
        width,
        height,
        currentLevel.board,
        Some(PlayableArea(position, playableAreaWidth, playableAreaHeight))
      )

    override def removePlayableArea: LevelEditorModel = createLevelEditorModel(height, width, currentLevel.board, None)

    private def createEmptyLevel() = Level(width, height, Board.empty)

    private def createLevelEditorModel(
        width: Int,
        height: Int,
        cells: Board[SetupCell],
        playableArea: Option[PlayableArea]
    ): LevelEditorModel = playableArea
      .map(a => LevelEditorModel(width, height, Level(width, height, cells, a)))
      .getOrElse(LevelEditorModel(width, height, Level(width, height, cells)))

  }

  def apply(width: Int, height: Int, level: Level): LevelEditorModel = LevelEditorModelImpl(width, height, Some(level))
  def apply(width: Int, height: Int): LevelEditorModel = LevelEditorModelImpl(width, height, None)
}
