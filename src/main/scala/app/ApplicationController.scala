package it.unibo.pps.caw.app

import it.unibo.pps.caw.game.ParentGameController
import it.unibo.pps.caw.menu.ParentMainMenuController
import it.unibo.pps.caw.settings.ParentSettingsController

import java.io.File

/** The controller of the main application.
  *
  * This controller represents the main, standalone application. Hence, it is parent to all other controllers created inside this
  * application and provides them the functionalities that are common between all controllers or that are "higher-level" ones,
  * such that no other controller should be responsible for them. It must be created through its companion object.
  */
trait ApplicationController extends ParentGameController with ParentMainMenuController with ParentSettingsController

/** Companion object to the [[ApplicationController]] trait, containing its factory method. */
object ApplicationController {

  /* Default implementation of the ApplicationController trait. */
  private class ApplicationControllerImpl(view: ApplicationView) extends ApplicationController {

    override def startGame(levelFile: File): Unit = view.showGame()

    override def exit(): Unit = sys.exit()

    override def backToMainMenu(): Unit = view.showMainMenu()

    override def back(): Unit = view.showMainMenu()

    override def openSettings(): Unit = view.showSettings()

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
