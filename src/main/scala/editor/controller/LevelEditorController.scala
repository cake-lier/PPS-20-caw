package it.unibo.pps.caw.editor.controller

import it.unibo.pps.caw.common.model.{Dimensions, Level, Position}
import it.unibo.pps.caw.common.model.cell.{BaseCell, PlayableCell}
import it.unibo.pps.caw.editor.model.LevelEditorModel
import it.unibo.pps.caw.editor.view.LevelEditorView
import java.io.File
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.Source

trait ParentLevelEditorController {
  def closeEditor(): Unit
  def saveLevel(path: String, level: Level[BaseCell]): Unit
}

sealed trait LevelEditorController {
  def closeEditor(): Unit
  def resetLevel(): Unit
  def setCell(cell: BaseCell): Unit
  def updateCellPosition(oldPosition: Position, newPosition: Position): Unit
  def removeCell(position: Position): Unit
  def setPlayableArea(position: Position, dimensions: Dimensions): Unit
  def removePlayableArea(): Unit
  def saveLevel(path: String): Unit
}

object LevelEditorController {
  case class LevelControllerImpl(
    parentLevelEditorController: ParentLevelEditorController,
    view: LevelEditorView,
    width: Int,
    height: Int,
    level: Option[Level[BaseCell]]
  ) extends LevelEditorController {
    private var levelEditorModel: LevelEditorModel = level.map(LevelEditorModel(_)).getOrElse(LevelEditorModel(width, height))

    view.printLevel(levelEditorModel.currentLevel)

    override def resetLevel(): Unit = {
      updateShowLevel(levelEditorModel.resetLevel)
      view.printLevel(levelEditorModel.currentLevel)
    }

    override def removeCell(position: Position): Unit = {
      updateShowLevel(levelEditorModel.unsetCell(position))
      view.printLevel(levelEditorModel.currentLevel)
    }

    override def setCell(cell: BaseCell): Unit = {
      updateShowLevel(levelEditorModel.setCell(cell))
      view.printLevel(levelEditorModel.currentLevel)
    }

    override def removePlayableArea(): Unit = {
      updateShowLevel(levelEditorModel.unsetPlayableArea)
      view.printLevel(levelEditorModel.currentLevel)
    }

    override def setPlayableArea(position: Position, dimensions: Dimensions): Unit =
      updateShowLevel(levelEditorModel.setPlayableArea(position, dimensions));
      view.printLevel(levelEditorModel.currentLevel)

    override def saveLevel(path: String): Unit =
      levelEditorModel
        .builtLevel
        .fold(view.showError("No playable area was set, could not save"))(parentLevelEditorController.saveLevel(path, _))

    override def closeEditor(): Unit = parentLevelEditorController.closeEditor()

    override def updateCellPosition(oldPosition: Position, newPosition: Position): Unit = {
      updateShowLevel(levelEditorModel.updateCellPosition(oldPosition, newPosition))
      view.printLevel(levelEditorModel.currentLevel)
    }

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
    level: Level[BaseCell]
  ): LevelEditorController =
    LevelControllerImpl(
      parentLevelEditorController,
      levelEditorView,
      level.dimensions.width,
      level.dimensions.height,
      Some(level)
    )
}
