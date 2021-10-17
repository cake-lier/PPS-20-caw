package it.unibo.pps.caw.app

import it.unibo.pps.caw.editor.controller.ParentEditorController
import it.unibo.pps.caw.game.controller.ParentDefaultGameController

import scala.util.Try
import cats.implicits.given
import it.unibo.pps.caw.common.LevelParser
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.BaseCell
import it.unibo.pps.caw.common.storage.{FileStorage, LevelStorage, Settings, SettingsStorage}
import it.unibo.pps.caw.menu.controller.ParentMainMenuController
import play.api.libs.json.Json

import concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.Future
import scala.collection.mutable.Set
import scala.jdk.CollectionConverters.given

/** The controller of the main application.
  *
  * This controller represents the main, standalone application. Hence, it is parent to all other controllers created inside this
  * application and provides them the functionalities that are common between all controllers or that are "higher-level" ones,
  * such that no other controller should be responsible for them. It must be created through its companion object.
  */
trait ApplicationController extends ParentDefaultGameController with ParentMainMenuController with ParentEditorController

/** Companion object to the [[ApplicationController]] trait, containing its factory method. */
object ApplicationController {

  /* Default implementation of the ApplicationController trait. */
  private class ApplicationControllerImpl(view: ApplicationView) extends ApplicationController {
    override val fileStorage = FileStorage()

    private val levelParser = LevelParser(fileStorage)
    private val levelStorage = LevelStorage(fileStorage, levelParser)
    private val settingsStorage = SettingsStorage(fileStorage)
    private var _settings: Settings = settingsStorage.load().getOrElse(settingsStorage.defaultSettings)
    private val futures: Set[Future[Try[Unit]]] = ConcurrentHashMap.newKeySet[Future[Try[Unit]]]().asScala

    override def closeGame(): Unit = view.showMainMenu()

    override def addSolvedLevel(index: Int): Unit = {
      _settings = _settings.copy(solvedLevels = _settings.solvedLevels + index)
      saveSettings(_settings)
    }

    private val levelFiles: Seq[Level[BaseCell]] =
      (for {
        f <- fileStorage.loadResource(path = "levels.json")
        s <- Json.parse(f).as[Seq[String]].map(n => fileStorage.loadResource(path = s"levels/$n")).sequence
        l <- s.map(levelParser.deserializeLevel(_)).sequence
      } yield l).getOrElse {
        view.showError(message = ApplicationControllerError.CouldNotLoadLevel.message)
        Seq.empty
      }

    override val levelsCount: Int = levelFiles.length

    override def settings: Settings = _settings

    override def startGame(levelPath: String): Unit =
      levelStorage
        .loadLevel(levelPath)
        .fold(_ => view.showError(message = ApplicationControllerError.CouldNotLoadLevel.message), view.showGame(_))

    override def startGame(levelIndex: Int): Unit = view.showGame(levelFiles, levelIndex)

    private def saveSettings(s: Settings): Unit = {
      val future: Future[Try[Unit]] = Future(
        settingsStorage.save(settings).recover(_ => view.showError(ApplicationControllerError.CouldNotSaveSettings.message))
      )
      futures.add(future)
      future.onComplete(_ => futures.remove(future))
    }

    override def saveVolumeSettings(musicVolume: Double, soundsVolume: Double): Unit = {
      _settings = _settings.copy(musicVolume = musicVolume, soundsVolume = soundsVolume)
      saveSettings(_settings)
    }

    override def openEditor(width: Int, height: Int): Unit = view.showLevelEditor(width, height)

    override def openEditor(levelPath: String): Unit =
      levelStorage
        .loadLevel(levelPath)
        .fold(_ => view.showError(message = ApplicationControllerError.CouldNotLoadLevel.message), view.showLevelEditor(_))

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
