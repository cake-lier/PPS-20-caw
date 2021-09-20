package it.unibo.pps.caw.menu

import it.unibo.pps.caw.ViewComponent
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import javafx.fxml.FXML
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene
import scalafx.stage.FileChooser

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
    * correctly create and then use its [[MainMenuController]]. It also receives the ScalaFX's [[Scene]] in order to draw and
    * display itself.
    *
    * @param parentController
    *   the [[ParentMainMenuController]] used so as to be able to correctly create and then use a [[MainMenuController]]
    * @param levelsCount
    *   the number of default [[it.unibo.pps.caw.game.model.Level]] currently available to be played
    * @param scene
    *   the ScalaFX's [[Scene]] on which draw and display the created [[MainMenuView]] instance
    * @param disableLevels
    *   whether or not to disable the button for accessing the page of the main menu which displays the levels, in case of error
    *   with the [[it.unibo.pps.caw.game.model.Level]] files loading
    * @return
    *   a new [[MainMenuView]] instance
    */
  def apply(parentController: ParentMainMenuController, levelsCount: Int, scene: Scene, disableLevels: Boolean): MainMenuView =
    MainMenuViewImpl(parentController, levelsCount, scene, disableLevels)

  /* Default implementation of the MainMenuView trait. */
  private final class MainMenuViewImpl(
      parentController: ParentMainMenuController,
      levelsCount: Int,
      scene: Scene,
      disableLevels: Boolean
  ) extends AbstractViewComponent[Pane]("main_menu_page.fxml")
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

    private val controller: MainMenuController = MainMenuController(parentController, this, levelsCount)

    playButton.setDisable(disableLevels)
    if (!disableLevels) {
      playButton.setOnMouseClicked(_ => scene.root.value = LevelSelectionView(scene, this, controller))
    }
    loadButton.setOnMouseClicked(_ => {
      val chooser: FileChooser = FileChooser()
      chooser.title = "Choose a level file"
      chooser.extensionFilters.add(FileChooser.ExtensionFilter("Level file", "*.json"))
      Option(chooser.showOpenDialog(scene.getWindow)).foreach(f => controller.startGame(f.toPath))
    })
    exitButton.setOnMouseClicked(_ => controller.exit())
    editorButton.setOnMouseClicked(_ => parentController.openLevelMenuView())
  }
}