package it.unibo.pps.caw
package menu.view

import common.view.{FilePicker, ViewComponent}
import common.view.ViewComponent.AbstractViewComponent
import menu.controller.EditorMenuController

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
  private final class EditorMenuViewImpl(controller: EditorMenuController, scene: Scene)
    extends AbstractViewComponent[Pane](fxmlFileName = "editor_menu_page.fxml")
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
        if (n.matches("^([1-9]|[1-2][0-9]|30)?$"))
          continue.setDisable(
            (width.getText.isEmpty || width.getText.toInt < 2 || width.getText.toInt > 30) ||
              (height.getText.isEmpty || height.getText.toInt < 2 || height.getText.toInt > 30)
          )
        else
          f.setText(o)
      }
    backButton.setText("Menu")
    continue.setDisable(true)
    width.textProperty().addListener(changeListener(width))
    height.textProperty().addListener(changeListener(height))
    backButton.setOnMouseClicked(_ => controller.goBack())
    loadFile.setOnMouseClicked(_ => FilePicker.forLevelFile(scene).openFile().foreach(controller.openEditor))
    continue.setOnMouseClicked(_ => controller.openEditor(width.getText.toInt, height.getText.toInt))
  }

  /** Returns a new instance of [[EditorMenuView]]. It receives the [[it.unibo.pps.caw.menu.controller.EditorMenuController]] so
    * the constructed view can provide the services which should be accessible through itself, the ScalaFX [[scalafx.scene.Scene]]
    * where the [[EditorMenuView]] will draw and display itself.
    *
    * @param controller
    *   the controller needed to build the [[it.unibo.pps.caw.menu.controller.EditorMenuController]]
    * @param scene
    *   the ScalaFX [[scalafx.scene.Scene]] where the [[EditorMenuView]] will be drawn and displayed
    */
  def apply(controller: EditorMenuController, scene: Scene): EditorMenuView = EditorMenuViewImpl(controller, scene)
}
