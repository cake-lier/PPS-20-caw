package it.unibo.pps.caw
package view

import java.net.URL
import java.util

import javafx.{fxml => jfxf, scene => jfxs}
import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.{event => jfxe, fxml => jfxf}
import scalafx.application.Platform
import scalafx.scene.layout.{GridPane, StackPane}
import scalafx.Includes._

object MainMenu {
  def getRoot: StackPane = {
    val resource =
      getClass.getClassLoader.getResource("fxml/main_menu_page.fxml")
    val page: jfxs.Parent = jfxf.FXMLLoader.load(resource)
    val root: StackPane = new StackPane()
    root.getChildren.add(page)
    root
  }
}

class MainMenuController() extends jfxf.Initializable {

  @jfxf.FXML
  private var playButton: jfxsc.Button = _
  @jfxf.FXML
  private var editorButton: jfxsc.Button = _
  @jfxf.FXML
  private var loadButton: jfxsc.Button = _
  @jfxf.FXML
  private var settingsButton: jfxsc.Button = _
  @jfxf.FXML
  private var exitButton: jfxsc.Button = _


  @jfxf.FXML
  def onPlaySelection(e:jfxe.ActionEvent) = {
    //TODO
    println("PLAY")
  }

  @jfxf.FXML
  def onExitSelection(e: jfxe.ActionEvent): Unit = Platform.exit()

  def initialize(url: URL, rb: util.ResourceBundle): Unit = {}
}
