package it.unibo.pps.caw
package app

import app.{ApplicationController, ApplicationView, DummyAudioPlayer}
import common.model.Level
import common.model.cell.BaseCell
import common.view.ViewComponent
import common.view.sounds.{AudioPlayer, AudioType}
import editor.view.EditorView
import game.view.GameView
import menu.view.MainMenuView

import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.layout.Pane
import javafx.stage.{Screen, Stage}
import scalafx.scene.control.Alert
import scalafx.scene.Scene

object TestApplicationView {

  /* Test implementation of the ApplicationView trait. */
  private class TestApplicationViewImpl(stage: Stage) extends ApplicationView {
    private val controller: ApplicationController = ApplicationController(this)
    private val audioPlayer: AudioPlayer = DummyAudioPlayer()
    val heightRatio: Int = 9
    val widthRatio: Int = 16
    val screenBounds: Rectangle2D = Screen.getPrimary.getVisualBounds
    stage.setX(screenBounds.getMinX)
    stage.setY(screenBounds.getMinY)
    stage.centerOnScreen()
    val unitaryHeight: Double = screenBounds.getHeight / heightRatio
    val unitaryWidth: Double = screenBounds.getWidth / widthRatio
    if (screenBounds.getWidth < unitaryHeight * widthRatio) {
      stage.setWidth(screenBounds.getWidth)
      stage.setHeight(unitaryWidth * heightRatio)
    } else if (screenBounds.getHeight < unitaryWidth * heightRatio) {
      stage.setHeight(screenBounds.getHeight)
      stage.setWidth(unitaryHeight * widthRatio)
    } else {
      stage.setHeight(screenBounds.getHeight)
      stage.setWidth(screenBounds.getWidth)
    }

    private val scene: Scene = Scene(stage.getWidth, stage.getHeight)

    stage.setResizable(false)
    stage.setMaximized(false)
    stage.setTitle("Cells at Work")
    scene.root.value = MainMenuView(controller, audioPlayer, scene)
    stage.setScene(scene.delegate)
    stage.show()
    stage.setOnCloseRequest(_ => controller.exit())
    audioPlayer.setVolume(controller.settings.musicVolume, AudioType.Music)
    audioPlayer.setVolume(controller.settings.soundsVolume, AudioType.Sound)

    override def showError(message: String): Unit = Platform.runLater(() => Alert(Alert.AlertType.Error, message).showAndWait())

    override def showGame(level: Level[BaseCell]): Unit = scene.root.value =
      GameView(controller, audioPlayer, level, scene, backButtonText = "Menu")

    override def showGame(levels: Seq[Level[BaseCell]], levelIndex: Int): Unit =
      scene.root.value = GameView(controller, audioPlayer, levels, levelIndex, scene, backButtonText = "Menu")

    override def showMainMenu(): Unit =
      scene.root.value = MainMenuView(controller, audioPlayer, scene)

    override def showLevelEditor(width: Int, height: Int): Unit =
      scene.root.value = EditorView(controller, scene, backButtonText = "Menu", audioPlayer, width, height)

    override def showLevelEditor(level: Level[BaseCell]): Unit =
      scene.root.value = EditorView(controller, scene, backButtonText = "Menu", audioPlayer, level)

  }

  /** Returns a new instance of the [[ApplicationView]] trait. It needs the ScalaFX [[PrimaryStage]] for creating a view for the
    * application.
    *
    * @param stage
    *   the ScalaFX [[PrimaryStage]] used for creating a view for the application
    * @return
    *   a new [[ApplicationView]] instance
    */
  def apply(stage: Stage): ApplicationView = TestApplicationViewImpl(stage)
}
