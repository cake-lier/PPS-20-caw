package it.unibo.pps.caw.game.controller

import it.unibo.pps.caw.common.model.{Level, Position}
import it.unibo.pps.caw.common.model.cell.BaseCell
import it.unibo.pps.caw.common.storage.FileStorage
import it.unibo.pps.caw.game.model.GameModel
import it.unibo.pps.caw.game.model.engine.RulesEngine
import it.unibo.pps.caw.game.view.GameView

import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture, TimeUnit}
import scala.io.Source
import scala.util.Using

/** The parent controller to the [[GameController]].
  *
  * This trait is used for abstracting the functionalities which the [[GameController]] needs from its parent controller so as to
  * offer its own functionalities. In this way, the [[GameController]] is more modular because it can be reused in multiple
  * contexts with multiple parent controllers.
  */
trait ParentGameController {

  /** Asks the parent controller to provide its instance of [[it.unibo.pps.caw.common.storage.FileStorage]]. */
  val fileStorage: FileStorage

  /** Asks the parent controller to go back to the previous state of the application. */
  def closeGame(): Unit
}

/** The parent controller to the [[GameController]] when playing the default levels.
  *
  * This trait extends [[ParentGameController]] to add functionalities pertaining to the game default levels to the
  * [[ParentGameController]] itself. In this way, even these functionalities can be delegated to the parent controller of the
  * [[GameController]] that encapsulates it and, being so, it can more modular because it can be reused in multiple contexts with
  * multiple parent controllers.
  */
trait ParentDefaultGameController extends ParentGameController {

  /** Asks the parent controller to add a default level to the set of solved levels.
    *
    * @param index
    *   the index of the level that has been solved
    */
  def addSolvedLevel(index: Int): Unit
}

/** The controller which manages the game part of an application.
  *
  * This controller is the mediator between the [[it.unibo.pps.caw.game.view.GameView]] and the
  * [[it.unibo.pps.caw.game.model.GameModel]]. It intercepts the inputs of the player while playing the game coming from the
  * [[it.unibo.pps.caw.game.view.GameView]] and updates the [[it.unibo.pps.caw.game.model.GameModel]] accordingly. Then, it shows
  * the updated [[it.unibo.pps.caw.game.model.GameModel]] to the [[it.unibo.pps.caw.game.view.GameView]] so as to notify the user
  * of the happened changes. It must be constructed through its companion object.
  */
trait GameController {

  /** Closes the game that is currently being played by the player. */
  def closeGame(): Unit

  /** Starts the periodic execution of game steps. It should be called when updates are paused or when they are not yet started,
    * if not, nothing happens.
    */
  def startUpdates(): Unit

  /** Pauses the periodic execution of game steps. It should be called while the updates are happening, if not, nothing happens.
    */
  def pauseUpdates(): Unit

  /** Executes one game step. */
  def step(): Unit

  /** Discards all updates which have been made to the level currently played by the player and resets it to the initial
    * configuration. If periodic updates of the current level are happening, they are immediately stopped.
    */
  def resetLevel(): Unit

  /** Goes to the next level, if the level currently played has a next level. If not, it simply closes the game. */
  def nextLevel(): Unit

  /** Updates the [[it.unibo.pps.caw.game.model.GameModel]] moving the [[it.unibo.pps.caw.common.model.cell.Cell]] which has a
    * [[it.unibo.pps.caw.common.model.Position]] equal to the given old position parameter to the position given by the new
    * position parameter.
    *
    * @param oldPosition
    *   the [[it.unibo.pps.caw.common.model.Position]] in which a [[it.unibo.pps.caw.common.model.cell.Cell]] is located
    * @param newPosition
    *   the [[it.unibo.pps.caw.common.model.Position]] to which the [[it.unibo.pps.caw.common.model.cell.Cell]] located at the old
    *   position parameter is moved
    */
  def moveCell(oldPosition: Position)(newPosition: Position): Unit
}

/** Companion object of the [[GameController]] trait, containing its factory method. */
object GameController {

  /* Abstract implementation of the GameController trait for factorizing common behaviors. */
  private abstract class AbstractGameController(
    parentController: ParentGameController,
    view: GameView
  ) extends GameController {
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var updatesHandler: Option[ScheduledFuture[?]] = None

    /* Creates a new GameModel to be used by this GameController given the needed RulesEngine. */
    protected def createModel(rulesEngine: RulesEngine): GameModel

    protected var model: GameModel = createModel(
      parentController
        .fileStorage
        .loadResource(path = "cellmachine.pl")
        .map(RulesEngine(_))
        .getOrElse({
          view.showError(message = "Impossible to load rules file")
          RulesEngine()
        })
    )

    view.drawLevel(model.state.levelInitialState, model.state.isCurrentLevelCompleted)

    override def startUpdates(): Unit = updatesHandler match {
      case None => updatesHandler = Some(scheduler.scheduleAtFixedRate(() => step(), 0, 1, TimeUnit.SECONDS))
      case _    => ()
    }

    override def pauseUpdates(): Unit = updatesHandler.foreach(h => {
      h.cancel(false)
      updatesHandler = None
    })

    override def step(): Unit = {
      model = model.update
      if (model.state.isCurrentLevelCompleted) {
        pauseUpdates()
      }
      view.drawPlayBoard(model.state.levelCurrentState.board, model.state.didEnemyDie, model.state.isCurrentLevelCompleted)
    }

    override def nextLevel(): Unit = {
      updatesHandler.foreach(_ => pauseUpdates())
      if (model.state.hasNextLevel) {
        model = model.nextLevel
        view.drawLevel(model.state.levelInitialState, model.state.isCurrentLevelCompleted)
      } else {
        closeGame()
      }
    }

    override def resetLevel(): Unit = {
      updatesHandler.foreach(_ => pauseUpdates())
      model = model.reset
      view.drawSetupBoard(model.state.levelInitialState.board)
    }

    override def closeGame(): Unit = {
      scheduler.shutdown()
      parentController.closeGame()
    }

    override def moveCell(oldPosition: Position)(newPosition: Position): Unit =
      model = model.moveCell(oldPosition)(newPosition)
  }

  /* Extension of the AbstractGameController class for playing a generic level. */
  private class ExternalGameController(parentController: ParentGameController, view: GameView, initialLevel: Level[BaseCell])
    extends AbstractGameController(parentController, view) {

    override protected def createModel(rulesEngine: RulesEngine): GameModel = GameModel(rulesEngine)(initialLevel)
  }

  /* Extension of the AbstractGameController class for playing the default levels. */
  private class DefaultGameController(
    parentController: ParentDefaultGameController,
    view: GameView,
    levels: Seq[Level[BaseCell]],
    initialIndex: Int
  ) extends AbstractGameController(parentController, view) {

    override protected def createModel(rulesEngine: RulesEngine): GameModel = GameModel(rulesEngine)(levels, initialIndex)

    override def step(): Unit = {
      super.step()
      if (model.state.isCurrentLevelCompleted) {
        parentController.addSolvedLevel(model.state.currentLevelIndex)
      }
    }
  }

  /** Returns a new instance of the [[GameController]] trait. It must receive the [[ParentGameController]], which it represents
    * its parent controller which provides all functionalities which must be delegated to this type of controllers, the
    * [[it.unibo.pps.caw.game.view.GameView]] which will be called by and will call the returned [[GameController]] instance and
    * the [[it.unibo.pps.caw.common.model.Level]] from which starting the game.
    *
    * @param parentController
    *   the parent controller of the returned [[GameController]]
    * @param view
    *   the [[it.unibo.pps.caw.game.view.GameView]] which will be called by and which will call the returned [[GameController]]
    *   instance
    * @param level
    *   the [[it.unibo.pps.caw.common.model.Level]] from which starting the game
    * @return
    *   a new [[GameController]] instance
    */
  def apply(parentController: ParentGameController, view: GameView, level: Level[BaseCell]): GameController =
    ExternalGameController(parentController, view, level)

  /** Returns a new instance of the [[GameController]] trait. It must receive the [[ParentDefaultGameController]], which it
    * represents its parent controller which provides all functionalities which must be delegated to this type of controllers, the
    * [[it.unibo.pps.caw.game.view.GameView]] which will be called by and will call the returned [[GameController]] instance, the
    * sequence of default [[it.unibo.pps.caw.common.model.Level]] which will be used during this game and the index of the default
    * [[it.unibo.pps.caw.common.model.Level]] in the given sequence from which starting the game.
    *
    * @param parentController
    *   the parent controller of the returned [[GameController]]
    * @param view
    *   the [[it.unibo.pps.caw.game.view.GameView]] which will be called by and which will call the returned [[GameController]]
    *   instance
    * @param levels
    *   the sequence of default [[it.unibo.pps.caw.common.model.Level]] to be used during this game
    * @param levelIndex
    *   the index of the default [[it.unibo.pps.caw.common.model.Level]] in the given sequence from which starting the game
    * @return
    *   a new [[GameController]] instance
    */
  def apply(
    parentController: ParentDefaultGameController,
    view: GameView,
    levels: Seq[Level[BaseCell]],
    levelIndex: Int
  ): GameController =
    DefaultGameController(parentController, view, levels, levelIndex)
}
