package it.unibo.pps.caw.game

import it.unibo.pps.caw.ViewComponent
import it.unibo.pps.caw.game.model.Level
import javafx.scene.layout.Pane
import scalafx.scene.Scene

import java.io.File
import java.nio.file.Path

/** The parent view to the [[GameView]].
  *
  * This trait is used for abstracting the functionalities which the [[GameView]] needs from its parent view so as to offer its
  * own functionalities. In this way, the [[GameView]] is more modular because it can be reused in multiple contexts with multiple
  * parent views.
  */
trait ParentGameView {

  /** Asks to the parent view component to display the given error message to the player.
    *
    * @param message
    *   the error message to display to the player
    */
  def showError(message: String): Unit
}

/** The view which displays the game part of an application.
  *
  * This view is responsible for displaying everything related to a game in an application. This means that this component should
  * capture the user input while playing and should relay it to its controller, the [[GameController]]. After the controller has
  * processed the input, the [[GameView]] should be used to display the current state of the game. It must be constructed through
  * its companion object.
  */
trait GameView extends ViewComponent[Pane] {

  /** Makes the application view go back to the main menu, displaying it. */
  def backToMenu(): Unit

  /** Displays the given [[Level]].
    *
    * @param level
    *   the [[Level]] to display
    */
  def drawLevel(level: Level): Unit

  /** Displays the given error message to the player.
    *
    * @param message
    *   the error message to display
    */
  def showError(message: String): Unit
}

/** Companion object of the [[GameView]] trait, containing its factory method. */
object GameView {

  /* Abstract implementation of the GameView trait for factorizing common behaviors. */
  private abstract class AbstractGameView(
    parentController: ParentGameController,
    parentView: ParentGameView,
    scene: Scene
  ) extends GameView {
    private val controller: GameController = createController()

    protected def createController(): GameController

    override val innerComponent: Pane = Pane()

    override def showError(message: String): Unit = parentView.showError(message)

    override def drawLevel(level: Level): Unit = ???

    override def backToMenu(): Unit = controller.goBack()
  }

  /* Extension of AbstractGameView for displaying default levels. */
  private class DefaultGameView(
    parentController: ParentGameController,
    parentView: ParentGameView,
    levelIndex: Int,
    scene: Scene
  ) extends AbstractGameView(parentController, parentView, scene) {
    override protected def createController(): GameController = GameController(parentController, this, levelIndex)
  }

  /* Extension of AbstractGameView for displaying a generic level. */
  private class ExternalGameView(
    parentController: ParentGameController,
    parentView: ParentGameView,
    levelPath: Path,
    scene: Scene
  ) extends AbstractGameView(parentController, parentView, scene) {
    override protected def createController(): GameController = GameController(parentController, this, levelPath)
  }

  /** Returns a new instance of the [[GameView]] trait. It receives a [[ParentGameController]] so as to be able to complete the
    * construction of a [[GameController]] correctly in order to use it, the [[ParentGameView]] which is the parent view of the
    * created [[GameView]], the index of the default level from which starting the game and the ScalaFX's [[Scene]] on which
    * displaying the instance after being constructed.
    *
    * @param parentController
    *   the controller needed so as to be able to complete the construction of a [[GameController]] correctly
    * @param scene
    *   the ScalaFX's [[Scene]] on which displaying the instance after being constructed
    * @param parentView
    *   the parent view component to the created [[GameView]]
    * @param levelIndex
    *   the index of the default level from which starting the game
    * @return
    *   a new [[GameView]] instance
    */
  def apply(
    parentController: ParentGameController,
    parentView: ParentGameView,
    levelIndex: Int,
    scene: Scene
  ): GameView =
    DefaultGameView(parentController, parentView, levelIndex, scene)

  /** Returns a new instance of the [[GameView]] trait. It receives a [[ParentGameController]] so as to be able to complete the
    * construction of a [[GameController]] correctly in order to use it, the [[ParentGameView]] which is the parent view of the
    * created [[GameView]], the [[Path]] of the file which contains the level from which starting the game and the ScalaFX's
    * [[Scene]] on which displaying the instance after being constructed.
    *
    * @param parentController
    *   the controller needed so as to be able to complete the construction of a [[GameController]] correctly
    * @param scene
    *   the ScalaFX's [[Scene]] on which displaying the instance after being constructed
    * @param parentView
    *   the parent view component to the created [[GameView]]
    * @param levelPath
    *   the [[Path]] of the file which contains the level from which starting the game
    * @return
    *   a new [[GameView]] instance
    */
  def apply(
    parentController: ParentGameController,
    parentView: ParentGameView,
    levelPath: Path,
    scene: Scene
  ): GameView = ExternalGameView(parentController, parentView, levelPath, scene)
}
