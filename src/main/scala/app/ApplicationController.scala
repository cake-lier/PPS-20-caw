package it.unibo.pps.caw.app

import it.unibo.pps.caw.game.model.{BaseCell, Level as GameLevel}
import it.unibo.pps.caw.editor.controller.{ParentLevelEditorController, Serializer}
import it.unibo.pps.caw.game.controller.{Deserializer, ParentGameController}
import it.unibo.pps.caw.editor.view.ParentLevelEditorMenuController
import it.unibo.pps.caw.editor.model.Level as EditorLevel
import it.unibo.pps.caw.menu.ParentMainMenuController

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Path, Paths}
import scala.io.Source
import scala.jdk.StreamConverters
import scala.util.{Failure, Try, Using}
import cats.implicits.given
import it.unibo.pps.caw.common.{LevelManager, Loader}
import play.api.libs.json.Json
import javafx.scene.layout.AnchorPane

/** The controller of the main application.
  *
  * This controller represents the main, standalone application. Hence, it is parent to all other controllers created inside this
  * application and provides them the functionalities that are common between all controllers or that are "higher-level" ones,
  * such that no other controller should be responsible for them. It must be created through its companion object.
  */

trait ApplicationController
  extends ParentGameController
  with ParentMainMenuController
  with ParentLevelEditorController
  with ParentLevelEditorMenuController

/** Companion object to the [[ApplicationController]] trait, containing its factory method. */
object ApplicationController {

  /* Default implementation of the ApplicationController trait. */
  private class ApplicationControllerImpl(view: ApplicationView) extends ApplicationController {

    override def startGame(levelPath: String): Unit =
      (for {
        f <- Loader.load(levelPath)
        l <- Deserializer.deserializeLevel(f)
      } yield l).fold(_ => view.showError("An error has occured, could not load level"), view.showGame(_))

    private val levelFiles: Seq[GameLevel[BaseCell]] =
      (for {
        f <- Loader.load("levels.json")
        l <- Json.parse(f).as[Seq[String]].map(n => LevelManager.load(s"levels/$n")).sequence
      } yield l).getOrElse {
        view.showError("An error has occured, could not load level")
        Seq.empty
      }

    override def startGame(levelIndex: Int): Unit = view.showGame(levelFiles, levelIndex)

    override val levelsCount: Int = levelFiles.length

    override def exit(): Unit = sys.exit()

    override def goBack(): Unit = view.showMainMenu()

    override def backToLevelEditorMenu(): Unit = view.showEditorMenuView()

    override def closeEditor(): Unit = view.showMainMenu()

    override def closeLevelEditorMenu(): Unit = view.showMainMenu()

    override def openLevelMenuView(): Unit = view.showEditorMenuView()

    override def saveLevel(path: String, level: EditorLevel): Unit =
      LevelManager.writeLevel(path, level)

    override def openLevelEditor(width: Int, height: Int): Unit =
      view.showLevelEditor(width, height)

    override def openLevelEditor(path: String): Unit =
      LevelManager.loadLevelLevelEditor(path).foreach(view.showLevelEditor)
  }

  /** Returns a new instance of the [[ApplicationController]] trait. It must receive the [[ApplicationView]] which will be called
    * by and which will call the returned [[ApplicationController]] instance.
    *
    * @param view
    *   the [[ApplicationView]] which will be called by and which will call the returned [[ApplicationController]] instance
    * @return
    *   a new [[ApplicationController]] instance
    */
  def apply(view: ApplicationView): ApplicationController = ApplicationControllerImpl(view)
}
