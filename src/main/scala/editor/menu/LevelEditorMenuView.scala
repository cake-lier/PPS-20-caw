package it.unibo.pps.caw.editor.menu

import it.unibo.pps.caw.{FilePicker, ViewComponent}
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.editor.controller.ParentLevelEditorController
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.scene.control.{Button, TextField}
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene
import scalafx.stage.FileChooser

trait LevelEditorMenuView extends ViewComponent[Pane]

object LevelEditorMenuView {
  private final class LevelEditorMenuViewImpl(
      parentLevelEditorController: ParentLevelEditorMenuController,
      scene: Scene,
      buttonMessage: String
  ) extends AbstractViewComponent[Pane]("editor_menu_page.fxml")
      with LevelEditorMenuView {
    @FXML
    var backButton: Button = _
    @FXML
    var loadFile: Button = _
    @FXML
    var continue: Button = _
    @FXML
    var width: TextField = _
    @FXML
    var height: TextField = _

    private val controller: LevelEditorMenuController = LevelEditorMenuController(parentLevelEditorController)

    override val innerComponent: Pane = loader.load[GridPane]

    backButton.setText(buttonMessage)
    continue.setDisable(true)
    width.textProperty().addListener((_, _, newValue) => continue.setDisable(newValue.isEmpty || height.getText.isEmpty))
    height.textProperty().addListener((_, _, newValue) => continue.setDisable(newValue.isEmpty || width.getText.isEmpty))
    backButton.setOnMouseClicked(_ => parentLevelEditorController.closeLevelEditorMenu())
    loadFile.setOnMouseClicked(_ => Option(FilePicker.pickFile(scene)).foreach(parentLevelEditorController.openLevelEditor))
    continue.setOnMouseClicked(_ => parentLevelEditorController.openLevelEditor(width.getText.toInt, height.getText.toInt))
  }

  def apply(
      parentLevelEditorController: ParentLevelEditorMenuController,
      scene: Scene,
      buttonMessage: String
  ): LevelEditorMenuView =
    LevelEditorMenuViewImpl(parentLevelEditorController, scene, buttonMessage)
}
