package it.unibo.pps.caw

import it.unibo.pps.caw.view.AppView
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

object Main extends JFXApp3 {
  override def start(): Unit = {
    val stage = new PrimaryStage
    new AppView(stage).drawMenu()
  }
}
