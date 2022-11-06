package it.unibo.pps.caw
package app

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

/** Main class of the application. */
object Main extends JFXApp3 {

  override def start(): Unit = ApplicationView(PrimaryStage())
}
