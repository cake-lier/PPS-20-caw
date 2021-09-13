package it.unibo.pps.caw.app.controller


import it.unibo.pps.caw.app.model.{Deserializer, Level}

import java.io.File
import java.nio.file.Path
import java.util
import java.util.NoSuchElementException
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

  /** Go to next level if current level is a default one,
    * otherwise go back to menu */
  def next(): Unit

  /** Go back to menu */
  def back(): Unit
}

object GameController {

  private final class GameControllerImpl(view: GameView, parentController: ParentGameController) extends GameController {
    private var currentLevel: Option[Level] = None
    private var currentIndex: Option[Int] = None

    def loadLevel(index: Int): Unit = {
      currentIndex = Some(index)
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

      currentLevel = Deserializer.deserializeLevel(stringLevel) match {
        case Right(level) => {
          view.drawLevel(level)
          Some(level)
        }
        case Left(e) => throw e
      }
    }

    def startUpdates(): Unit = ???
    def pauseUpdates(): Unit = ???
    def step(): Unit = ???

    def reset(): Unit = currentLevel match {
      case Some(l) => view.drawLevel(l)
      case _ => throw NoSuchElementException("There is no loaded level to reset")
    }

    def next(): Unit = currentIndex match {
      case Some(i) => loadLevel(i + 1) /* how do we manage next() after last level? do we just not show the button? */
      case _ => back() // This case is executed when playing a non-default level
    }

    def back(): Unit = parentController.toMenu()

  }

  def apply(view: GameView, parentController: ParentGameController): GameController
              = GameControllerImpl(view, parentController)

}

object Test extends App{
  GameController(GameView(), ApplicationController()).next()
}

/* Mock objects */

class GameView {

  def drawLevel(level: Level): Unit = println(level)

}

trait ParentGameController { // Parent controller of GameController
  def toMenu(): Unit
}

trait ApplicationController extends ParentGameController {

}

object ApplicationController {

  private final class ApplicationControllerImpl() extends ApplicationController {
    override def toMenu(): Unit = ???
  }

  def apply(): ApplicationController = ApplicationControllerImpl()

}

