package it.unibo.pps.caw
package menu.view

import common.view.ViewComponent
import common.view.ViewComponent.AbstractViewComponent
import menu.controller.LevelSelectionController

import javafx.fxml.FXML
import javafx.scene.control.{Button, ScrollPane}
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import scalafx.scene.Scene

/** The "level selection" page on the main menu.
  *
  * This view component represents the "level selection" screen, which is part of the main menu. As such, its duty is to capture
  * all interactions with this specific part of the view and provide the expected functionalities such as allowing to choose the
  * [[it.unibo.pps.caw.common.model.Level]] which starting the game with through the
  * [[it.unibo.pps.caw.menu.controller.LevelSelectionController]]. It must be constructed through its companion object.
  */
trait LevelSelectionView extends ViewComponent[Pane]

/** Companion object for the [[LevelSelectionView]] trait, being a factory for new [[LevelSelectionView]] instances. */
object LevelSelectionView {

  /** Returns a new instance of the [[LevelSelectionView]] trait. It receives a ScalaFX [[scalafx.scene.Scene]] so as to draw and
    * display itself on it and the [[it.unibo.pps.caw.menu.controller.LevelSelectionController]] so the constructed view can
    * provide the services which should be accessible through itself.
    *
    * @param scene
    *   the ScalaFX [[scalafx.scene.Scene]] on which the constructed [[LevelSelectionView]] will draw and display itself
    * @param controller
    *   the [[it.unibo.pps.caw.menu.controller.LevelSelectionController]] associated to the created [[LevelSelectionView]]
    * @return
    *   a new [[LevelSelectionView]] instance
    */
  def apply(scene: Scene, controller: LevelSelectionController): LevelSelectionView =
    LevelSelectionViewImpl(controller)

  /* Default implementation of the LevelSelectionView trait. */
  private final class LevelSelectionViewImpl(controller: LevelSelectionController)
    extends AbstractViewComponent[Pane]("level_selection_page.fxml")
    with LevelSelectionView {
    @FXML
    var backButton: Button = _
    @FXML
    var buttonsPane: GridPane = _
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
