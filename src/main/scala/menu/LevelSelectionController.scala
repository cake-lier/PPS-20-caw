package it.unibo.pps.caw
package menu

trait LevelSelectionController {

  /** Returns the number of default [[it.unibo.pps.caw.game.model.Level]] available. */
  val levelsCount: Int

  /** Starts a new game beginning from one of the default levels, which index is given. Then, the game will continue using the
    * level next to this one, and then the next one and so on until the last one is reached or the player exits the game.
    *
    * @param levelIndex
    *   the index of the default level from which starting the new game
    */
  def startGame(levelIndex: Int): Unit

  def goBack(): Unit
}
