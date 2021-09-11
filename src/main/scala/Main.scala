package it.unibo.pps.caw

import view.AppView
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

/** Main class of the application. */
object Main extends JFXApp3 {
  override def start(): Unit = {
    val stage = new PrimaryStage
    new AppView(stage).drawMenu()
  }
}
