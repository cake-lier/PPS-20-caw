package it.unibo.pps.caw.game.view

import it.unibo.pps.caw.common.model.Position
import it.unibo.pps.caw.common.view.*
import it.unibo.pps.caw.app.{TestApplicationView}
import it.unibo.pps.caw.common.BoardViewTest
import it.unibo.pps.caw.menu.view.LevelSelectionView
import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane
import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.jupiter.api.{Assertions, Test}
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.assertions.api.Assertions as FxAssertions
import org.testfx.framework.junit5.{Start, Stop}
import org.testfx.robot.Motion
import org.testfx.util.WaitForAsyncUtils

import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.given

/** Test for [[LevelSelectionView]] and [[it.unibo.pps.caw.game.view.GameView]] */
class GameViewTest extends GameTest {

  protected var stageWidth: Double = 0.0
  protected var stageHeight: Double = 0.0

  @Start
  def start(stage: Stage): Unit = {
    TestApplicationView(stage)
    stageWidth = stage.getWidth
    stageHeight = stage.getHeight
  }

  @Stop
  def stop(): Unit = FxToolkit.hideStage()

  @Test
  def testPlayButtonIsPresent(robot: FxRobot): Unit = testDefaultStateButton(buttonId = "playButton", text = "Play")(robot)

  @Test
  def testLevelSelectionViewHasAllControls(robot: FxRobot): Unit = {
    // when player clicks the play button
    clickOnPlayButton(robot)
    // levels should be present
    val levels: Set[Button] = getLevels(robot)
    Assertions.assertEquals(24, levels.size)
    Assertions.assertEquals(24, levels.map(_.getText).size)
    levels.foreach(b =>
      FxAssertions.assertThat(b).isVisible
      FxAssertions.assertThat(b).isEnabled
    )
    // the back button should be present
    testDefaultStateButton(buttonId = "backButton", text = "Back")(robot)
  }

  private def clickOnBackButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "backButton")

  @Test
  def testBackButtonOfLevelSelectionMenu(robot: FxRobot): Unit = {
    // when player clicks the play button
    clickOnPlayButton(robot)
    // and then clicks the back button
    clickOnBackButton(robot)
    // it should return back to main menu
    testPlayButtonIsPresent(robot)
  }

  private def testInvisibleButton(buttonId: String, text: String)(robot: FxRobot): Unit = {
    val button: Button = getButtonById(id = buttonId)(robot)
    FxAssertions.assertThat(button).hasText(text)
    FxAssertions.assertThat(button).isInvisible
    FxAssertions.assertThat(button).isEnabled
  }

  @Test
  def testGameViewHasAllControls(robot: FxRobot): Unit = {
    // when player clicks on the 1st level
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    // the game board should be present
    val gameBoard: GridPane = getBoard(robot)
    // the play button should be present
    testDefaultStateButton(buttonId = "playSimulationButton", text = "Play")(robot)
    // the step button should be present
    testDefaultStateButton(buttonId = "stepSimulationButton", text = "Step")(robot)
    // the reset button should be present but invisible
    testInvisibleButton(buttonId = "resetButton", text = "Reset")(robot)
    // the next button should be present but invisible
    testInvisibleButton(buttonId = "nextButton", text = "Next")(robot)
    // the back button should be present
    testDefaultStateButton(buttonId = "backToMenuButton", text = "Menu")(robot)
  }

  @Test
  def testGameCellDragAndDropDuringSetupPhase(robot: FxRobot): Unit = {
    // during the setup phase
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    val gameBoard: GridPane = getBoard(robot)
    // the mover cell can be dropped inside the playable area
    val moverCell = getImageView(gameBoard)(CellImage.MoverRight.image)
    val outsideTile = getDropTile(gameBoard)(7, 4)
    robot.drag(moverCell).dropTo(getDropTile(gameBoard)(2, 4))
    // the mover cell cannot be dropped outside the playable area
    robot.drag(moverCell).dropTo(outsideTile)
    Assertions.assertEquals((2, 4), (GridPane.getColumnIndex(moverCell), GridPane.getRowIndex(moverCell)))

    // the enemy cell cannot be dragged
    val enemyCell = getImageView(gameBoard)(CellImage.Enemy.image)
    robot.drag(enemyCell).dropTo(outsideTile)
    Assertions.assertEquals((7, 4), (GridPane.getColumnIndex(enemyCell), GridPane.getRowIndex(enemyCell)))
  }

  private def clickOnPlaySimulationButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "playSimulationButton")

  private def setUpGame(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
  }

  @Test
  def testMoverCellHasMovedDuringSimulation(robot: FxRobot): Unit = {
    setUpGame(robot)
    clickOnStepSimulationButton(robot)
    // after starting the simulation by one step
    // the mover cell should be found at coordinates (3,4)
    val moverCell = getImageView(getBoard(robot))(CellImage.MoverRight.image)
    Assertions.assertEquals((3, 4), (GridPane.getColumnIndex(moverCell), GridPane.getRowIndex(moverCell)))
  }

  @Test
  def testGameControlsAfterPlayButtonIsClicked(robot: FxRobot): Unit = {
    //after starting the simulation by clicking the play button
    setUpGame(robot)
    clickOnPlaySimulationButton(robot)
    // the play simulation button text should change from 'Play' to 'Pause'
    testDefaultStateButton(buttonId = "playSimulationButton", text = "Pause")(robot)
    // the step button should be disabled
    testDisabledButton(buttonId = "stepSimulationButton", text = "Step")(robot)
    // the reset button should be visible
    testDefaultStateButton(buttonId = "resetButton", text = "Reset")(robot)
  }

  private def clickOnPauseSimulationButton(robot: FxRobot): Unit = clickOnPlaySimulationButton(robot)

  @Test
  def testGameControlsAfterPauseButtonIsClicked(robot: FxRobot): Unit = {
    setUpGame(robot)
    clickOnPlaySimulationButton(robot)
    clickOnPauseSimulationButton(robot)
    // the play simutlation button text should change from 'Pause' to 'Play'
    testDefaultStateButton(buttonId = "playSimulationButton", text = "Play")(robot)
    // the step button should be enabled
    testDefaultStateButton(buttonId = "stepSimulationButton", text = "Step")(robot)
  }

  @Test
  def testGameCellDragAndDropWhileSimulationIsPaused(robot: FxRobot): Unit = {
    setUpGame(robot)
    clickOnPlaySimulationButton(robot)
    clickOnPauseSimulationButton(robot)
    // no game cells should be movable
    val gameBoard = getBoard(robot)
    val moverCell = getImageView(gameBoard)(CellImage.MoverRight.image)
    val enemyCell = getImageView(gameBoard)(CellImage.Enemy.image)
    val tile: ImageView = getDropTile(gameBoard)(7, 4)
    robot.drag(moverCell).dropTo(tile)
    robot.drag(enemyCell).dropTo(tile)

    Assertions.assertEquals((3, 4), (GridPane.getColumnIndex(moverCell), GridPane.getRowIndex(moverCell)))
    Assertions.assertEquals((7, 4), (GridPane.getColumnIndex(enemyCell), GridPane.getRowIndex(enemyCell)))
  }

  private def clickOnResetSimulationButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "resetButton")

  @Test
  def testResetButton(robot: FxRobot): Unit = {
    setUpGame(robot)
    clickOnPlaySimulationButton(robot)
    WaitForAsyncUtils.sleep(2, TimeUnit.SECONDS)
    clickOnResetSimulationButton(robot)
    // after reset, the mover cell should be found in the original position
    val moverCell = getImageView(getBoard(robot))(CellImage.MoverRight.image)
    Assertions.assertEquals((2, 4), (GridPane.getColumnIndex(moverCell), GridPane.getRowIndex(moverCell)))
  }

  private def clickOnStepSimulationButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "stepSimulationButton")

  @Test
  def testStepAndPlaySimulationButton(robot: FxRobot): Unit = {
    setUpGame(robot)
    val gameBoard = getBoard(robot)
    // it should display the game simulation by 1 step
    clickOnStepSimulationButton(robot)
    // after 1 step, the mover cell should be found at coordinate (3,4)
    val moverCellStep1 = getImageView(gameBoard)(CellImage.MoverRight.image)
    Assertions.assertEquals((3, 4), (GridPane.getColumnIndex(moverCellStep1), GridPane.getRowIndex(moverCellStep1)))
    // it should display the game simulation by 2 step
    clickOnStepSimulationButton(robot)
    // after 2 steps, the mover cell should be found at coordinate (4,4)
    val moverCellStep2 = getImageView(gameBoard)(CellImage.MoverRight.image)
    Assertions.assertEquals((4, 4), (GridPane.getColumnIndex(moverCellStep2), GridPane.getRowIndex(moverCellStep2)))
    // the play button should be clickable
    testDefaultStateButton(buttonId = "playSimulationButton", text = "Play")(robot)
  }

  @Test
  def testGameControlsAfterSimulationHasEnded(robot: FxRobot): Unit = {
    setUpGame(robot)
    (0 until 5).foreach(_ => clickOnStepSimulationButton(robot))
    // after simulation has ended
    // the play simulation button should be disabled
    testDisabledButton(buttonId = "playSimulationButton", text = "Play")(robot)
    // the step simulation button should be disabled
    testDisabledButton(buttonId = "stepSimulationButton", text = "Step")(robot)
    // the next button should be visible
    testDefaultStateButton(buttonId = "nextButton", text = "Next")(robot)
  }
//
  private def clickOnNextButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "nextButton")

  @Test
  def testNextButtonShouldShowNextLevel(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    val boardLevel1: GridPane = getBoard(robot)
    moveMoverCell(robot)
    (0 until 5).foreach(_ => clickOnStepSimulationButton(robot))
    // after the simulation has ended
    // clicking the next button should show the next level
    testDefaultStateButton(buttonId = "nextButton", text = "Next")(robot)
    clickOnNextButton(robot)
    // the board of level 2 should not equal the board of level 1
    val boardLevel2: GridPane = getBoard(robot)
    Assertions.assertNotEquals(boardLevel2.getChildren, boardLevel1.getChildren)
  }

  private def clickOnBackToMenu(robot: FxRobot): Unit = robot.clickOn(_.getId == "backToMenuButton")

  @Test
  def testBackToMenuButton(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    // when in game, after the player clicks the menu button
    clickOnBackToMenu(robot)
    // it should return to main menu
    testPlayButtonIsPresent(robot)
  }
}
