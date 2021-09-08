package it.unibo.pps.caw
package view

import ViewComponent.AbstractViewComponent
import javafx.fxml.FXML
import javafx.scene.layout.{Pane, GridPane}
import javafx.scene.control.{Button, Label}

object MainMenu {
  def apply(): Component[Pane] = new MainMenuImpl()

  private final class MainMenuImpl
      extends AbstractViewComponent[Pane]("main_menu_page.fxml") {
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

    playButton.setOnMouseClicked(_ => println("CLICKED PLAY"))

    exitButton.setOnMouseClicked(_ => {
      import scalafx.application.Platform
      Platform.exit()
    })
  }
}
