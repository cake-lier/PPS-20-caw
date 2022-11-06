package it.unibo.pps.caw
package menu.view

import common.view.{FilePicker, ViewComponent}
import common.view.ViewComponent.AbstractViewComponent
import common.view.sounds.{AudioPlayer, Track}
import menu.controller.{MainMenuController, ParentMainMenuController}

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene

/** The view which displays the main menu of the application.
  *
  * This view is responsible for displaying the main menu of the main, standalone application and let the user access all
  * functionalities which are accessible from the menu. All functionalities are provided through the
  * [[it.unibo.pps.caw.menu.controller.MainMenuController]] which this component creates and incapsulates. This view should then
  * capture the user input, associate to a given request to the controller and display the effects of the user choices on itself.
  * This view is divided in a subcomponent for each page of the menu, for a better modularization of the application. It must be
  * constructed through its companion object.
  */
trait MainMenuView extends ViewComponent[Pane]

/** Companion object fot the [[MainMenuView]] trait, being a factory for new [[MainMenuView]] instances. */
object MainMenuView {

  /** Returns a new instance of the [[MainMenuView]] trait. It receives a
    * [[it.unibo.pps.caw.menu.controller.ParentMainMenuController]] so as to be able to correctly create and then use its
    * [[it.unibo.pps.caw.menu.controller.MainMenuController]], the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used
    * for playing sounds and music and the ScalaFX [[scalafx.scene.Scene]] in order to draw and display itself.
    *
    * @param parentController
    *   the [[it.unibo.pps.caw.menu.controller.ParentMainMenuController]] used so as to be able to correctly create and then use a
    *   [[it.unibo.pps.caw.menu.controller.MainMenuController]]
    * @param audioPlayer
    *   the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used for playing sounds and music
    * @param scene
    *   the ScalaFX [[scalafx.scene.Scene]] on which draw and display the created [[MainMenuView]] instance
    * @return
    *   a new [[MainMenuView]] instance
    */
  def apply(parentController: ParentMainMenuController, audioPlayer: AudioPlayer, scene: Scene): MainMenuView =
    MainMenuViewImpl(parentController, audioPlayer, scene)

  /* Default implementation of the MainMenuView trait. */
  private final class MainMenuViewImpl(parentController: ParentMainMenuController, audioPlayer: AudioPlayer, scene: Scene)
    extends AbstractViewComponent[Pane]("main_menu_page.fxml")
    with MainMenuView {
    @FXML
    var playButton: Button = _
    @FXML
    var editorButton: Button = _
    @FXML
    var loadButton: Button = _
    @FXML
    var settingsButton: Button = _
    @FXML
    var exitButton: Button = _

    override val innerComponent: Pane = loader.load[GridPane]

    private val controller: MainMenuController = MainMenuController(parentController, this)

    audioPlayer.play(Track.MenuMusic)
    if (controller.levelsCount != 0) {
      playButton.setDisable(false)
      playButton.setOnMouseClicked(_ => scene.root.value = LevelSelectionView(scene, controller))
    }
    loadButton.setOnMouseClicked(_ => FilePicker.forLevelFile(scene).openFile().foreach(controller.startGame))
    settingsButton.setOnMouseClicked(_ => scene.root.value = SettingsView(controller, audioPlayer, scene))
    editorButton.setOnMouseClicked(_ => scene.root.value = EditorMenuView(controller, scene))
    exitButton.setOnMouseClicked(_ => controller.exit())
  }
}
