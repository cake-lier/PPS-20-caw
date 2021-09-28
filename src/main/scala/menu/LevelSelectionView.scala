package it.unibo.pps.caw.menu

import it.unibo.pps.caw.common.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.{SoundButton, ViewComponent}
import it.unibo.pps.caw.common.model.Level
import javafx.fxml.FXML
import javafx.scene.control.{Button, ScrollPane}
import javafx.scene.image.ImageView
import javafx.scene.layout.{GridPane, Pane, RowConstraints}
import scalafx.scene.Scene

import java.nio.file.{Files, Paths}

/** The "level selection" page on the main menu.
  *
  * This view component represents the "level selection" screen, which is part of the main menu. As such, its duty is to capture
  * all interactions with this specific part of the view and provide the expected functionalities such as allowing to choose the
  * [[Level]] which starting the game with through the [[LevelSelectionController]]. It must be
  * constructed through its companion object.
  */
trait LevelSelectionView extends ViewComponent[Pane]

/** Companion object for the [[LevelSelectionView]] trait, being a factory for new [[LevelSelection]] instances. */
object LevelSelectionView {

  /** Returns a new instance of the [[LevelSelectionView]] trait. It receives a ScalaFX's [[Scene]] so as to draw and display
    * itself on it and the [[LevelSelectionController]] so the constructed view can provide the services which should be
    * accessible through itself.
    *
    * @param scene
    *   the ScalaFX's [[Scene]] on which the constructed [[LevelSelectionView]] will draw and display itself
    * @param controller
    *   the [[LevelSelectionController]] associated to the created [[LevelSelectionView]]
    * @return
    *   a new [[LevelSelectionView]] instance
    */
  def apply(scene: Scene, controller: LevelSelectionController): LevelSelectionView =
    LevelSelectionViewImpl(scene, controller)

  /* Default implementation of the LevelSelectionView trait. */
  private final class LevelSelectionViewImpl(scene: Scene, controller: LevelSelectionController)
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

    backButton.setOnMouseClicked(_ => controller.goBack())
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
    (0 until controller.levelsCount).foreach(i => buttonsPane.add(LevelButton(i + 1, controller), i % 10, (i / 10.0).toInt))
  }
}
