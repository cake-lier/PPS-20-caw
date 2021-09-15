package it.unibo.pps.caw.game

import it.unibo.pps.caw.{AudioManager, ViewComponent, Track}
import javafx.scene.layout.Pane
import scalafx.scene.Scene

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
}

/** Companion object of the [[GameView]] trait, containing its factory method. */
object GameView {

  /* Default implementation of the GameView trait. */
  private class GameViewImpl(parentController: ParentGameController, scene: Scene) extends GameView {
    AudioManager.play(Track.Game, stopAll = true)

    private val controller: GameController = GameController(parentController, this)

    override val innerComponent: Pane = Pane()

    override def backToMenu(): Unit = controller.backToMainMenu()
  }

  /** Returns a new instance of the [[GameView]] trait. It receives a [[ParentGameController]] so as to be able to complete the
    * construction of a [[GameController]] correctly in order to use it. It also receives the ScalaFX's [[Scene]] on which
    * displaying the instance after being constructed.
    *
    * @param parentController
    *   the controller needed so as to be able to complete the construction of a [[GameController]] correctly
    * @param scene
    *   the ScalaFX's [[Scene]] on which displaying the instance after being constructed
    * @return
    *   a new [[GameView]] instance
    */
  def apply(parentController: ParentGameController, scene: Scene): GameView = GameViewImpl(parentController, scene)
}
