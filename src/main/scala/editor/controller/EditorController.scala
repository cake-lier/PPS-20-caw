package it.unibo.pps.caw.editor.controller

import it.unibo.pps.caw.common.model.{Dimensions, Level, Position}
import it.unibo.pps.caw.common.model.cell.{BaseCell, PlayableCell}
import it.unibo.pps.caw.editor.model.LevelEditorModel
import it.unibo.pps.caw.editor.view.EditorView
import java.io.File
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.Source

trait ParentLevelEditorController {
  def closeEditor(): Unit
  def saveLevel(path: String, level: Level[BaseCell]): Unit
}

sealed trait EditorController {
  def closeEditor(): Unit
  def resetLevel(): Unit
  def setCell(cell: BaseCell): Unit
  def updateCellPosition(oldPosition: Position, newPosition: Position): Unit
  def removeCell(position: Position): Unit
  def setPlayableArea(position: Position, dimensions: Dimensions): Unit
  def removePlayableArea(): Unit
  def saveLevel(path: String): Unit
}

object EditorController {
  abstract class AbstractController(parentLevelEditorController: ParentLevelEditorController, view: EditorView)
    extends EditorController {
    protected var levelEditorModel: LevelEditorModel

    override def resetLevel(): Unit = {
      updateShowLevel(levelEditorModel.resetLevel)
    }

    override def removeCell(position: Position): Unit = {
      updateShowLevel(levelEditorModel.unsetCell(position))
    }

    override def setCell(cell: BaseCell): Unit = {
      updateShowLevel(levelEditorModel.setCell(cell))
    }

    override def removePlayableArea(): Unit = {
      updateShowLevel(levelEditorModel.unsetPlayableArea)
    }

    override def setPlayableArea(position: Position, dimensions: Dimensions): Unit =
      updateShowLevel(levelEditorModel.setPlayableArea(position, dimensions));

    override def saveLevel(path: String): Unit =
      levelEditorModel
        .builtLevel
        .fold(view.showError("No playable area was set, could not save"))(parentLevelEditorController.saveLevel(path, _))

    override def closeEditor(): Unit = parentLevelEditorController.closeEditor()

    override def updateCellPosition(oldPosition: Position, newPosition: Position): Unit = {
      updateShowLevel(levelEditorModel.updateCellPosition(oldPosition, newPosition))
    }

    private def updateShowLevel(newLevelEditorModel: LevelEditorModel): Unit =
      levelEditorModel = newLevelEditorModel; view.printLevel(levelEditorModel.currentLevel)
  }

  case class EmptyEditorController(
    parentLevelEditorController: ParentLevelEditorController,
    levelEditorView: EditorView,
    width: Int,
    height: Int
  ) extends AbstractController(parentLevelEditorController: ParentLevelEditorController, levelEditorView: EditorView) {
    protected var levelEditorModel: LevelEditorModel = LevelEditorModel(width, height)
    levelEditorView.printLevel(levelEditorModel.currentLevel)
  }

  case class LevelEditorController(
    parentLevelEditorController: ParentLevelEditorController,
    levelEditorView: EditorView,
    level: Level[BaseCell]
  ) extends AbstractController(parentLevelEditorController: ParentLevelEditorController, levelEditorView: EditorView) {
    protected var levelEditorModel: LevelEditorModel = LevelEditorModel(level)
    levelEditorView.printLevel(levelEditorModel.currentLevel)
  }

  def apply(
    parentLevelEditorController: ParentLevelEditorController,
    levelEditorView: EditorView,
    width: Int,
    height: Int
  ): EditorController =
    EmptyEditorController(parentLevelEditorController, levelEditorView, width, height)

  def apply(
    parentLevelEditorController: ParentLevelEditorController,
    levelEditorView: EditorView,
    level: Level[BaseCell]
  ): EditorController =
    LevelEditorController(
      parentLevelEditorController,
      levelEditorView,
      level
    )
}
