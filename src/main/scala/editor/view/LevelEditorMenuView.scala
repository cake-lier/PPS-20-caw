package it.unibo.pps.caw.editor.view

import it.unibo.pps.caw.common.FilePicker
import it.unibo.pps.caw.common.view.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.view.ViewComponent
import it.unibo.pps.caw.editor.controller.{EditorMenuController, ParentLevelEditorController, ParentLevelEditorMenuController}
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.scene.control.TextFormatter.Change
import javafx.scene.control.{Button, TextField, TextFormatter}
import javafx.scene.layout.{GridPane, Pane}
import javafx.util.StringConverter
import scalafx.scene.Scene
import scalafx.stage.FileChooser

import java.util.function.UnaryOperator

/** The view of the editor menu.
  *
  * It is responsible of displaying the editor menu, with all its controls. It must be constructed through its companion object.
  */
trait LevelEditorMenuView extends ViewComponent[Pane]

/** The companion object of the trait [[LevelEditorMenuView]], containing its factory method. */
object LevelEditorMenuView {

  /* Implementation of the LevelEditorMenuView. */
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

    override val innerComponent: Pane = loader.load[GridPane]

    private val controller: EditorMenuController = EditorMenuController(parentLevelEditorController)
    private val changeListener: TextField => ChangeListener[String] = textField =>
      (_, oldValue, newValue) => {
        if (newValue.matches("^([0-4])?([0-9])?$"))
          continue.setDisable(width.getText.isEmpty || height.getText.isEmpty)
        else textField.setText(oldValue)
      }
    backButton.setText(buttonMessage)
    continue.setDisable(true)
    //TODO: change value for dimensions also in the schema
    width.textProperty().addListener(changeListener(width))
    height.textProperty().addListener(changeListener(height))
    backButton.setOnMouseClicked(_ => parentLevelEditorController.goBack())
    loadFile.setOnMouseClicked(_ => FilePicker.pickFile(scene).foreach(f => parentLevelEditorController.startEditor(f.getPath)))
    continue.setOnMouseClicked(_ => parentLevelEditorController.startEditor(width.getText.toInt, height.getText.toInt))
  }

  /** Returns a new instance of [[LevelEditorMenuView]]. It receives the [[ParentLevelEditorMenuController]] so as to be able to
    * correctly create and then use its [[EditorMenuController]], the ScalaFX [[Scene]] where the [[EditorView]] will draw and
    * display itself, the text that the upper left button will display depending if the [[EditorView]] was called from the menu or
    * as a its own application.
    * @param parentLevelEditorController
    *   the controller needed to build the [[EditorMenuController]]
    * @param scene
    *   the ScalaFX [[Scene]] where the [[LevelEditorMenuView]] will be drawn and displayed
    * @param closeEditorButtonText
    *   the text of the upper left button
    */
  def apply(
    parentLevelEditorController: ParentLevelEditorMenuController,
    scene: Scene,
    buttonMessage: String
  ): LevelEditorMenuView =
    LevelEditorMenuViewImpl(parentLevelEditorController, scene, buttonMessage)
}
