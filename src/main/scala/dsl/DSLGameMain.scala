package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.game.controller.{Deserializer, ParentGameController}
import it.unibo.pps.caw.game.view.GameView
import it.unibo.pps.caw.game.model.{Level}
import it.unibo.pps.caw.common.{Position, PlayableArea}
import it.unibo.pps.caw.menu.MainMenuView
import it.unibo.pps.caw.AudioPlayer
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.{FlowPane, Pane}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.{Parameters, PrimaryStage}
import scalafx.scene.Scene

import java.nio.file.{Path, Paths}
import scala.io.Source
import scala.util.{Failure, Try, Using}

/** The main class for the application launched by the DSL when asked by the user to play a level with the
  * [[it.unibo.pps.caw .dsl.entities.Board]] has been just created.
  *
  * This application needs to receive as its first and only argument the [[Path]] of the file containing the
  * [[it.unibo.pps.caw .dsl.entities.Board]] to be used for playing. Then the "game" interface will be launched, as if inside the
  * main application after choosing a default level. The only difference is that every control hinting to change the application
  * state (such as "next" and "back" buttons) will result in closing the launched application.
  */
object DSLGameMain extends JFXApp3 {

  override def start(): Unit = {
    val gameScene: Scene = Scene(1080, 720)
    (for {
      f <- Using(Source.fromFile(Paths.get(parameters.raw(0)).toFile))(_.getLines.mkString)
      l <- Deserializer.deserializeLevel(f)
    } yield l).fold(
      _ => gameScene.root.value = FXMLLoader.load[FlowPane](ClassLoader.getSystemResource("fxml/empty.fxml")),
      l => {
        gameScene.root.value = GameView(
          new ParentGameController {

            /** Asks the parent controller to go back to the previous state of the application. */
            override def goBack(): Unit = sys.exit()
          },
          AudioPlayer(),
          l,
          gameScene
        )
      }
    )
    stage = new PrimaryStage {
      title = "Cells At Work - Play your level!"
      resizable = false
      maximized = false
      scene = gameScene
    }
  }
}
