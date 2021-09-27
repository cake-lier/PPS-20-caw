package it.unibo.pps.caw.common

import javafx.scene.Scene
import scalafx.stage.FileChooser

import java.io.File

object FilePicker {
  def pickFile(scene: Scene): Option[File] = {
    val chooser: FileChooser = FileChooser()
    chooser.title = "Choose a level file"
    chooser.extensionFilters.add(FileChooser.ExtensionFilter("Level file", "*.json"))
    Option(chooser.showOpenDialog(scene.getWindow))
  }
  def saveFile(scene: Scene): Option[File] = {
    val chooser: FileChooser = FileChooser()
    chooser.title = "Save your level to file"
    chooser.extensionFilters.add(FileChooser.ExtensionFilter("Level File", "*.json"))
    Option(chooser.showSaveDialog(scene.getWindow))
  }
}
