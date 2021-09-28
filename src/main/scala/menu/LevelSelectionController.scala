package it.unibo.pps.caw.menu

/** The controller to be used by the level selection page in the main menu.
  *
  * This controller is capable of providing all information and services which are useful to the [[LevelSelectionView]]. These
  * information are about the number of [[it.unibo.pps.caw.game.model.Level]] currently playable and the services allow for the
  * start of a new game given the index of the selected [[it.unibo.pps.caw.game.model.Level]]. Moreover, it is always possible to
  * go back from the selected page to the main menu through this controller.
  */
trait LevelSelectionController {

  /** Returns the number of default [[it.unibo.pps.caw.game.model.Level]] available. */
  val levelsCount: Int

  /** Returns the indexes of default solved levels. */
  def solvedLevels: Set[Int]

  /** Starts a new game beginning from one of the default levels, which index is given. Then, the game will continue using the
    * level next to this one, and then the next one and so on until the last one is reached or the player exits the game.
    *
    * @param levelIndex
    *   the index of the default level from which starting the new game
    */
  def startGame(levelIndex: Int): Unit

  /** Returns to the main menu. */
  def goBack(): Unit
}
