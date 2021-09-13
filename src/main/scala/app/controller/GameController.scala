package it.unibo.pps.caw.app.controller


import it.unibo.pps.caw.app.model.{Deserializer, Level}

import java.io.File
import java.nio.file.Path
import java.util
import scala.io.Source
import scala.util.{Failure, Success, Using}

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

  /** Loads deafult level to display it to the player.
   *
   * @param index Used to identify the desired level.
   */
  def loadLevel(index: Int): Unit

  /** Loads level from file to display it to the player.
    *
    * @param file The level file.
    */
  def loadLevel(file: File): Unit

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

  private final class GameControllerImpl(view: GameView, parentController: ParentGameController) extends GameController {

    def loadLevel(index: Int): Unit = {
      val files: List[File] = File(ClassLoader.getSystemResource("levels/").toURI)
                          .listFiles(_.getName.endsWith(".json")).toList
      if (index < 1 || index > files.length) throw IllegalArgumentException("There is no level of index " + index)
      loadLevel(files(index-1))
    }

    def loadLevel(file: File): Unit = {

      val stringLevel: String = Using(Source.fromFile(file))(_.getLines.mkString) match {
        case Success(v) => v
        case Failure(e) => throw e
      }

      val level: Level = Deserializer.deserializeLevel(stringLevel) match {
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

  def apply(view: GameView, parentController: ParentGameController): GameController
              = GameControllerImpl(view, parentController)

}

/* Mock objects */

class GameView {

  def drawLevel(level: Level): Unit = println(level)

}

trait ParentGameController { // Parent controller of GameController

}

trait ApplicationController extends ParentGameController {

}

object ApplicationController {

  private final class ApplicationControllerImpl() extends ApplicationController {

  }

  def apply(): ApplicationController = ApplicationControllerImpl()

}

