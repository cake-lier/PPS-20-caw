package it.unibo.pps.caw.editor

import it.unibo.pps.caw.{FilePicker, ViewComponent}
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.editor.controller.{LevelEditorController, ParentLevelEditorController}
import it.unibo.pps.caw.editor.model.Level
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene

import java.io.File

trait LevelEditorView extends ViewComponent[Pane] {
  def printLevel(level: Level): Unit
}

object LevelEditorView {
  private final class LevelEditorViewImpl(
      parentLevelEditorController: ParentLevelEditorController,
      scene: Scene,
      closeEditorButtonText: String,
      width: Int,
      height: Int,
      level: Option[Level]
  ) extends AbstractViewComponent[Pane]("editor.fxml")
      with LevelEditorView {

    @FXML
    var backButton: Button = _
    @FXML
    var saveButton: Button = _
    @FXML
    var resetAll: Button = _
    @FXML
    var dragAndDrop: Button = _
    @FXML
    var levelEditorMenuButton: Button = _

    override val innerComponent: Pane = loader.load[GridPane]
    override def printLevel(level: Level): Unit = Platform.runLater(() => println(level))

    private val controller: LevelEditorController =
      if (level.isDefined)
        LevelEditorController(parentLevelEditorController, this, level.get)
      else LevelEditorController(parentLevelEditorController, this, width, height)

    private val levelEditorController: LevelEditorController =
      if (level.isDefined)
        LevelEditorController(parentLevelEditorController, this, level.get)
      else LevelEditorController(parentLevelEditorController, this, width, height)

    levelEditorMenuButton.setOnMouseClicked(_ => levelEditorController.backToLevelEditorMenu())
    backButton.setText(closeEditorButtonText)
    backButton.setOnMouseClicked(_ => levelEditorController.closeEditor())
    saveButton.setOnMouseClicked(_ => FilePicker.saveFile(scene).foreach(controller.saveLevel))
    resetAll.setOnMouseClicked(_ => levelEditorController.resetLevel())
  }

  def apply(
      parentLevelEditorController: ParentLevelEditorController,
      scene: Scene,
      closeEditorButtonText: String,
      boardWidth: Int,
      boardHeight: Int
  ): LevelEditorView =
    LevelEditorViewImpl(parentLevelEditorController, scene, closeEditorButtonText, boardWidth, boardHeight, None)

  def apply(
      parentLevelEditorController: ParentLevelEditorController,
      scene: Scene,
      closeEditorButtonText: String,
      level: Level
  ): LevelEditorView =
    LevelEditorViewImpl(parentLevelEditorController, scene, closeEditorButtonText, level.width, level.height, Some(level))

}
