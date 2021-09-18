package it.unibo.pps.caw.app

import it.unibo.pps.caw.game.GameView
import it.unibo.pps.caw.menu.MainMenuView
import it.unibo.pps.caw.{AudioPlayer, Track, ViewComponent}
import javafx.scene.layout.Pane
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

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

  /** Shows the [[MainMenuView]] to the user, hiding the currently displayed view. */
  def showMainMenu(): Unit

  /** Shows the [[GameView]] to the player, hiding the currently displayed view. */
  def showGame(): Unit
}

/** Companion object for the [[ApplicationView]] trait, containing its factory method. */
object ApplicationView {

  /* Default implementation of the ApplicationView trait. */
  private class ApplicationViewImpl(stage: PrimaryStage) extends ApplicationView {
    private val controller: ApplicationController = ApplicationController(this)
    private val audioPlayer: AudioPlayer = AudioPlayer()
    private val scene: Scene = Scene(1080, 720)
    private var visibleView: ViewComponent[Pane] = MainMenuView(controller, audioPlayer, scene)

    stage.resizable = false
    stage.maximized = false
    stage.title = "Cells at Work"
    scene.root.value = visibleView.innerComponent
    stage.scene = scene
    stage.show()

    override def showGame(): Unit = {
      visibleView = GameView(controller, audioPlayer, scene)
      scene.root.value = visibleView.innerComponent
    }

    override def showMainMenu(): Unit = {
      visibleView = MainMenuView(controller, audioPlayer, scene)
      scene.root.value = visibleView.innerComponent
    }
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
