package it.unibo.pps.caw.app

import it.unibo.pps.caw.game.{Deserializer, ParentGameController}
import it.unibo.pps.caw.game.model.{Level, PlayableArea, Position}
import it.unibo.pps.caw.menu.ParentMainMenuController

import java.io.File
import java.nio.file.{Files, Path, Paths}
import scala.io.Source
import scala.jdk.StreamConverters.given
import scala.util.{Failure, Using}

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
    override val levelFiles: Seq[Path] = Files
      .list(Paths.get(ClassLoader.getSystemResource("levels/").toURI))
      .toScala(Seq)
      .filter(_.getFileName.toString.endsWith(".json"))

    override def startGame(levelPath: Path): Unit = view.showGame(levelPath)

    override def startGame(levelIndex: Int): Unit = view.showGame(levelIndex)

    override def loadLevel(path: Path): Level =
      Using(Source.fromFile(path.toFile))(_.getLines.mkString)
        .flatMap(Deserializer.deserializeLevel(_))
        .getOrElse {
          view.showError("There was an error loading the level")
          Level(10, 10, Set(), PlayableArea(Position(0, 0), 10, 10))
        }

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
