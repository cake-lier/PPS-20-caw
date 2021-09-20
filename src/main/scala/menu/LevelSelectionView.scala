package it.unibo.pps.caw.menu

import it.unibo.pps.caw.{SoundButton, ViewComponent}
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import javafx.fxml.FXML
import javafx.scene.control.{Button, ScrollPane}
import javafx.scene.image.ImageView
import javafx.scene.layout.{GridPane, Pane, RowConstraints}
import scalafx.scene.Scene

import java.nio.file.{Files, Paths}

/** The "level selection" page on the main menu.
  *
  * This view component represents the "level selection" screen, which is part of the main menu. As such, its duty is to capture
  * all interactions with this specific part of the view and provide the expected functionalities, the choice of the level which
  * starting the game with, through the general [[MainMenuController]]. It must be constructed through its companion object.
  */
trait LevelSelectionView extends ViewComponent[Pane]

/** Companion object for the [[LevelSelectionView]] trait, being a factory for new [[LevelSelection]] instances. */
object LevelSelectionView {

  /** Returns a new instance of the [[LevelSelectionView]] trait. It receives a ScalaFX's [[Scene]] so as to draw and display
    * itself on it. It receives the [[MainMenuView]] which is the parent of the constructed view, in order to notify it that the
    * user wants to go back to the main menu and its main page is to display instead of this specific page. At last, it receives
    * the [[MainMenuController]] associated to the parent [[MainMenuView]] so the constructed view can provide the services which
    * should be accessible through itself.
    *
    * @param scene
    *   the ScalaFX's [[Scene]] on which the constructed [[LevelSelectionView]] will draw and display itself
    * @param parentView
    *   the parent [[MainMenuView]] of the constructed view
    * @param controller
    *   the [[MainMenuController]] associated to the given [[MainMenuView]]
    * @return
    *   a new [[LevelSelectionView]] instance
    */
  def apply(scene: Scene, parentView: MainMenuView, controller: MainMenuController): LevelSelectionView =
    LevelSelectionViewImpl(scene, parentView, controller)

  /* Default implementation of the LevelSelectionView trait. */
  private final class LevelSelectionViewImpl(scene: Scene, parentView: MainMenuView, controller: MainMenuController)
      extends AbstractViewComponent[Pane]("level_selection_page.fxml")
      with LevelSelectionView {
    @FXML
    var backButton: SoundButton = _
    @FXML
    var buttonsPane: GridPane = _
    @FXML
    var buttonsPaneContainer: ScrollPane = _
    @FXML
    var arrowsIcon: ImageView = _

    override val innerComponent: Pane = loader.load[GridPane]

    backButton.setOnMouseClicked(_ => scene.root.value = parentView)

    val constraints: RowConstraints = RowConstraints()
    val rows: Int = (controller.levelsCount / 10.0).ceil.toInt
    val minTableRows: Int = 4
    val tableRows: Int = Math.max(rows, minTableRows)
    val maxVisibleTableRows: Int = 5
    if (rows > maxVisibleTableRows) {
      arrowsIcon.setVisible(true)
    }
    constraints.setPercentHeight(100.0 / tableRows)
    (0 until tableRows).foreach(_ => buttonsPane.getRowConstraints.add(constraints))
    (0 until controller.levelsCount).foreach { i =>
      buttonsPane.add(LevelButton(i + 1, controller).innerComponent, i % 10, (i / 10.0).toInt)
    }
  }
}
