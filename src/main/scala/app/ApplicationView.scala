package it.unibo.pps.caw.app

import it.unibo.pps.caw.menu.{MainMenuView, ParentMainMenuController}
import it.unibo.pps.caw.ViewComponent
import javafx.application.Platform
import scalafx.scene.control.Alert
import it.unibo.pps.caw.game.model.Level as GameLevel
import it.unibo.pps.caw.editor.model.Level as EditorLevel
import it.unibo.pps.caw.menu.MainMenuView
import it.unibo.pps.caw.ViewComponent
import it.unibo.pps.caw.editor.LevelEditorView
import it.unibo.pps.caw.editor.menu.LevelEditorMenuView
import it.unibo.pps.caw.editor.model.Level
import it.unibo.pps.caw.game.GameView
import javafx.geometry.Rectangle2D
import javafx.scene.layout.Pane
import javafx.stage.Screen
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
  def showGame(level: GameLevel): Unit

  /** Shows the [[GameView]] to the player, hiding the currently displayed view, for playing a default [[Level]]. The [[Level]]
    * which will be played will be the one with the given index between the given sequence of default [[Level]]. After playing
    * that [[Level]], the player will be able to play all subsequent [[Level]] in the sequence, until its end.
    *
    * @param levels
    *   the sequence of default [[Level]] that will be used while playing the game
    * @param levelIndex
    *   the index of the [[Level]] which will be first displayed in the given sequence of [[Level]]
    */
  def showGame(levels: Seq[GameLevel], levelIndex: Int): Unit

  /** Shows the [[LevelEditorView]] to the player with an empty level, hiding the currently displayed view.
    * @param width:
    *   the width of the empty [[Level]]
    * @param height:
    *   the height of the empty [[Level]]
    */
  def showLevelEditor(width: Int, height: Int): Unit

  /** Shows the [[LevelEditorView]] to the player with an empty level, hiding the currently displayed view.
    * @param level:
    *   the loaded level
    */
  def showLevelEditor(level: EditorLevel): Unit

  /** Shows the [[LevelEditorMenuView]] to the player.
    * @param buttonText:
    *   the text to be written in the back or close button of [[LevelEditorView]] and [[LevelEditorMenuView]]
    */
  def showEditorMenuView(): Unit
}

/** Companion object for the [[ApplicationView]] trait, containing its factory method. */
object ApplicationView {

  /* Default implementation of the ApplicationView trait. */
  private class ApplicationViewImpl(stage: PrimaryStage) extends ApplicationView {
    private val controller: ApplicationController = ApplicationController(this)
    private val scene: Scene = Scene(1080, 720)
    private var visibleView: ViewComponent[? <: Pane] =
      MainMenuView(controller, controller.levelsCount, scene, controller.levelsCount == 0)
    /* TODO
    private val screenSize: Rectangle2D = Screen.getPrimary.getBounds
    private val scene: Scene = Scene(screenSize.getWidth * 0.80, screenSize.getHeight * 0.80)*/

    stage.resizable = false
    stage.maximized = false
    stage.title = "Cells at Work"
    scene.root.value = visibleView.innerComponent
    stage.scene = scene
    stage.show()

    override def showError(message: String): Unit = Platform.runLater(() => Alert(Alert.AlertType.Error, message).showAndWait())

    override def showGame(level: GameLevel): Unit = setVisibleView(GameView(controller, level, scene))

    override def showGame(levels: Seq[GameLevel], levelIndex: Int): Unit = setVisibleView(
      GameView(controller, levels, levelIndex, scene)
    )

    override def showMainMenu(): Unit = setVisibleView(
      MainMenuView(controller, controller.levelsCount, scene, controller.levelsCount == 0)
    )

    override def showLevelEditor(width: Int, height: Int): Unit = setVisibleView(
      LevelEditorView(controller, scene, "Menu", width, height)
    )

    override def showLevelEditor(level: EditorLevel): Unit = setVisibleView(
      LevelEditorView(controller, scene, "Menu", level)
    )

    override def showEditorMenuView(): Unit = setVisibleView(LevelEditorMenuView(controller, scene, "Menu"))

    private def setVisibleView(newVisibleView: ViewComponent[? <: Pane]) =
      Platform.runLater(() => {
        visibleView = newVisibleView; scene.root.value = visibleView.innerComponent
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