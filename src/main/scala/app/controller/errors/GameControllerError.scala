package it.unibo.pps.caw
package app.controller.errors

enum GameControllerError(val message: String) {

  case LevelIndexOutOfBounds extends GameControllerError("Level index out of bounds")

  case LevelNotLoaded extends GameControllerError("Failed to load level")

  case RunningUpdates extends GameControllerError("Updates are already happening")

  case NothingToPause extends GameControllerError("There is nothing to pause")

  case NothingToReset extends GameControllerError("There is no loaded level to reset")

}
