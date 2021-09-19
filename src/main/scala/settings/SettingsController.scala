package it.unibo.pps.caw
package settings

import java.io.File

trait ParentSettingsController {
  def back(): Unit
}

sealed trait SettingsController {
  def back(): Unit
}

object SettingsController {
  def apply(parentSettingsController: ParentSettingsController): SettingsController = SettingsControllerImpl(
    parentSettingsController: ParentSettingsController
  )
  private case class SettingsControllerImpl(parentSettingsController: ParentSettingsController) extends SettingsController {
    override def back(): Unit = parentSettingsController.back()
  }
}
