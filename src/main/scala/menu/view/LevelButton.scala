package it.unibo.pps.caw.menu.view

import it.unibo.pps.caw.common.view.ViewComponent
import it.unibo.pps.caw.common.view.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.menu.controller.LevelSelectionController

import javafx.scene.control.Button

/** A button on the "level selection" page in the main menu. */
trait LevelButton extends ViewComponent[Button]

/** Companion object used as a factory for new [[LevelButton]] instances. */
object LevelButton {

  /** Creates a level button component given the index of the level which the button is associated to and the
    * [[MainMenuController]] used for switching to the game in order to play the level associated to this button.
    *
    * @param levelIndex
    *   the index of the level which the button is associated to
    * @param controller
    *   the [[MainMenuController]] used for switching to the game
    */
  def apply(levelIndex: Int, controller: LevelSelectionController): ViewComponent[Button] =
    LevelButtonImpl(levelIndex, controller)

  /* Default implementation of the LevelButton trait. */
  private final class LevelButtonImpl(number: Int, controller: LevelSelectionController)
    extends AbstractViewComponent[Button]("level_button.fxml") {
    override val innerComponent: Button = loader.load[Button]

    innerComponent.setText(number.toString)
    innerComponent.setOnAction(_ => controller.startGame(number))

    if (controller.solvedLevels.contains(number)) {
      innerComponent.getStyleClass.add("completed")
    }
  }
}
