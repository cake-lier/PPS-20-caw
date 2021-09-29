package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.*

sealed trait LevelEditorModel {
  val currentLevel: LevelBuilder
  val builtLevel: Option[Level[BaseCell]]
  def resetLevel: LevelEditorModel
  def setCell(cell: BaseCell): LevelEditorModel
  def updateCellPosition(oldPosition: Position, newPosition: Position): LevelEditorModel
  def unsetCell(position: Position): LevelEditorModel
  def setPlayableArea(position: Position, dimensions: Dimensions): LevelEditorModel
  def unsetPlayableArea: LevelEditorModel
}

object LevelEditorModel {
  private def createPlayableFromBase(cell: BaseCell): PlayableCell = cell match {
    case BaseRotatorCell(p, r)   => PlayableRotatorCell(p, r, true)
    case BaseGeneratorCell(p, o) => PlayableGeneratorCell(p, o, true)
    case BaseEnemyCell(p)        => PlayableEnemyCell(p, true)
    case BaseMoverCell(p, o)     => PlayableMoverCell(p, o, true)
    case BaseBlockCell(p, d)     => PlayableBlockCell(p, d, true)
    case BaseWallCell(p)         => PlayableWallCell(p, true)
  }

  private case class LevelEditorModelImpl(currentLevel: LevelBuilder) extends LevelEditorModel {

    override val builtLevel: Option[Level[BaseCell]] =
      currentLevel
        .playableArea
        .map(
          Level(
            Dimensions(currentLevel.width, currentLevel.height),
            currentLevel
              .board
              .cells
              .map(_ match {
                case PlayableRotatorCell(p, r, _)   => BaseRotatorCell(p, r)
                case PlayableGeneratorCell(p, o, _) => BaseGeneratorCell(p, o)
                case PlayableEnemyCell(p, _)        => BaseEnemyCell(p)
                case PlayableMoverCell(p, o, _)     => BaseMoverCell(p, o)
                case PlayableBlockCell(p, d, _)     => BaseBlockCell(p, d)
                case PlayableWallCell(p, _)         => BaseWallCell(p)
              }),
            _
          )
        )

    override def resetLevel: LevelEditorModel = LevelEditorModel(currentLevel.width, currentLevel.height)

    override def updateCellPosition(oldPosition: Position, newPosition: Position): LevelEditorModel = {
      val updatedCell: PlayableCell = currentLevel
        .board
        .cells
        .find(_.position == oldPosition)
        .map(_ match {
          case PlayableWallCell(_, playable)  => PlayableWallCell(newPosition, playable)
          case PlayableEnemyCell(_, playable) => PlayableEnemyCell(newPosition, playable)
          case PlayableRotatorCell(_, rotationDirection, playable) =>
            PlayableRotatorCell(newPosition, rotationDirection, playable)
          case PlayableGeneratorCell(_, orientation, playable) => PlayableGeneratorCell(newPosition, orientation, playable)
          case PlayableMoverCell(_, orientation, playable)     => PlayableMoverCell(newPosition, orientation, playable)
          case PlayableBlockCell(_, push, playable)            => PlayableBlockCell(newPosition, push, playable)
        })
        .get
      LevelEditorModelImpl(currentLevel.copy(board = currentLevel.board.cells.filter(_.position != oldPosition) + updatedCell))
    }

    override def setCell(cell: BaseCell): LevelEditorModel =
      LevelEditorModelImpl(currentLevel.copy(board = currentLevel.board.cells + createPlayableFromBase(cell)))

    override def unsetCell(position: Position): LevelEditorModel =
      LevelEditorModelImpl(currentLevel.copy(board = currentLevel.board.cells.filter(_.position != position)))

    override def setPlayableArea(position: Position, dimensions: Dimensions): LevelEditorModel =
      LevelEditorModelImpl(currentLevel.copy(playableArea = Some(PlayableArea(position, dimensions))))

    override def unsetPlayableArea: LevelEditorModel = LevelEditorModelImpl(currentLevel.copy(playableArea = None))
  }

  def apply(level: Level[BaseCell]): LevelEditorModel =
    LevelEditorModelImpl(
      LevelBuilder(
        level.dimensions.width,
        level.dimensions.height,
        Board(level.board.cells.map(createPlayableFromBase(_))),
        level.playableArea
      )
    )

  def apply(width: Int, height: Int): LevelEditorModel =
    LevelEditorModelImpl(LevelBuilder(width, height, Board.empty[PlayableCell]))
}
