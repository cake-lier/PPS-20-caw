package it.unibo.pps.caw
package menu.controller

/** The controller to be used by the settings page in the main menu.
  *
  * This controller is capable of providing all information and services which are useful to the
  * [[it.unibo.pps.caw.menu.view.SettingsView]]. This controller makes always possible to go back from the selected page to the
  * main menu.
  */
trait SettingsController {

  /** Returns the current value of the music volume. */
  def musicVolume: Double

  /** Returns the current value of the sound volume. */
  def soundsVolume: Double

  /** Goes back to the main menu. */
  def goBack(): Unit

  /** Saves values of volume as set in [[it.unibo.pps.caw.menu.view.SettingsView]].
    *
    * @param musicVolume
    *   the value of the music volume
    * @param soundsVolume
    *   the value of sound effects volume
    */
  def saveVolumeSettings(musicVolume: Double, soundsVolume: Double): Unit
}
