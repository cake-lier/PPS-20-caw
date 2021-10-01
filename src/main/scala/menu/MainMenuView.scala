package it.unibo.pps.caw.menu

import it.unibo.pps.caw.common.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.{AudioPlayer, FilePicker, SoundButton, Track, ViewComponent}
import it.unibo.pps.caw.editor.view.LevelEditorMenuView
import javafx.fxml.FXML
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene

/** The view which displays the main menu of the application.
  *
  * This view is responsible for displaying the main menu of the main, standalone application and let the user access all
  * functionalities which are accessible from the menu. All functionalities are provided through the [[MainMenuController]] which
  * this component creates and incapsulates. This view should then capture the user input, associate to a given request to the
  * controller and display the effects of the user choices on itself. This view is divided in a subcomponent for each page of the
  * menu, for a better modularization of the application. It must be constructed through its companion object.
  */
trait MainMenuView extends ViewComponent[Pane]

/** Companion object fot the [[MainMenuView]] trait, being a factory for new [[MainMenuView]] instances. */
object MainMenuView {

  /** Returns a new instance of the [[MainMenuView]] trait. It receives a [[ParentMainMenuController]] so as to be able to
    * correctly create and then use its [[MainMenuController]], the [[AudioPlayer]] to be used for playing sounds and music and
    * the ScalaFX'state [[Scene]] in order to draw and display itself.
    *
    * @param parentController
    *   the [[ParentMainMenuController]] used so as to be able to correctly create and then use a [[MainMenuController]]
    * @param audioPlayer
    *   the [[AudioPlayer]] to be used for playing sounds and music
    * @param scene
    *   the ScalaFX'state [[Scene]] on which draw and display the created [[MainMenuView]] instance
    * @return
    *   a new [[MainMenuView]] instance
    */
  def apply(
    parentController: ParentMainMenuController,
    audioPlayer: AudioPlayer,
    levelsCount: Int,
    scene: Scene,
    disableLevels: Boolean
  ): MainMenuView =
    MainMenuViewImpl(parentController, audioPlayer, levelsCount, scene, disableLevels)

  /* Default implementation of the MainMenuView trait. */
  private final class MainMenuViewImpl(
    parentController: ParentMainMenuController,
    audioPlayer: AudioPlayer,
    levelsCount: Int,
    scene: Scene,
    disableLevels: Boolean
  ) extends AbstractViewComponent[Pane]("main_menu_page.fxml")
    with MainMenuView {
    @FXML
    var playButton: SoundButton = _
    @FXML
    var editorButton: SoundButton = _
    @FXML
    var loadButton: SoundButton = _
    @FXML
    var settingsButton: SoundButton = _
    @FXML
    var exitButton: SoundButton = _

    override val innerComponent: Pane = loader.load[GridPane]

    private val controller: MainMenuController = MainMenuController(parentController, this)

    audioPlayer.play(Track.MenuMusic)
    if (controller.levelsCount != 0) {
      playButton.setDisable(false)
      playButton.setOnMouseClicked(_ => scene.root.value = LevelSelectionView(scene, controller))
    }
    loadButton.setOnMouseClicked(_ => FilePicker.pickFile(scene).foreach(f => controller.startGame(f.getPath)))
    settingsButton.setOnMouseClicked(_ => scene.root.value = SettingsView(controller, audioPlayer, scene))
    editorButton.setOnMouseClicked(_ => scene.root.value = LevelEditorMenuView(controller, scene, "Menu"))
    exitButton.setOnMouseClicked(_ => controller.exit())
  }
}
