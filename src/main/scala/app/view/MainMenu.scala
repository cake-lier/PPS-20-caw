package it.unibo.pps.caw.app.view

import ViewComponent.AbstractViewComponent
import javafx.fxml.FXML
import javafx.scene.layout.{GridPane, Pane}
import javafx.scene.control.{Button, Label}

/** Factory for new [[MainMenu]] instance. */
object MainMenu {

  /** Creates a main menu component. */
  def apply(): ViewComponent[Pane] = new MainMenuImpl()

  /** Implementation of the MainMenu. */
  private final class MainMenuImpl extends AbstractViewComponent[Pane]("main_menu_page.fxml") {
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

    playButton.setOnMouseClicked(_ => playButton.getScene.setRoot(LevelSelection().innerComponent))

    exitButton.setOnMouseClicked(_ => {
      import scalafx.application.Platform
      Platform.exit()
    })
  }
}
