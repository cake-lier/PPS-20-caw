package it.unibo.pps.caw.app

import it.unibo.pps.caw.editor.view.EditorView
import it.unibo.pps.caw.game.view.GameView
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.BaseCell
import it.unibo.pps.caw.common.view.sounds.{AudioPlayer, AudioType}
import it.unibo.pps.caw.common.view.{StageResizer, ViewComponent}
import it.unibo.pps.caw.menu.view.MainMenuView
import javafx.application.Platform
import javafx.scene.layout.Pane
import scalafx.scene.control.Alert
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image

import scala.io.Source

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

  /** Shows the [[it.unibo.pps.caw.menu.view.MainMenuView]] to the user, hiding the currently displayed view. */
  def showMainMenu(): Unit

  /** Shows the [[it.unibo.pps.caw.game.view.GameView]] to the player, hiding the currently displayed view, for playing the given
    * [[it.unibo.pps.caw.common.model.Level]].
    *
    * @param level
    *   the [[it.unibo.pps.caw.common.model.Level]] which will be first displayed
    */
  def showGame(level: Level[BaseCell]): Unit

  /** Shows the [[it.unibo.pps.caw.game.view.GameView]] to the player, hiding the currently displayed view, for playing a default
    * [[it.unibo.pps.caw.common.model.Level]]. The level which will be played will be the one with the given index between the
    * given sequence of default levels. After playing that level, the player will be able to play all subsequent level in the
    * sequence, until its end.
    *
    * @param levels
    *   the sequence of default [[it.unibo.pps.caw.common.model.Level]] that will be used while playing the game
    * @param levelIndex
    *   the index of the [[it.unibo.pps.caw.common.model.Level]] which will be first displayed in the given sequence of levels
    */
  def showGame(levels: Seq[Level[BaseCell]], levelIndex: Int): Unit

  /** Shows the [[it.unibo.pps.caw.editor.view.EditorView]] to the player with an empty level, hiding the currently displayed
    * view.
    *
    * @param width
    *   the width of the empty [[it.unibo.pps.caw.common.model.Level]]
    * @param height
    *   the height of the empty [[it.unibo.pps.caw.common.model.Level]]
    */
  def showLevelEditor(width: Int, height: Int): Unit

  /** Shows the [[it.unibo.pps.caw.editor.view.EditorView]] to the player with an empty level, hiding the currently displayed
    * view.
    *
    * @param level
    *   the loaded level
    */
  def showLevelEditor(level: Level[BaseCell]): Unit
}

/** Companion object for the [[ApplicationView]] trait, containing its factory method. */
object ApplicationView {

  /* Default implementation of the ApplicationView trait. */
  private class ApplicationViewImpl(stage: PrimaryStage) extends ApplicationView {
    private val controller: ApplicationController = ApplicationController(this)
    private val audioPlayer: AudioPlayer = AudioPlayer(controller.settings.musicVolume, controller.settings.soundsVolume)
    StageResizer.resize(stage)
    private val scene: Scene = Scene(stage.width.value, stage.height.value)

    stage.resizable = false
    stage.maximized = false
    stage.title = "Cells at Work"
    stage.icons.add(Image(ClassLoader.getSystemResourceAsStream("imgs/app_icon.png")))
    scene.root.value = MainMenuView(controller, audioPlayer, controller.levelsCount, scene, controller.levelsCount == 0)
    stage.scene = scene
    stage.show()
    stage.setOnCloseRequest(_ => controller.exit())
    audioPlayer.setVolume(controller.settings.musicVolume, AudioType.Music)
    audioPlayer.setVolume(controller.settings.soundsVolume, AudioType.Sound)

    override def showError(message: String): Unit = Platform.runLater(() => Alert(Alert.AlertType.Error, message).showAndWait())

    override def showGame(level: Level[BaseCell]): Unit =
      Platform.runLater(() => scene.root.value = GameView(controller, audioPlayer, level, scene, backButtonText = "Menu"))

    override def showGame(levels: Seq[Level[BaseCell]], levelIndex: Int): Unit =
      Platform.runLater(() =>
        scene.root.value = GameView(controller, audioPlayer, levels, levelIndex, scene, backButtonText = "Menu")
      )

    override def showMainMenu(): Unit =
      Platform.runLater(() =>
        scene.root.value = MainMenuView(controller, audioPlayer, controller.levelsCount, scene, controller.levelsCount == 0)
      )

    override def showLevelEditor(width: Int, height: Int): Unit =
      Platform.runLater(() =>
        scene.root.value = EditorView(controller, scene, backButtonText = "Menu", audioPlayer, width, height)
      )

    override def showLevelEditor(level: Level[BaseCell]): Unit =
      Platform.runLater(() => scene.root.value = EditorView(controller, scene, backButtonText = "Menu", audioPlayer, level))
  }

  /** Returns a new instance of the [[ApplicationView]] trait. It needs the ScalaFX'state
    * [[scalafx.application.JFXApp3.PrimaryStage]] for creating a view for the application.
    *
    * @param stage
    *   the ScalaFX'state [[scalafx.application.JFXApp3.PrimaryStage]] used for creating a view for the application
    * @return
    *   a new [[ApplicationView]] instance
    */
  def apply(stage: PrimaryStage): ApplicationView = ApplicationViewImpl(stage)
}
