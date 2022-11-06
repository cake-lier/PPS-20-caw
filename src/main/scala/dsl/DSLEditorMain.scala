package it.unibo.pps.caw
package dsl

import common.LevelParser
import common.model.cell.BaseCell
import common.model.Level
import common.storage.{FileStorage, LevelStorage}
import common.view.StageResizer
import common.view.sounds.AudioPlayer
import editor.controller.ParentEditorController
import editor.view.EditorView

import javafx.fxml.FXMLLoader
import javafx.scene.layout.FlowPane
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

/* The main class for the application launched by the DSL when asked by the user to edit a level with the
 * [[it.unibo.pps.caw.dsl.entities.Board]] which has been just created.
 *
 * This application needs to receive as its first and only argument the [[Path]] of the file containing the
 * [[it.unibo.pps.caw.dsl.entities.Board]] to be used for playing. Then the "editor" interface will be launched, as if inside the
 * main application after choosing to open the level editor. The only difference is that every control hinting to change the
 * application state (such as a "back" button) will result in closing the launched application.
 */
private object DSLEditorMain extends JFXApp3 {

  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "Cells At Work - Edit your level!"
      resizable = false
      maximized = false
    }
    StageResizer.resize(stage)
    val editorScene: Scene = Scene(stage.width.value, stage.height.value)
    stage.scene = editorScene
    val fileStorage = FileStorage()
    val levelManager = LevelStorage(fileStorage, LevelParser(fileStorage))
    levelManager
      .loadLevel(parameters.raw.head)
      .fold(
        _ => editorScene.root.value = FXMLLoader.load[FlowPane](ClassLoader.getSystemResource("fxml/empty.fxml")),
        l => {
          editorScene.root.value = EditorView(
            new ParentEditorController {

              override def closeEditor(): Unit = sys.exit()

              override def saveLevel(path: String, level: Level[BaseCell]): Unit = levelManager.saveLevel(path, level)
            },
            editorScene,
            backButtonText = "Close",
            AudioPlayer(musicVolume = 0.5, soundsVolume = 0.5),
            l
          )
        }
      )
  }
}
