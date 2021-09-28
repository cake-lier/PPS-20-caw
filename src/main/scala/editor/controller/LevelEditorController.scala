package it.unibo.pps.caw.editor.controller

import it.unibo.pps.caw.common.model.{Dimensions, Position}
import it.unibo.pps.caw.editor.LevelEditorView
import it.unibo.pps.caw.editor.model.{Cell, Level, LevelEditorModel, SetupCell}

import java.io.File
import java.util.concurrent.{Executors, ExecutorService}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.Source

trait ParentLevelEditorController {
  def closeEditor(): Unit
  def backToLevelEditorMenu(): Unit
  def saveLevel(path: String, level: Level): Unit
}

sealed trait LevelEditorController {
  def closeEditor(): Unit
  def backToLevelEditorMenu(): Unit
  def resetLevel(): Unit
  def setCell(cell: SetupCell): Unit
  def updateCellPosition(oldPosition: Position, newPosition: Position): Unit
  def removeCell(position: Position): Unit
  def setPlayableArea(position: Position, dimensions: Dimensions): Unit
  def removePlayableArea(): Unit
  def saveLevel(path: String): Unit
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
      level.map(LevelEditorModel(width, height, _)).getOrElse(LevelEditorModel(width, height))

    levelEditorView.printLevel(levelEditorModel.currentLevel)

    override def resetLevel(): Unit = {
      updateShowLevel(levelEditorModel.resetLevel)
      levelEditorView.printLevel(levelEditorModel.currentLevel)
    }

    override def removeCell(position: Position): Unit = {
      updateShowLevel(levelEditorModel.removeCell(position))
      levelEditorView.printLevel(levelEditorModel.currentLevel)
    }

    override def setCell(cell: SetupCell): Unit = {
      updateShowLevel(levelEditorModel.setCell(cell))
      levelEditorView.printLevel(levelEditorModel.currentLevel)
    }

    override def removePlayableArea(): Unit = {
      updateShowLevel(levelEditorModel.removePlayableArea)
      levelEditorView.printLevel(levelEditorModel.currentLevel)
    }

    override def setPlayableArea(position: Position, dimensions: Dimensions): Unit =
      updateShowLevel(levelEditorModel.setPlayableArea(position, dimensions));
      levelEditorView.printLevel(levelEditorModel.currentLevel)

    override def saveLevel(path: String): Unit = parentLevelEditorController.saveLevel(path, levelEditorModel.currentLevel)

    override def closeEditor(): Unit = parentLevelEditorController.closeEditor()

    override def updateCellPosition(oldPosition: Position, newPosition: Position): Unit = {
      updateShowLevel(levelEditorModel.updateCellPosition(oldPosition, newPosition))
      levelEditorView.printLevel(levelEditorModel.currentLevel)
    }

    override def backToLevelEditorMenu(): Unit = parentLevelEditorController.backToLevelEditorMenu()

    private def updateShowLevel(newLevelEditorModel: LevelEditorModel): Unit =
      levelEditorModel = newLevelEditorModel
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
