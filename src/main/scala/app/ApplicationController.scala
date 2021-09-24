package it.unibo.pps.caw.app

import it.unibo.pps.caw.editor.controller.{ParentLevelEditorController, Serializer}
import it.unibo.pps.caw.editor.view.ParentLevelEditorMenuController
import it.unibo.pps.caw.game.controller.ParentGameController
import it.unibo.pps.caw.game.model.Level as GameLevel
import it.unibo.pps.caw.menu.ParentMainMenuController

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Path, Paths}
import scala.io.Source
import scala.jdk.StreamConverters
import scala.util.{Failure, Try, Using}
import cats.implicits
import it.unibo.pps.caw.editor.model.Level as EditorLevel
import javafx.scene.layout.AnchorPane
import it.unibo.pps.caw.game.controller.ParentGameController
import it.unibo.pps.caw.LevelLoader

import scala.jdk.StreamConverters.given
import cats.implicits.given
import it.unibo.pps.caw.game.LevelManager

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

    override def startGame(levelPath: Path): Unit =
      LevelManager.load(levelPath).fold(_ => view.showError("An error has occured, could not load level"), view.showGame(_))

    private val levelFiles: Seq[GameLevel] =
      Files
        .list(Paths.get(ClassLoader.getSystemResource("levels/").toURI))
        .toScala(Seq)
        .filter(_.getFileName.toString.endsWith(".json"))
        .map(LevelManager.load(_))
        .sequence
        .getOrElse {
          view.showError("An error has occured, could not load level")
          Seq()
        }

    override val levelsCount: Int = levelFiles.length

    override def startGame(levelIndex: Int): Unit = view.showGame(levelFiles, levelIndex)

    override def exit(): Unit = sys.exit()

    override def goBack(): Unit = view.showMainMenu()

    override def backToLevelEditorMenu(): Unit = view.showEditorMenuView()

    override def closeEditor(): Unit = view.showMainMenu()

    override def closeLevelEditorMenu(): Unit = view.showMainMenu()

    override def openLevelMenuView(): Unit = view.showEditorMenuView()

    override def saveLevel(file: File, level: EditorLevel): Unit =
      Serializer.serializeLevel(level).map(s => Using(PrintWriter(file)) { _.write(s) })

    override def openLevelEditor(width: Int, height: Int): Unit =
      view.showLevelEditor(width, height)

    override def openLevelEditor(level: File): Unit =
      LevelManager.loadLevelLevelEditor(level).foreach(view.showLevelEditor)
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
