package it.unibo.pps.caw.common

import javafx.scene.Scene
import scalafx.stage.FileChooser

import java.io.File

/** Utility object for interacting with user file system when opening and saving a level file. */
object FilePicker {

  /** Opens dialog window to choose a level file from file system.
    *
    * @param scene
    *   the scene in which to open the dialog window to choose the level file
    * @return
    *   an [[Option]] wrapping the chosen file
    */
  def pickFile(scene: Scene): Option[File] =
    Option(getFileChooser(title = "Choose a level file").showOpenDialog(scene.getWindow))

  /** Opens dialog window to choose where to save a level file in the file system.
    *
    * @param scene
    *   the scene in which to open the dialog window to choose the file location
    * @return
    *   an [[Option]] wrapping the saved file
    */
  def saveFile(scene: Scene): Option[File] =
    Option(getFileChooser(title = "Save your level to file").showSaveDialog(scene.getWindow))

  private def getFileChooser(title: String): FileChooser = {
    val chooser: FileChooser = FileChooser()
    chooser.title = title
    chooser.extensionFilters.add(FileChooser.ExtensionFilter("LevelBuilder File", "*.json"))
    chooser
  }
}
