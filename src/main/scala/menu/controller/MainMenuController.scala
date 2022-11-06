package it.unibo.pps.caw
package menu.controller

import common.storage.Settings
import menu.view.MainMenuView

/** The parent controller to the [[MainMenuController]].
  *
  * This trait is used for abstracting the functionalities which the [[MainMenuController]] needs from its parent controller so as
  * to offer its own functionalities. In this way, the [[MainMenuController]] is more modular because it can be reused in multiple
  * contexts with multiple parent controllers.
  */
trait ParentMainMenuController {

  /** Asks the parent controller to return the number of default [[it.unibo.pps.caw.common.model.Level]] available. */
  val levelsCount: Int

  /** Returns the current game settings. */
  def settings: Settings

  /** Asks the parent controller to start a new game. It needs the path of the file containing the level from which starting the
    * game.
    *
    * @param levelPath
    *   the path of the file containing the level from which starting to play the game
    */
  def startGame(levelPath: String): Unit

  /** Asks the parent controller to start a new game. It needs the index of the default level from which starting the game.
    *
    * @param levelIndex
    *   the index of the level from which starting to play the game
    */
  def startGame(levelIndex: Int): Unit

  /** Asks the parent controller to save volume settings.
    *
    * @param musicVolume
    *   the value of the music volume
    * @param soundsVolume
    *   the value of sound effects volume
    */
  def saveVolumeSettings(musicVolume: Double, soundsVolume: Double): Unit

  /** Asks the parent controller to start the level editor with a blank level of specified dimensions.
    *
    * @param width
    *   the width of the blank level
    * @param height
    *   the height of the blank level
    */
  def openEditor(width: Int, height: Int): Unit

  /** Asks the parent controller to start the level editor with a level loaded from file.
    *
    * @param levelPath
    *   the path to the level file
    */
  def openEditor(levelPath: String): Unit

  /** Asks the parent controller to show the main menu initial page. */
  def showMainMenu(): Unit

  /** Asks the parent controller to exit the application. */
  def exit(): Unit
}

/** The controller which manages the main menu part of the application.
  *
  * This controller is responsible for containing all functionalities which are proper to the main menu and that this last one
  * offers. It must be constructed through its companion object.
  */
trait MainMenuController extends LevelSelectionController with SettingsController with EditorMenuController {

  /** Returns the number of default [[it.unibo.pps.caw.common.model.Level]] available. */
  val levelsCount: Int

  /** Starts a new game for playing the level contained in the file with the given path. No other level will be played after this
    * one, the only option for the player will be to exit the game.
    *
    * @param levelPath
    *   the path to the file containing the [[it.unibo.pps.caw.common.model.Level]] to play in the new game
    */
  def startGame(levelPath: String): Unit

  /** Exits the application. */
  def exit(): Unit
}

/** Companion object to the [[MainMenuController]] trait, containing its factory method. */
object MainMenuController {

  /* Default implementation of the MainMenuController trait. */

  private class MainMenuControllerImpl(parentController: ParentMainMenuController)
    extends MainMenuController {

    override val levelsCount: Int = parentController.levelsCount

    override def solvedLevels: Set[Int] = parentController.settings.solvedLevels

    override def soundsVolume: Double = parentController.settings.soundsVolume

    override def musicVolume: Double = parentController.settings.musicVolume

    override def startGame(levelIndex: Int): Unit = parentController.startGame(levelIndex)

    override def startGame(levelPath: String): Unit = parentController.startGame(levelPath)

    override def exit(): Unit = parentController.exit()

    override def goBack(): Unit = parentController.showMainMenu()

    override def saveVolumeSettings(musicVolume: Double, soundsVolume: Double): Unit =
      parentController.saveVolumeSettings(musicVolume, soundsVolume)

    override def openEditor(width: Int, height: Int): Unit = parentController.openEditor(width, height)

    override def openEditor(levelPath: String): Unit = parentController.openEditor(levelPath)
  }

  /** Returns a new instance of the [[MainMenuController]] trait. It must receive the [[ParentMainMenuController]], which it
    * represents its parent controller which provides all functionalities which must be delegated to this type of controllers. It
    * must also receive the [[it.unibo.pps.caw.menu.view.MainMenuView]] which will be called by and will call the returned
    * [[MainMenuController]] instance.
    *
    * @param parentController
    *   the parent controller of the returned [[MainMenuController]]
    * @param view
    *   the [[it.unibo.pps.caw.game.view.GameView]] which will be called by and which will call the returned
    *   [[MainMenuController]] instance
    * @return
    *   a new [[MainMenuController]] instance
    */
  def apply(parentController: ParentMainMenuController, view: MainMenuView): MainMenuController =
    MainMenuControllerImpl(parentController)
}
