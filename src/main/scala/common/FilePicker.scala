package it.unibo.pps.caw.common

import javafx.scene.Scene
import scalafx.stage.FileChooser

import java.io.File

object FilePicker {
  def pickFile(scene: Scene): Option[File] = {
    val chooser: FileChooser = FileChooser()
    chooser.title = "Choose a level file"
    chooser.extensionFilters.add(FileChooser.ExtensionFilter("LevelBuilder file", "*.json"))
    Option(chooser.showOpenDialog(scene.getWindow))
  }
  def saveFile(scene: Scene): Option[File] = {
    val chooser: FileChooser = FileChooser()
    chooser.title = "Save your level to file"
    chooser.extensionFilters.add(FileChooser.ExtensionFilter("LevelBuilder File", "*.json"))
    Option(chooser.showSaveDialog(scene.getWindow))
  }
}
