package it.unibo.pps.caw.editor.model

sealed trait LevelEditorModel {
  val currentLevel: Level
  def resetLevel: LevelEditorModel
  def setCell(cell: Cell): LevelEditorModel
  def removeCell(position: Position): LevelEditorModel
  def setPlayableArea(position: Position, playableAreaWidth: Int, playableAreaHeight: Int): LevelEditorModel
  def removePlayableArea: LevelEditorModel
}

object LevelEditorModel {
  private case class LevelEditorModelImpl(width: Int, height: Int, level: Option[Level]) extends LevelEditorModel {
    override val currentLevel: Level = level.getOrElse(createEmptyLevel())

    override def resetLevel: LevelEditorModel = LevelEditorModel(width, height)

    override def setCell(cell: Cell): LevelEditorModel =
      createLevelEditorModel(width, height, currentLevel.cells + cell, currentLevel.playableArea)

    override def removeCell(position: Position): LevelEditorModel =
      createLevelEditorModel(
        width,
        height,
        currentLevel.cells.filter(_.position != position),
        currentLevel.playableArea
      )

    override def setPlayableArea(position: Position, playableAreaWidth: Int, playableAreaHeight: Int): LevelEditorModel =
      createLevelEditorModel(
        width,
        height,
        currentLevel.cells,
        Some(PlayableArea(position, playableAreaWidth, playableAreaHeight))
      )

    override def removePlayableArea: LevelEditorModel = createLevelEditorModel(height, width, currentLevel.cells, None)

    private def createEmptyLevel() = Level(width, height, Set.empty)

    private def createLevelEditorModel(
        width: Int,
        height: Int,
        cells: Set[Cell],
        playableArea: Option[PlayableArea]
    ): LevelEditorModel = playableArea
      .map(a => LevelEditorModel(width, height, Level(width, height, cells, a)))
      .getOrElse(LevelEditorModel(width, height, Level(width, height, cells)))

  }

  def apply(width: Int, height: Int, level: Level): LevelEditorModel = LevelEditorModelImpl(width, height, Some(level))
  def apply(width: Int, height: Int): LevelEditorModel = LevelEditorModelImpl(width, height, None)
}
