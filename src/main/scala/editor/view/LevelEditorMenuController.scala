package it.unibo.pps.caw.editor.view

import it.unibo.pps.caw.game.model.Level

import java.io.File

trait ParentLevelEditorMenuController {
  def closeLevelEditorMenu(): Unit
  def openLevelEditor(width: Int, height: Int): Unit
  def openLevelEditor(path: String): Unit
}

sealed trait LevelEditorMenuController {
  def openLevelEditor(width: Int, height: Int): Unit
  def openLevelEditor(path: String): Unit
}

object LevelEditorMenuController {
  private case class LevelEditorMenuControllerImpl(parentLevelEditorMenuController: ParentLevelEditorMenuController)
    extends LevelEditorMenuController {
    def openLevelEditor(width: Int, height: Int): Unit =
      parentLevelEditorMenuController.openLevelEditor(width, height)
    def openLevelEditor(path: String): Unit =
      parentLevelEditorMenuController.openLevelEditor(path)
  }

  def apply(parentLevelEditorMenuController: ParentLevelEditorMenuController): LevelEditorMenuController =
    LevelEditorMenuControllerImpl(parentLevelEditorMenuController)
}
