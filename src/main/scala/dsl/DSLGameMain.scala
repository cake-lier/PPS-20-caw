package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.game.controller.ParentGameController
import it.unibo.pps.caw.game.view.GameView
import it.unibo.pps.caw.common.LevelParser
import it.unibo.pps.caw.common.view.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.storage.{FileStorage, LevelStorage}
import it.unibo.pps.caw.common.view.StageResizer
import it.unibo.pps.caw.common.view.sounds.AudioPlayer
import it.unibo.pps.caw.menu.view.MainMenuView
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.{FlowPane, Pane}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.{Parameters, PrimaryStage}
import scalafx.scene.Scene

import java.nio.file.{Path, Paths}
import scala.util.{Failure, Try}

/* The main class for the application launched by the DSL when asked by the user to play a level with the
 * [[it.unibo.pps.caw.dsl.entities.Board]] which has been just created.
 *
 * This application needs to receive as its first and only argument the [[Path]] of the file containing the
 * [[it.unibo.pps.caw.dsl.entities.Board]] to be used for playing. Then the "game" interface will be launched, as if inside the
 * main application after choosing a default level. The only difference is that every control hinting to change the application
 * state (such as "next" and "back" buttons) will result in closing the launched application.
 */
private object DSLGameMain extends JFXApp3 {

  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "Cells At Work - Play your level!"
      resizable = false
      maximized = false
    }
    StageResizer.resize(stage)
    val gameScene: Scene = Scene(stage.width.value, stage.height.value)
    stage.scene = gameScene
    val fileStorage = FileStorage()
    LevelStorage(fileStorage, LevelParser(fileStorage))
      .loadLevel(parameters.raw(0))
      .fold(
        _ => gameScene.root.value = FXMLLoader.load[FlowPane](ClassLoader.getSystemResource("fxml/empty.fxml")),
        l => {
          gameScene.root.value = GameView(
            new ParentGameController {
              override val fileStorage: FileStorage = fileStorage

              override def closeGame(): Unit = sys.exit()
            },
            AudioPlayer(musicVolume = 0.5, soundsVolume = 0.5),
            l,
            gameScene,
            backButtonText = "Close"
          )
        }
      )
  }
}
