package it.unibo.pps.caw.editor.controller

import it.unibo.pps.caw.editor.LevelEditorView
import it.unibo.pps.caw.editor.model.{Cell, Serializer, Level, LevelEditorModel, Position}

import java.io.File
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.Source

trait ParentLevelEditorController {
  def closeEditor(): Unit
  def backToLevelEditorMenu(): Unit
}

sealed trait LevelEditorController {
  def closeEditor(): Unit
  def backToLevelEditorMenu(): Unit
  def resetLevel(): Unit
  def setCell(cell: Cell): Unit
  def removeCell(position: Position): Unit
  def setPlayableArea(position: Position, playableAreaWidth: Int, playableAreaHeight: Int): Unit
  def removePlayableArea(): Unit
  def saveLevel(): Unit
}

object LevelEditorController {
  case class LevelControllerImpl(
      parentLevelEditorController: ParentLevelEditorController,
      levelEditorView: LevelEditorView,
      width: Int,
      height: Int,
      level: Option[Level]
  ) extends LevelEditorController {
    private var levelEditorModel: LevelEditorModel =
      if (level.isDefined) LevelEditorModel(width, height, level.get) else LevelEditorModel(width, height)
    override def resetLevel(): Unit = updateShowLevel(levelEditorModel.resetLevel)

    override def removeCell(position: Position): Unit = updateShowLevel(levelEditorModel.removeCell(position))

    override def setCell(cell: Cell): Unit = updateShowLevel(levelEditorModel.setCell(cell))

    override def removePlayableArea(): Unit = updateShowLevel(levelEditorModel.removePlayableArea)

    override def setPlayableArea(position: Position, playableAreaWidth: Int, playableAreaHeight: Int): Unit =
      updateShowLevel(levelEditorModel.setPlayableArea(position, playableAreaWidth, playableAreaHeight))

    override def saveLevel(): Unit =
      ExecutionContext.global.execute(() => Serializer.serializeLevel(levelEditorModel.currentLevel))

    override def closeEditor(): Unit = parentLevelEditorController.closeEditor()

    override def backToLevelEditorMenu(): Unit = parentLevelEditorController.backToLevelEditorMenu()

    private def updateShowLevel(newLevelEditorModel: LevelEditorModel): Unit = {
      levelEditorModel = newLevelEditorModel; levelEditorView.printLevel(levelEditorModel.currentLevel)
    }
  }

  def apply(
      parentLevelEditorController: ParentLevelEditorController,
      levelEditorView: LevelEditorView,
      width: Int,
      height: Int
  ): LevelEditorController =
    LevelControllerImpl(parentLevelEditorController, levelEditorView, width, height, None)

  def apply(
      parentLevelEditorController: ParentLevelEditorController,
      levelEditorView: LevelEditorView,
      level: Level
  ): LevelEditorController =
    LevelControllerImpl(parentLevelEditorController, levelEditorView, level.width, level.height, Some(level))
}
