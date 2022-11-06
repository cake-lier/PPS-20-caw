package it.unibo.pps.caw
package common.view

import javafx.scene.Scene
import scalafx.stage.FileChooser

/** A special window dialog for allowing the user to choose a file from, or save a file to, the file system.
  *
  * It must be constructed through one of the factory methods in its companion object.
  */
trait FilePicker {

  /** Opens a window dialog for choosing a file from the file system and getting its path.
    *
    * @return
    *   an [[Option]] wrapping the path to the chosen file
    */
  def openFile(): Option[String]

  /** Opens a window dialog for choosing the path to where to save a file in the file system and getting the path to the chosen
    * location.
    *
    * @return
    *   an [[Option]] wrapping the path to the saved file
    */
  def saveFile(): Option[String]
}

/** Companion object for the [[FilePicker]] trait, containing factory methods for different file pickers. */
object FilePicker {

  /* Implementation of a FilePicker for a level file. */
  private class LevelFilePicker(scene: Scene) extends FilePicker {
    private val chooser: FileChooser = FileChooser()
    chooser.extensionFilters.add(FileChooser.ExtensionFilter("Level file", "*.json"))

    override def openFile(): Option[String] = Option(chooser.showOpenDialog(scene.getWindow)).map(_.getPath)

    override def saveFile(): Option[String] = Option(chooser.showSaveDialog(scene.getWindow)).map(_.getPath)
  }

  /** Creates a new [[FilePicker]] instance for opening and saving a [[it.unibo.pps.caw.common.model.Level]] file. It needs the
    * [[javafx.scene.Scene]] on which displaying the dialog.
    *
    * @param scene
    *   the [[javafx.scene.Scene]] on which displaying the newly created dialog
    * @return
    *   a new [[FilePicker]] for [[it.unibo.pps.caw.common.model.Level]] files
    */
  def forLevelFile(scene: Scene): FilePicker = LevelFilePicker(scene)
}
