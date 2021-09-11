package it.unibo.pps.caw.controller

import it.unibo.pps.caw.view.View

/** The Controller of a game session.
 *
 *  A game session is defined as the player interacting with one or more levels
 *  ininterrupted, without going back to the menu.
 *
 *  This class represents the controller component of the MVC pattern: it responds to user input
 *  by interacting with the model of the game and its view.
 *  The instantiation is made through its companion object.
 */
sealed trait GameController {

  /** Loads level file so as to display it to player.
   *
   * @param index Used to identify the desired level.
   */
  def loadLevel(index: Int): Unit

  /** Starts automatic execution of game steps */
  def startUpdates(): Unit

  /** Pauses automatic game steps */
  def pauseUpdates(): Unit

  /** Executes one game step */
  def step(): Unit

  /** Resets level */
  def reset(): Unit

  /** Go to next level */
  def next(): Unit

  /** Go back to menu */
  def back(): Unit
}

object GameController {

  private final class GameControllerImpl(view: View) extends GameController {

    def loadLevel(index: Int): Unit = ???

    def startUpdates(): Unit = ???
    def pauseUpdates(): Unit = ???
    def step(): Unit = ???
    def reset(): Unit = ???

    def next(): Unit = ???
    def back(): Unit = ???
  }

  def apply(view: View): GameController = new GameControllerImpl(view)

}

