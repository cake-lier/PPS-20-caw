package it.unibo.pps.caw.game

/** The parent controller to the [[GameController]].
  *
  * This trait is used for abstracting the functionalities which the [[GameController]] needs from its parent controller so as to
  * offer its own functionalities. In this way, the [[GameController]] is more modular because it can be reused in multiple
  * contexts with multiple parent controllers.
  */
trait ParentGameController {

  /** Asks the parent controller to go back to the main menu. */
  def backToMainMenu(): Unit
}

/** The controller which manages the game part of an application.
  *
  * This controller is the mediator between the [[GameView]] and the [[GameModel]]. It intercepts the inputs of the player while
  * playing the game coming from the [[GameView]] and updates the [[GameModel]] accordingly. Then, it shows the updated
  * [[GameModel]] to the [[GameView]] so as to notify the user of the happened changes. It must be constructed through its
  * companion object.
  */
trait GameController {

  /** Goes back to the main menu. */
  def backToMainMenu(): Unit
}

/** Companion object of the [[GameController]] trait, containing its factory method. */
object GameController {

  /* Default implementation of the GameController trait. */
  private class GameControllerImpl(parentController: ParentGameController, view: GameView) extends GameController {

    override def backToMainMenu(): Unit = parentController.backToMainMenu()
  }

  /** Returns a new instance of the [[GameController]] trait. It must receive the [[ParentGameController]], which it represents
    * its parent controller which provides all functionalities which must be delegated to this type of controllers. It must also
    * receive the [[GameView]] which will be called by and will call the returned [[GameController]] instance.
    *
    * @param parentController
    *   the parent controller of the returned [[GameController]]
    * @param view
    *   the [[GameView]] which will be called by and which will call the returned [[GameController]] instance
    * @return
    *   a new [[GameController]] instance
    */
  def apply(parentController: ParentGameController, view: GameView): GameController = GameControllerImpl(parentController, view)
}
