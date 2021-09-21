package it.unibo.pps.caw.menu

/** The controller to be used by the settings page in the main menu.
  *
  * This controller is capable of providing all information and services which are useful to the [[SettingsView]]. This controller
  * makes always possible to go back from the selected page to the main menu.
  */
trait SettingsController {

  /** Goes back to the main menu. */
  def backToMainMenu(): Unit
}
