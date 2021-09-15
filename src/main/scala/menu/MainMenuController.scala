package it.unibo.pps.caw.menu

import java.io.File

/** The parent controller to the [[MainMenuController]].
  *
  * This trait is used for abstracting the functionalities which the [[MainMenuController]] needs from its parent controller so as
  * to offer its own functionalities. In this way, the [[MainMenuController]] is more modular because it can be reused in multiple
  * contexts with multiple parent controllers.
  */
trait ParentMainMenuController {

  /** Asks the parent controller to start a new game. It needs the file containing the level from which starting to play the game.
    *
    * @param levelFile
    *   the file containing the level from which starting to play the game
    */
  def startGame(levelFile: File): Unit

  /** Asks the parent controller to exit the application. */
  def exit(): Unit
}

/** The controller which manages the main menu part of the application.
  *
  * This controller is responsible for containing all functionalities which are proper to the main menu and that this last one
  * offers. It must be constructed through its companion object.
  */
trait MainMenuController {

  /** Starts a new game beginning from one of the default levels, which index is given. Then, the game will continue using the
    * level next to this one, and then the next one and so on until the last one is reached or the player exits the game.
    *
    * @param levelIndex
    *   the index of the default level from which starting the new game
    */
  def startGame(levelIndex: Int): Unit

  /** Starts a new game for playing the level contained in the given file. No other level will be played after this one, the only
    * option for the player will be to exit the game.
    *
    * @param levelFile
    *   the file containing the level to play in the new game
    */
  def startGame(levelFile: File): Unit

  /** Exits the application. */
  def exit(): Unit
}

/** Companion object to the [[MainMenuController]] trait, containing its factory method. */
object MainMenuController {

  /* Default implementation of the MainMenuController trait. */
  private class MainMenuControllerImpl(parentController: ParentMainMenuController, view: MainMenuView)
    extends MainMenuController {

    override def startGame(levelIndex: Int): Unit = parentController.startGame(new File(s"level$levelIndex.json"))

    override def startGame(levelFile: File): Unit = parentController.startGame(levelFile)

    override def exit(): Unit = parentController.exit()
  }

  /** Returns a new instance of the [[MainMenuController]] trait. It must receive the [[ParentMainMenuController]], which it
    * represents its parent controller which provides all functionalities which must be delegated to this type of controllers. It
    * must also receive the [[MainMenuView]] which will be called by and will call the returned [[MainMenuController]] instance.
    *
    * @param parentController
    *   the parent controller of the returned [[MainMenuController]]
    * @param view
    *   the [[GameView]] which will be called by and which will call the returned [[MainMenuController]] instance
    * @return
    *   a new [[MainMenuController]] instance
    */
  def apply(parentController: ParentMainMenuController, view: MainMenuView): MainMenuController =
    MainMenuControllerImpl(parentController, view)
}