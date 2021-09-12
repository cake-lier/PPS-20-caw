package it.unibo.pps.caw.app

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

import it.unibo.pps.caw.app.view.AppView

/** Main class of the application. */
object Main extends JFXApp3 {
  override def start(): Unit = {
    val stage = new PrimaryStage
    new AppView(stage).drawMenu()
  }
}
