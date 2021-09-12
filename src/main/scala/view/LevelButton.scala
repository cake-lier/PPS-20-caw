package it.unibo.pps.caw
package view

import view.ViewComponent.AbstractViewComponent

import javafx.fxml.FXML
import javafx.scene.control.Button

/** Factory for new [[LevelButton]] instance. */
object LevelButton {

  /** Creates a level button component.
   *
   *  @param number Used to represent the level in the selection menu.
   */
  def apply(number: Int): ViewComponent[Button] = new LevelButtonImpl(number)

  private final class LevelButtonImpl(number: Int) extends AbstractViewComponent[Button]("level_button.fxml") {
    @FXML
    var levelButton: Button = _

    override val innerComponent: Button = loader.load[Button]

    levelButton.setText(number.toString)

    levelButton.setOnMouseClicked(_ => println("Level " + number + " selected."))
  }
}
