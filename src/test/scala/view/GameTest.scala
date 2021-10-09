package it.unibo.pps.caw
package view

import common.view.*

import javafx.scene.control.Button
import javafx.scene.Node
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.{Assertions, BeforeAll, Test, TestInstance}
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions as FxAssertions
import org.testfx.framework.junit5.{ApplicationExtension, Start}
import org.testfx.util.WaitForAsyncUtils

import java.util.concurrent.TimeUnit

@ExtendWith(Array(classOf[ApplicationExtension]))
@TestInstance(Lifecycle.PER_CLASS)
/** Test for [[it.unibo.pps.caw.menu.LevelSelectionView]] and [[it.unibo.pps.caw.game.view.GameView]] */
class GameTest {

  @BeforeAll
  def beforeAll(): Unit = {
    System.setProperty("testfx.robot", "glass")
    System.setProperty("testfx.headless", "true")
    System.setProperty("java.awt.headless", "true")
    System.setProperty("prism.order", "sw")
    System.setProperty("prism.text", "t2k")
  }

  private var width: Double = 0
  private var height: Double = 0

  @Start
  def start(stage: Stage): Unit = {
    TestApplicationView(stage)
    width = stage.getWidth
    height = stage.getHeight
  }

  import ViewTestHelper._

  @Test
  def testMainMenuButtonIsPresent(robot: FxRobot): Unit = {
    val playButton: Button = getButtonById("playButton")
    FxAssertions.assertThat(playButton).hasText("Play")
    FxAssertions.assertThat(playButton).isVisible
    FxAssertions.assertThat(playButton).isEnabled
  }

  @Test
  def testLevelSelectionMenuHasAllControls(robot: FxRobot): Unit = {
    // when player clicks the play button
    clickOnPlayButton(robot)

    // levels should be present
    val levels = robot
      .lookup((b: Button) => b.getText.matches("\\d+"))
      .queryAll[Button]()
    Assertions.assertEquals(24, levels.size())
    // the back button should be present
    val backButton = getButtonById("backButton")
    FxAssertions.assertThat(backButton).hasText("Back")
    FxAssertions.assertThat(backButton).isVisible
    FxAssertions.assertThat(backButton).isEnabled
  }

  @Test
  def testBackButtonOfLevelSelectionMenu(robot: FxRobot): Unit = {
    // when player clicks the play button
    clickOnPlayButton(robot)
    // and then clicks the back button
    clickOnBackButton(robot)
    // it should return back to main menu
    testMainMenuButtonIsPresent(robot)
  }

  @Test
  def testGameViewHasAllControls(robot: FxRobot): Unit = {
    // when player clicks on the 1st level
    clickOnPlayButton(robot)
    clickOnLevel(robot)

    // the game board should be present
    robot
      .lookup(_.isInstanceOf[GridPane])
      .query[GridPane]()
    // the play button should be present
    val playSimulationButton = getButtonById("playSimulationButton")
    FxAssertions.assertThat(playSimulationButton).hasText("Play")
    FxAssertions.assertThat(playSimulationButton).isVisible
    FxAssertions.assertThat(playSimulationButton).isEnabled
    // the step button should be present
    val stepSimulationButton: Button = getButtonById("stepSimulationButton")
    FxAssertions.assertThat(stepSimulationButton).hasText("Step")
    FxAssertions.assertThat(stepSimulationButton).isVisible
    FxAssertions.assertThat(stepSimulationButton).isEnabled
    // the reset button should be present but invisible
    val resetButton: Button = getButtonById("resetButton")
    FxAssertions.assertThat(resetButton).hasText("Reset")
    FxAssertions.assertThat(resetButton).isInvisible
    FxAssertions.assertThat(resetButton).isEnabled
    // the next button should be present but invisible
    val nextButton: Button = getButtonById("nextButton")
    FxAssertions.assertThat(nextButton).hasText("Next")
    FxAssertions.assertThat(nextButton).isInvisible
    FxAssertions.assertThat(nextButton).isEnabled
    // the back button should be present
    val backToMenuButton: Button = getButtonById("backToMenuButton")
    FxAssertions.assertThat(backToMenuButton).hasText("Menu")
    FxAssertions.assertThat(backToMenuButton).isVisible
    FxAssertions.assertThat(backToMenuButton).isEnabled
  }

  @Test
  def testGameCellDragAndDropDuringSetupPhase(robot: FxRobot): Unit = {
    // during the setup phase
    clickOnPlayButton(robot)
    clickOnLevel(robot)

    // the mover cell can be dropped inside the playable area
    val moverCell = getImageViewByImage(CellImage.MoverRight.image)
    robot
      .drag(moverCell, MouseButton.PRIMARY)
      .dropBy(0, height * 0.2)
      .drop()
    // the mover cell cannot be dropped outside the playable area
    robot
      .drag(moverCell, MouseButton.PRIMARY)
      .dropBy(width * 0.2, 0)
    // the enemy cell cannot be dragged
    robot
      .drag(getImageViewByImage(CellImage.Enemy.image), MouseButton.PRIMARY)
      .dropBy(0, -height * 0.2)
  }

  @Test
  def testMoverCellHasMovedDuringSimulation(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    val moverCellBeforePlay = getImageViewByImage(CellImage.MoverRight.image)
    val moverCellPositionBeforePlay = (GridPane.getColumnIndex(moverCellBeforePlay), GridPane.getRowIndex(moverCellBeforePlay))

    clickOnPlaySimulationButton(robot) // Play
    clickOnPlaySimulationButton(robot) // Pause

    val moverCellAfterPlay = getImageViewByImage(CellImage.MoverRight.image)
    val moverCellPositionAfterPlay = (GridPane.getColumnIndex(moverCellAfterPlay), GridPane.getRowIndex(moverCellAfterPlay))
    // the mover cell should have moved
    Assertions.assertNotEquals(moverCellPositionAfterPlay, moverCellPositionBeforePlay)
  }

  @Test
  def testGameControlsAfterPlayButtonIsClicked(robot: FxRobot): Unit = {
    //after starting the simulation by clicking the play button
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    clickOnPlaySimulationButton(robot)

    // the play simulation button text should change from 'Play' to 'Pause'")
    FxAssertions.assertThat(getButtonById("playSimulationButton")).hasText("Pause")
    // the step button should be disabled
    FxAssertions.assertThat(getButtonById("stepSimulationButton")).isDisabled
    // the reset button should be visible
    FxAssertions.assertThat(getButtonById("resetButton")).isVisible
  }

  @Test
  def testGameControlsAfterPauseButtonIsClicked(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    clickOnPlaySimulationButton(robot)
    clickOnPauseSimulationButton(robot)

    // the play simutlation button text should change from 'Pause' to 'Play'") {
    FxAssertions.assertThat(getButtonById("playSimulationButton")).hasText("Play")
    // the step button should be enabled
    FxAssertions.assertThat(getButtonById("stepSimulationButton")).isEnabled
  }

  @Test
  def testGameCellDragAndDropWhileSimulationIsPaused(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    clickOnPlaySimulationButton(robot)
    clickOnPauseSimulationButton(robot)

    // no game cells should be movable
    robot
      .drag(getImageViewByImage(CellImage.MoverRight.image), MouseButton.PRIMARY)
      .dropBy(0, -height * 0.2)
    robot
      .drag(getImageViewByImage(CellImage.Enemy.image), MouseButton.PRIMARY)
      .dropBy(0, -height * 0.2)
  }

  @Test
  def testResetButton(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    val moverCellBeforePlay = getImageViewByImage(CellImage.MoverRight.image)
    val moverCellPositionBeforePlay = (GridPane.getColumnIndex(moverCellBeforePlay), GridPane.getRowIndex(moverCellBeforePlay))
    clickOnPlaySimulationButton(robot)

    clickOnResetSimulationButton(robot)

    val moverCellAfterReset = getImageViewByImage(CellImage.MoverRight.image)
    val moverCellPositionAfterReset = (GridPane.getColumnIndex(moverCellAfterReset), GridPane.getRowIndex(moverCellAfterReset))

    // after reset, the mover cell position should be resetted
    Assertions.assertEquals(moverCellPositionAfterReset, moverCellPositionBeforePlay)
  }

  @Test
  def testStepAndPlaySimulationButton(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    val moverCellStep0 = getImageViewByImage(CellImage.MoverRight.image)
    val moverCellPositionStep0 = (GridPane.getColumnIndex(moverCellStep0), GridPane.getRowIndex(moverCellStep0))

    // it should display the game simulation by 1 step
    clickOnStepSimulationButton(robot)
    val moverCellStep1 = getImageViewByImage(CellImage.MoverRight.image)
    val moverCellPositionStep1 = (GridPane.getColumnIndex(moverCellStep1), GridPane.getRowIndex(moverCellStep1))
    Assertions.assertEquals((moverCellPositionStep0._1 + 1, moverCellPositionStep0._2), moverCellPositionStep1)
    // it should display the game simulation by 2 step
    clickOnStepSimulationButton(robot)
    val moverCellStep2 = getImageViewByImage(CellImage.MoverRight.image)
    val moverCellPositionStep2 = (GridPane.getColumnIndex(moverCellStep2), GridPane.getRowIndex(moverCellStep2))
    Assertions.assertEquals((moverCellPositionStep0._1 + 2, moverCellPositionStep0._2), moverCellPositionStep2)
    // it should show the game simulation
    clickOnPlaySimulationButton(robot)
  }

  @Test
  def testGameControlsAfterSimulationHasEnded(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    clickOnPlaySimulationButton(robot)
    WaitForAsyncUtils.sleep(5, TimeUnit.SECONDS)
    // after simulation has ended
    // the play simulation button should be disabled
    FxAssertions.assertThat(getButtonById("playSimulationButton")).isDisabled
    // the step simulation button should be disabled
    FxAssertions.assertThat(getButtonById("stepSimulationButton")).isDisabled
    // the next button should be visible
    FxAssertions.assertThat(getButtonById("nextButton")).isVisible
  }

  @Test
  def testNextButtonShouldShowNextLevel(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    val boardLevel1: GridPane = robot
      .lookup(_.isInstanceOf[GridPane])
      .query[GridPane]()
    moveMoverCell(robot)
    clickOnPlaySimulationButton(robot)
    WaitForAsyncUtils.sleep(5, TimeUnit.SECONDS)

    // after the simulation has ended
    // clicking the next button should show the next level
    clickOnNextButton(robot)

    val boardLevel2: GridPane = robot
      .lookup(_.isInstanceOf[GridPane])
      .query[GridPane]()

    // the board of level 2 should not equal the board of level 1
    Assertions.assertNotEquals(boardLevel2.getChildren.toArray, boardLevel1.getChildren.toArray)
  }

  @Test
  def testBackToMenuButton(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    // when in game, the player clicks the menu button
    clickOnBackToMenu(robot)
    // it should return to main menu
    testMainMenuButtonIsPresent(robot)
  }

  // player actions
  private def clickOnPlayButton(robot: FxRobot): Unit = robot.clickOn(getButtonById("playButton"))
  private def clickOnBackButton(robot: FxRobot): Unit = robot.clickOn(getButtonById("backButton"))
  private def clickOnLevel(robot: FxRobot): Unit = robot.clickOn(
    robot
      .lookup((b: Button) => b.getText == "1")
      .queryButton()
  )
  private def moveMoverCell(robot: FxRobot): Unit = robot
    .drag(getImageViewByImage(CellImage.MoverRight.image), MouseButton.PRIMARY)
    .dropBy(0, height * 0.2)
    .drop()
  private def clickOnPlaySimulationButton(robot: FxRobot): Unit = robot.clickOn(getButtonById("playSimulationButton"))
  private def clickOnPauseSimulationButton(robot: FxRobot): Unit = clickOnPlaySimulationButton(robot)
  private def clickOnResetSimulationButton(robot: FxRobot): Unit = robot.clickOn(getButtonById("resetButton"))
  private def clickOnStepSimulationButton(robot: FxRobot): Unit = robot.clickOn(getButtonById("stepSimulationButton"))
  private def clickOnNextButton(robot: FxRobot): Unit = robot.clickOn(getButtonById("nextButton"))
  private def clickOnBackToMenu(robot: FxRobot): Unit = robot.clickOn(getButtonById("backToMenuButton"))
}
