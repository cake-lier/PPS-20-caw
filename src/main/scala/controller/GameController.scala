package it.unibo.pps.caw.controller

import it.unibo.pps.caw.model.Deserializer
import it.unibo.pps.caw.model.Level
import it.unibo.pps.caw.view.View

import java.io.File
import scala.io.Source
import scala.util.{Using, Success, Failure}

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
    val deserializer = Deserializer

    def loadLevel(index: Int): Unit = {
//      val files: List[File] = File(ClassLoader.getSystemResource("levels/").toURI)
//                          .listFiles(_.getName.endsWith(".json")).toList

      val files: List[File] = File(getClass.getClassLoader.getResource("levels/").toURI)
                                .listFiles(_.getName.endsWith(".json")).toList

      val stringLevel: String = Using(Source.fromFile(files(index - 1)))(_.mkString) match {
        case Success(v) => v
        case Failure(e) => throw IllegalArgumentException("There is no level of index " + index)
      }

      val level: Level = deserializer.deserializeLevel(stringLevel) match {
        case Right(level) => {
          view.drawLevel(level)
          level
        }
        case Left(e) => throw e
      }

    }

    def startUpdates(): Unit = ???
    def pauseUpdates(): Unit = ???
    def step(): Unit = ???
    def reset(): Unit = ???

    def next(): Unit = ???
    def back(): Unit = ???
  }

  def apply(view: View): GameController = new GameControllerImpl(view)

}

