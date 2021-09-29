package it.unibo.pps.caw.editor.controller

import java.io.File

trait ParentLevelEditorMenuController {
  def goBack(): Unit
  def startLevelEditor(width: Int, height: Int): Unit
  def startLevelEditor(path: String): Unit
}

sealed trait LevelEditorMenuController {
  def startLevelEditor(width: Int, height: Int): Unit
  def startLevelEditor(path: String): Unit
}

object LevelEditorMenuController {
  private case class LevelEditorMenuControllerImpl(parentLevelEditorMenuController: ParentLevelEditorMenuController)
    extends LevelEditorMenuController {
    def startLevelEditor(width: Int, height: Int): Unit = parentLevelEditorMenuController.startLevelEditor(width, height)
    def startLevelEditor(path: String): Unit = parentLevelEditorMenuController.startLevelEditor(path)
  }

  def apply(parentLevelEditorMenuController: ParentLevelEditorMenuController): LevelEditorMenuController =
    LevelEditorMenuControllerImpl(parentLevelEditorMenuController)
}
