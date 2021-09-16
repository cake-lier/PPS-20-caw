package it.unibo.pps.caw.game

/** An error happening inside the [[GameController]].
  *
  * The type of errors represented by this enum are errors which can arise while calling methods of the [[GameController]].
  */
enum GameControllerError(val message: String) {

  /** The error which arises when trying to start the periodic game updates when they are already happening. */
  case RunningUpdates extends GameControllerError("Updates are already happening")

  /** The error which arises when trying to pause the periodic game updates when they are already paused or have not yet been
    * started.
    */
  case NothingToPause extends GameControllerError("There is nothing to pause")
}
