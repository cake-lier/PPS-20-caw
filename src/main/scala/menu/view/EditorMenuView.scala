package it.unibo.pps.caw.menu.view

import it.unibo.pps.caw.common.view.{FilePicker, ViewComponent}
import it.unibo.pps.caw.common.view.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.menu.controller.EditorMenuController

import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.scene.control.{Button, TextField}
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene

/** The view of the editor menu.
  *
  * It is responsible of displaying the editor menu, with all its controls. It must be constructed through its companion object.
  */
trait EditorMenuView extends ViewComponent[Pane]

/** The companion object of the trait [[EditorMenuView]], containing its factory method. */
object EditorMenuView {

  /* Implementation of the EditorMenuView. */
  private final class LevelEditorMenuViewImpl(
    parentController: EditorMenuController,
    scene: Scene,
    buttonMessage: String
  ) extends AbstractViewComponent[Pane]("editor_menu_page.fxml")
    with EditorMenuView {
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
    private val changeListener: TextField => ChangeListener[String] = f =>
      (_, o, n) => {
        if (n.matches("^([1-9]|[1-4][0-9]|50)?$"))
          continue.setDisable(
            (width.getText.isEmpty || width.getText.toInt < 2 || width.getText.toInt > 50) ||
              (height.getText.isEmpty || height.getText.toInt < 2 || height.getText.toInt > 50)
          )
        else
          f.setText(o)
      }
    backButton.setText(buttonMessage)
    continue.setDisable(true)
    width.textProperty().addListener(changeListener(width))
    height.textProperty().addListener(changeListener(height))
    backButton.setOnMouseClicked(_ => parentController.goBack())
    loadFile.setOnMouseClicked(_ => FilePicker.forLevelFile(scene).openFile().foreach(parentController.openEditor(_)))
    continue.setOnMouseClicked(_ => parentController.openEditor(width.getText.toInt, height.getText.toInt))
  }

  /** Returns a new instance of [[EditorMenuView]]. It receives the [[ParentEditorMenuController]] so as to be able to correctly
    * create and then use its [[EditorMenuController]], the ScalaFX [[Scene]] where the [[EditorView]] will draw and display
    * itself, the text that the upper left button will display depending if the [[EditorView]] was called from the menu or as a
    * its own application.
    *
    * @param parentController
    *   the controller needed to build the [[EditorMenuController]]
    * @param scene
    *   the ScalaFX [[Scene]] where the [[EditorMenuView]] will be drawn and displayed
    * @param closeEditorButtonText
    *   the text of the upper left button
    */
  def apply(
    parentController: EditorMenuController,
    scene: Scene,
    buttonMessage: String
  ): EditorMenuView =
    LevelEditorMenuViewImpl(parentController, scene, buttonMessage)
}
