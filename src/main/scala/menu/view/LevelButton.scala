package it.unibo.pps.caw
package menu.view

import common.view.ViewComponent
import common.view.ViewComponent.AbstractViewComponent
import menu.controller.LevelSelectionController

import javafx.scene.control.Button

/** A button on the "level selection" page in the main menu. */
private trait LevelButton extends ViewComponent[Button]

/** Companion object used as a factory for new [[LevelButton]] instances. */
private object LevelButton {

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
