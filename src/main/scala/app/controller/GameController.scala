package it.unibo.pps.caw.app.controller

import it.unibo.pps.caw.game.model.{Deserializer, Level}
import it.unibo.pps.caw.app.controller.errors.GameControllerError
import it.unibo.pps.caw.app.controller.Loader

import java.io.File
import java.nio.file.Path
import java.util
import java.util.NoSuchElementException
import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture, TimeUnit}
import scala.io.Source
import scala.util.{Failure, Success, Using}

/** The Controller of a game session.
  *
  * A game session is defined as the player interacting with one or more levels ininterrupted, without going back to the menu.
  *
  * This class represents the controller component of the MVC pattern: it responds to user input by interacting with the model of
  * the game and its view. The instantiation is made through its companion object.
  */
sealed trait GameController {

  /** Loads deafult level to display it to the player.
    *
    * @param index
    *   Identifies the level.
    */
  def loadLevel(index: Int): Unit

  /** Loads level from file to display it to the player.
    *
    * @param file
    *   The level file.
    */
  def loadLevel(file: File): Unit

  /** Starts automatic execution of game steps
    *
    * Should be called when updates are paused or not yet started.
    */
  def startUpdates(): Unit

  /** Pauses automatic game steps
    *
    * Should be called while periodic updates are happening.
    */
  def pauseUpdates(): Unit

  /** Executes one game step */
  def step(): Unit

  /** Resets level
    *
    * Should be called only after level has been loaded.
    */
  def reset(): Unit

  /** Go to next level if current level is a default one, otherwise go back to menu
    */
  def next(): Unit

  /** Go back to menu */
  def back(): Unit
}

object GameController {

  private final class GameControllerImpl(parentController: ParentGameController, view: GameView) extends GameController {
    private var currentLevel: Option[Level] = None
    private var currentIndex: Option[Int] = None

    private final var scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private var updatesHandler: Option[ScheduledFuture[?]] = None

    def loadLevel(index: Int): Unit = {
      currentIndex = Some(index)
      val files: List[File] = File(ClassLoader.getSystemResource("levels/").toURI)
        .listFiles(_.getName.endsWith(".json"))
        .toList
      if (index < 1 || index > files.length)
        Console.err.println(GameControllerError.LevelIndexOutOfBounds.message)
      else
        loadLevel(files(index - 1))
    }

    def loadLevel(file: File): Unit = {
      Loader.loadLevel(file) match {
        case Success(l) => view.drawLevel(l)
        case _          => view.showError(GameControllerError.LevelNotLoaded.message)
      }
    }

    def startUpdates(): Unit = {
      updatesHandler match {
        case Some(_) => Console.err.println(GameControllerError.RunningUpdates.message)
        case _ =>
          updatesHandler = Some(
            scheduler.scheduleAtFixedRate(new Runnable() { def run(): Unit = step() }, 0, 1, TimeUnit.SECONDS)
          )
      }
    }

    def pauseUpdates(): Unit = updatesHandler match {
      case Some(handler) => {
        handler.cancel(false)
        updatesHandler = None
      }
      case _ => Console.err.println(GameControllerError.NothingToPause.message)
    }

    def step(): Unit = {
      val currentBoard = Model.update()
      view.drawGame(currentBoard)
    }

    def reset(): Unit = currentLevel match {
      case Some(l) => view.drawLevel(l)
      case _       => Console.err.println(GameControllerError.NothingToReset.message)
    }

    def next(): Unit = currentIndex match {
      case Some(i) => loadLevel(i + 1)
      case _       => back() // This case is executed when playing a non-default level
    }

    def back(): Unit = {
      parentController.toMenu()
      scheduler.shutdown()
    }

  }

  def apply(parentController: ParentGameController, view: GameView): GameController = GameControllerImpl(parentController, view)

}

/* Mock objects */

class GameView {
  def drawLevel(level: Level): Unit = println(level)
  def drawGame(game: Board): Unit = println("draw new step")
  def showError(msg: String): Unit = println("[VIEW] error: " + msg)
}

trait Board {} //placeholder
object Board {
  private case class BoardImpl() extends Board
  def apply(): Board = BoardImpl()
}

object Model {
  def update(): Board = {
    println("step")
    Board()
  }
  def reset(): Unit = println("[MODEL] reset")
}

trait ParentGameController { // Parent controller of GameController
  def toMenu(): Unit
}

trait ApplicationController extends ParentGameController {}

object ApplicationController {
  private final class ApplicationControllerImpl() extends ApplicationController {
    override def toMenu(): Unit = println("[ApplicationController] toMenu()")
  }
  def apply(): ApplicationController = ApplicationControllerImpl()
}
