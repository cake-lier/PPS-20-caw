package it.unibo.pps.caw.app

import it.unibo.pps.caw.editor.controller.{ParentLevelEditorController, ParentLevelEditorMenuController}
import it.unibo.pps.caw.game.controller.ParentDefaultGameController
import it.unibo.pps.caw.menu.ParentMainMenuController

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Path, Paths}
import scala.jdk.StreamConverters
import scala.util.{Failure, Try}
import cats.implicits.given
import it.unibo.pps.caw.common.{LevelStorage, LevelParser, FileStorage, Settings, SettingsManager}
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.{BaseCell, PlayableCell}
import play.api.libs.json.Json

import concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.{ConcurrentHashMap, CopyOnWriteArraySet}
import scala.concurrent.Future
import scala.collection.mutable.Set
import scala.jdk.CollectionConverters.given

/** The controller of the main application.
  *
  * This controller represents the main, standalone application. Hence, it is parent to all other controllers created inside this
  * application and provides them the functionalities that are common between all controllers or that are "higher-level" ones,
  * such that no other controller should be responsible for them. It must be created through its companion object.
  */
trait ApplicationController extends ParentDefaultGameController with ParentMainMenuController with ParentLevelEditorController

/** Companion object to the [[ApplicationController]] trait, containing its factory method. */
object ApplicationController {

  /* Default implementation of the ApplicationController trait. */
  private class ApplicationControllerImpl(view: ApplicationView) extends ApplicationController {
    private val fileStorage = FileStorage()
    private val levelParser = LevelParser(fileStorage)
    private val levelStorage = LevelStorage(fileStorage, levelParser)
    private val settingsManager = SettingsManager(fileStorage)
    private var _settings: Settings = settingsManager.load().getOrElse(settingsManager.defaultSettings)
    private val futures: Set[Future[Try[Unit]]] = ConcurrentHashMap.newKeySet[Future[Try[Unit]]]().asScala

    override def closeGame(): Unit = view.showMainMenu()

    override def addSolvedLevel(index: Int): Unit = {
      _settings = Settings(settings.volumeMusic, settings.volumeSFX, settings.solvedLevels ++ Set(index))
      saveSettings(settings)
    }

    private val levelFiles: Seq[Level[BaseCell]] =
      (for {
        f <- fileStorage.loadResource("levels.json")
        s <- Json.parse(f).as[Seq[String]].map(n => fileStorage.loadResource(s"levels/$n")).sequence
        l <- s.map(levelParser.deserializeLevel(_)).sequence
      } yield l).getOrElse {
        view.showError("An error has occured, could not load level")
        Seq.empty
      }

    override val levelsCount: Int = levelFiles.length

    override def settings: Settings = _settings

    override def startGame(levelPath: String): Unit =
      levelStorage.loadLevel(levelPath).fold(_ => view.showError("An error has occurred, could not load level"), view.showGame(_))

    override def startGame(levelIndex: Int): Unit = view.showGame(levelFiles, levelIndex)

    private def saveSettings(s: Settings): Unit = {
      val future: Future[Try[Unit]] = Future(
        settingsManager.save(settings).recover(_ => view.showError("An error has occured, could not save settings"))
      )
      futures.add(future)
      future.onComplete(_ => futures.remove(future))
    }

    override def saveVolumeSettings(volumeMusic: Double, volumeSFX: Double): Unit = {
      _settings = Settings(volumeMusic, volumeSFX, settings.solvedLevels)
      saveSettings(settings)
    }

    override def startLevelEditor(width: Int, height: Int): Unit = view.showLevelEditor(width, height)

    override def startLevelEditor(levelPath: String): Unit =
      levelStorage
        .loadLevel(levelPath)
        .fold(_ => view.showError("An error has occured, could not load level"), view.showLevelEditor(_))

    override def showMainMenu(): Unit = view.showMainMenu()

    override def exit(): Unit = Future.sequence(futures).onComplete(_ => sys.exit())

    override def closeEditor(): Unit = view.showMainMenu()

    override def saveLevel(path: String, level: Level[BaseCell]): Unit = levelStorage.saveLevel(path, level)
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
