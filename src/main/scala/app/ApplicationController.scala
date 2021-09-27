package it.unibo.pps.caw.app

import it.unibo.pps.caw.game.model.{BaseCell, Level}
import it.unibo.pps.caw.menu.ParentMainMenuController
import cats.implicits.given
import play.api.libs.json.Json
import it.unibo.pps.caw.game.controller.{Deserializer, ParentGameController}
import it.unibo.pps.caw.Loader

/** The controller of the main application.
  *
  * This controller represents the main, standalone application. Hence, it is parent to all other controllers created inside this
  * application and provides them the functionalities that are common between all controllers or that are "higher-level" ones,
  * such that no other controller should be responsible for them. It must be created through its companion object.
  */
trait ApplicationController extends ParentGameController with ParentMainMenuController

/** Companion object to the [[ApplicationController]] trait, containing its factory method. */
object ApplicationController {

  /* Default implementation of the ApplicationController trait. */
  private class ApplicationControllerImpl(view: ApplicationView) extends ApplicationController {

    override def startGame(levelPath: String): Unit =
      (for {
        f <- Loader.load(levelPath)
        l <- Deserializer.deserializeLevel(f)
      } yield l).fold(_ => view.showError("An error has occured, could not load level"), view.showGame(_))

    private val levelFiles: Seq[Level[BaseCell]] =
      (for {
        f <- Loader.load("levels.json")
        s <- Json.parse(f).as[Seq[String]].map(n => Loader.load(s"levels/$n")).sequence
        l <- s.map(Deserializer.deserializeLevel(_)).sequence
      } yield l).getOrElse {
        view.showError("An error has occured, could not load level")
        Seq.empty
      }

    override val levelsCount: Int = levelFiles.length

    override def startGame(levelIndex: Int): Unit = view.showGame(levelFiles, levelIndex)

    override def exit(): Unit = sys.exit()

    override def goBack(): Unit = view.showMainMenu()
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
