package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.common.{AudioPlayer, LevelManager, StageResizer}
import it.unibo.pps.caw.editor.LevelEditorView
import it.unibo.pps.caw.editor.controller.{Deserializer, ParentLevelEditorController}
import it.unibo.pps.caw.editor.model.Level
import javafx.fxml.FXMLLoader
import javafx.scene.layout.FlowPane
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.application.JFXApp3.PrimaryStage

import java.io.File
import java.nio.file.Paths
import scala.io.Source
import scala.util.Using

/** The main class for the application launched by the DSL when asked by the user to edit a level with the
  * [[it.unibo.pps.caw.dsl.entities.Board]] which has been just created.
  *
  * This application needs to receive as its first and only argument the [[Path]] of the file containing the
  * [[it.unibo.pps.caw.dsl.entities.Board]] to be used for playing. Then the "editor" interface will be launched, as if inside the
  * main application after choosing to open the level editor. The only difference is that every control hinting to change the
  * application state (such as a "back" button) will result in closing the launched application.
  */
object DSLEditorMain extends JFXApp3 {

  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "Cells At Work - Edit your level!"
      resizable = false
      maximized = false
    }
    StageResizer.resize(stage)
    val editorScene: Scene = Scene(stage.width.value, stage.height.value)
    stage.scene = editorScene
    LevelManager
      .loadLevelLevelEditor(parameters.raw(0))
      .fold(
        _ => editorScene.root.value = FXMLLoader.load[FlowPane](ClassLoader.getSystemResource("fxml/empty.fxml")),
        l => {
          editorScene.root.value = LevelEditorView(
            new ParentLevelEditorController {
              override def closeEditor(): Unit = sys.exit()

              override def backToLevelEditorMenu(): Unit = sys.exit()

              override def saveLevel(path: String, level: Level): Unit = LevelManager.writeLevel(path, level)
            },
            editorScene,
            "Close",
            l
          )
        }
      )
  }
}
