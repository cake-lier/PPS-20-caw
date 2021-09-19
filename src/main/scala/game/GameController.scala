package it.unibo.pps.caw.game

import it.unibo.pps.caw.game.model.{Level, Model, PlayableArea, Position}
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType

import java.nio.file.{Path, Paths}
import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture, TimeUnit}
import scala.io.Source
import scala.util.{Try, Using}

/** The parent controller to the [[GameController]].
  *
  * This trait is used for abstracting the functionalities which the [[GameController]] needs from its parent controller so as to
  * offer its own functionalities. In this way, the [[GameController]] is more modular because it can be reused in multiple
  * contexts with multiple parent controllers.
  */
trait ParentGameController {

  /** Asks the parent controller to go back to the previous state of the application. */
  def goBack(): Unit
}

/** The controller which manages the game part of an application.
  *
  * This controller is the mediator between the [[GameView]] and the [[GameModel]]. It intercepts the inputs of the player while
  * playing the game coming from the [[GameView]] and updates the [[GameModel]] accordingly. Then, it shows the updated
  * [[GameModel]] to the [[GameView]] so as to notify the user of the happened changes. It must be constructed through its
  * companion object.
  */
trait GameController {

  /** Goes back to the previous state of the application. */
  def goBack(): Unit

  /** Starts the periodic execution of game steps. It should be called when updates are paused or when they are not yet started
    * and not while they are happening.
    */
  def startUpdates(): Unit

  /** Pauses the periodic execution of game steps. It should be called while the updates are happening and not when they are
    * paused or not yet started.
    */
  def pauseUpdates(): Unit

  /** Executes one game step. */
  def step(): Unit

  /** Discards all updates which have been made to the level currently played by the player and resets it to the initial
    * configuration. If periodic updates of the current level are happening, they are immediately stopped.
    */
  def resetLevel(): Unit

  /** Goes to the next level, if the level currently played has a next level. If not, it simply goes back to the previous state of
    * the application.
    */
  def nextLevel(): Unit
}

/** Companion object of the [[GameController]] trait, containing its factory method. */
object GameController {

  /* Abstract implementation of the GameController trait for factorizing common behaviors. */
  private abstract class AbstractGameController(parentController: ParentGameController, view: GameView, initialLevel: Level)
    extends GameController {
    protected val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    protected var updatesHandler: Option[ScheduledFuture[?]] = None
    protected var model = Model(initialLevel)

    view.drawLevel(model.initialLevel, model.isLevelCompleted)

    def startUpdates(): Unit = updatesHandler match {
      case Some(_) => Console.err.println(GameControllerError.RunningUpdates.message)
      case _       => updatesHandler = Some(scheduler.scheduleAtFixedRate(() => step(), 0, 1, TimeUnit.SECONDS))
    }

    def pauseUpdates(): Unit = updatesHandler match {
      case Some(handler) => {
        handler.cancel(false)
        updatesHandler = None
      }
      case _ => Console.err.println(GameControllerError.NothingToPause.message)
    }

    def step(): Unit = {
      model = model.update()
      view.drawLevelUpdate(model.initialLevel, model.currentBoard, model.isLevelCompleted)
    }

    def resetLevel(): Unit = {
      updatesHandler.foreach(_ => pauseUpdates())
      model = model.reset
      view.drawLevelReset(model.initialLevel)
    }

    def goBack(): Unit = {
      scheduler.shutdown()
      parentController.goBack()
    }
  }

  /* Extension of the AbstractGameController class for playing a generic level. */
  private class ExternalGameController(parentController: ParentGameController, view: GameView, initialLevel: Level)
    extends AbstractGameController(parentController, view, initialLevel) {

    def nextLevel(): Unit = goBack()
  }

  /* Extension of the AbstractGameController class for playing the default levels. */
  private class DefaultGameController(
    parentController: ParentGameController,
    view: GameView,
    levels: Seq[Level],
    initialLevelIndex: Int
  ) extends AbstractGameController(parentController, view, levels(initialLevelIndex - 1)) {
    private var currentIndex: Int = initialLevelIndex

    def nextLevel(): Unit = {
      updatesHandler.foreach(_ => pauseUpdates())
      model.nextLevelIndex(currentIndex) match {
        case Some(v) => {
          currentIndex = v
          model = Model(levels(currentIndex - 1))
          view.drawLevel(model.initialLevel, model.isLevelCompleted)
        }
        case None => goBack()
      }
    }
  }

  /** Returns a new instance of the [[GameController]] trait. It must receive the [[ParentGameController]], which it represents
    * its parent controller which provides all functionalities which must be delegated to this type of controllers, the
    * [[GameView]] which will be called by and will call the returned [[GameController]] instance and the [[Level]] from which
    * starting the game.
    *
    * @param parentController
    *   the parent controller of the returned [[GameController]]
    * @param view
    *   the [[GameView]] which will be called by and which will call the returned [[GameController]] instance
    * @param level
    *   the [[Level]] from which starting the game
    * @return
    *   a new [[GameController]] instance
    */
  def apply(parentController: ParentGameController, view: GameView, level: Level): GameController =
    ExternalGameController(parentController, view, level)

  /** Returns a new instance of the [[GameController]] trait. It must receive the [[ParentGameController]], which it represents
    * its parent controller which provides all functionalities which must be delegated to this type of controllers, the
    * [[GameView]] which will be called by and will call the returned [[GameController]] instance, the sequence of default
    * [[Level]] which will be used during this game and the index of the default [[Level]] in the given sequence from which
    * starting the game.
    *
    * @param parentController
    *   the parent controller of the returned [[GameController]]
    * @param view
    *   the [[GameView]] which will be called by and which will call the returned [[GameController]] instance
    * @param levels
    *   the sequence of default [[Level]] to be used during this game
    * @param levelIndex
    *   the index of the default [[Level]] in the given sequence from which starting the game
    * @return
    *   a new [[GameController]] instance
    */
  def apply(parentController: ParentGameController, view: GameView, levels: Seq[Level], levelIndex: Int): GameController =
    DefaultGameController(parentController, view, levels, levelIndex)
}
