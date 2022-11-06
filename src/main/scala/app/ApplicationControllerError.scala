package it.unibo.pps.caw
package app

/* An error that the [[ApplicationController]] can get into during the normal execution of its function.
 *
 * Each error comes with a human-friendly message that can be displayed to the player so as to inform them that the error has
 * occurred and the given function could not be correctly performed.
 */
private enum ApplicationControllerError(val message: String) {

  /* The error that occurs when a [[it.unibo.pps.caw.common.model.Level]] could not be loaded. */
  case CouldNotLoadLevel extends ApplicationControllerError(message = "An error has occurred, could not load level")

  /* The error that occurs when the [[it.unibo.pps.caw.common.storage.Settings]] could not be saved. */
  case CouldNotSaveSettings extends ApplicationControllerError(message = "An error has occurred, could not save settings")
}
