package it.unibo.pps.caw
package settings

import java.io.File

trait ParentSettingsController {
  def goBack(): Unit
}

sealed trait SettingsController {
  def goBack(): Unit
}

object SettingsController {
  def apply(parentSettingsController: ParentSettingsController): SettingsController = SettingsControllerImpl(
    parentSettingsController: ParentSettingsController
  )
  private case class SettingsControllerImpl(parentSettingsController: ParentSettingsController) extends SettingsController {
    override def goBack(): Unit = parentSettingsController.goBack()
  }
}
