package it.unibo.pps.caw
package view

import app.{ApplicationController, ApplicationView}
import common.view.ViewComponent
import common.view.sounds.{AudioPlayer, AudioType}
import common.model.Level
import common.model.cell.BaseCell
import editor.view.EditorView
import game.view.GameView
import menu.MainMenuView
import view.DummyAudioPlayer

import javafx.application.Platform
import javafx.scene.layout.Pane
import javafx.stage.{Screen, Stage}
import javafx.geometry.Rectangle2D
import scalafx.scene.control.Alert
import scalafx.scene.Scene

object TestApplicationView {
  object TestStageResizer {
    def resize(stage: Stage): Unit = {
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
    }
  }
  /* Default implementation of the ApplicationView trait. */
  private class ApplicationViewImpl(stage: Stage) extends ApplicationView {
    private val controller: ApplicationController = ApplicationController(this)
    private val audioPlayer: AudioPlayer = DummyAudioPlayer()
    TestStageResizer.resize(stage)
    private val scene: Scene = Scene(stage.getWidth, stage.getHeight)

    stage.setResizable(false)
    stage.setMaximized(false)
    stage.setTitle("Cells at Work")
    scene.root.value = MainMenuView(controller, audioPlayer, controller.levelsCount, scene, controller.levelsCount == 0)
    stage.setScene(scene.delegate)
    stage.show()
    stage.setOnCloseRequest(_ => controller.exit())
    audioPlayer.setVolume(controller.settings.musicVolume, AudioType.Music)
    audioPlayer.setVolume(controller.settings.soundVolume, AudioType.Sound)

    override def showError(message: String): Unit = Platform.runLater(() => Alert(Alert.AlertType.Error, message).showAndWait())

    override def showGame(level: Level[BaseCell]) = show(GameView(controller, audioPlayer, level, scene))

    override def showGame(levels: Seq[Level[BaseCell]], levelIndex: Int): Unit =
      show(GameView(controller, audioPlayer, levels, levelIndex, scene))

    override def showMainMenu(): Unit =
      show(MainMenuView(controller, audioPlayer, controller.levelsCount, scene, controller.levelsCount == 0))

    override def showLevelEditor(width: Int, height: Int): Unit =
      show(EditorView(controller, scene, "Menu", audioPlayer, width, height))

    override def showLevelEditor(level: Level[BaseCell]): Unit =
      show(EditorView(controller, scene, "Menu", audioPlayer, level))

    def show(view: ViewComponent[? <: Pane]): ViewComponent[? <: Pane] = {
      scene.root.value = view
      view
    }
  }

  /** Returns a new instance of the [[ApplicationView]] trait. It needs the ScalaFX'state [[PrimaryStage]] for creating a view for
    * the application.
    *
    * @param stage
    *   the ScalaFX'state [[PrimaryStage]] used for creating a view for the application
    * @return
    *   a new [[ApplicationView]] instance
    */
  def apply(stage: Stage): ApplicationView = ApplicationViewImpl(stage)
}
