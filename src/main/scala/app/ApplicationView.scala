package it.unibo.pps.caw.app

import it.unibo.pps.caw.game.{GameView, ParentGameController}
import it.unibo.pps.caw.menu.{MainMenuView, ParentMainMenuController}
import it.unibo.pps.caw.ViewComponent
import it.unibo.pps.caw.game.model.Level
import javafx.application.Platform
import scalafx.scene.control.Alert
import javafx.scene.layout.Pane
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

import java.io.File
import java.nio.file.Path

/** The view of the main application.
  *
  * This view represents the parent view component of all the views which are part of the main, standalone application. As such,
  * it can create and display every other view component when deemed appropriate. It is also the first component to be created in
  * the application, so it creates the [[ApplicationController]] and every other controller transitively. This operation is done
  * so as to allow the view components to call and be called by their corresponding controller counterparts. It must be created
  * through its companion object.
  */
trait ApplicationView {

  /** Shows the given error message to the user.
    *
    * @param message
    *   the message to display to the user
    */
  def showError(message: String): Unit

  /** Shows the [[MainMenuView]] to the user, hiding the currently displayed view. */
  def showMainMenu(): Unit

  /** Shows the [[GameView]] to the player, hiding the currently displayed view, for playing the given [[Level]].
    *
    * @param level
    *   the [[Level]] which will be first displayed
    */
  def showGame(level: Level): Unit

  /** Shows the [[GameView]] to the player, hiding the currently displayed view, for playing a default [[Level]]. The [[Level]]
    * which will be played will be the one with the given index between the given sequence of default [[Level]]. After playing
    * that [[Level]], the player will be able to play all subsequent [[Level]] in the sequence, until its end.
    *
    * @param levels
    *   the sequence of default [[Level]] that will be used while playing the game
    * @param levelIndex
    *   the index of the [[Level]] which will be first displayed in the given sequence of [[Level]]
    */
  def showGame(levels: Seq[Level], levelIndex: Int): Unit
}

/** Companion object for the [[ApplicationView]] trait, containing its factory method. */
object ApplicationView {

  /* Default implementation of the ApplicationView trait. */
  private class ApplicationViewImpl(stage: PrimaryStage) extends ApplicationView {
    private val controller: ApplicationController = ApplicationController(this)
    private val scene: Scene = Scene(1080, 720)
    private var visibleView: ViewComponent[? <: Pane] =
      MainMenuView(controller, controller.levelsCount, scene, controller.levelsCount == 0)

    stage.resizable = false
    stage.maximized = false
    stage.title = "Cells at Work"
    scene.root.value = visibleView.innerComponent
    stage.scene = scene
    stage.show()

    override def showError(message: String): Unit = Platform.runLater(() => Alert(Alert.AlertType.Error, message).showAndWait())

    override def showGame(level: Level): Unit = Platform.runLater(() => {
      visibleView = GameView(controller, level, scene)
      scene.root.value = visibleView.innerComponent
    })

    override def showGame(levels: Seq[Level], levelIndex: Int): Unit = Platform.runLater(() => {
      visibleView = GameView(controller, levels, levelIndex, scene)
      scene.root.value = visibleView.innerComponent
    })

    override def showMainMenu(): Unit = Platform.runLater(() => {
      visibleView = MainMenuView(controller, controller.levelsCount, scene, controller.levelsCount == 0)
      scene.root.value = visibleView.innerComponent
    })
  }

  /** Returns a new instance of the [[ApplicationView]] trait. It needs the ScalaFX's [[PrimaryStage]] for creating a view for the
    * application.
    *
    * @param stage
    *   the ScalaFX's [[PrimaryStage]] used for creating a view for the application
    * @return
    *   a new [[ApplicationView]] instance
    */
  def apply(stage: PrimaryStage): ApplicationView = ApplicationViewImpl(stage)
}
