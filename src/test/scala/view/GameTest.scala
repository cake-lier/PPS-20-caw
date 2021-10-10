package it.unibo.pps.caw.view

import it.unibo.pps.caw.common.model.Position
import it.unibo.pps.caw.common.view.*
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.junit.jupiter.api.{Assertions, Test}
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.assertions.api.Assertions as FxAssertions
import org.testfx.framework.junit5.{Start, Stop}
import org.testfx.util.WaitForAsyncUtils

import scala.jdk.CollectionConverters.given
import java.util.concurrent.TimeUnit

/** Test for [[it.unibo.pps.caw.menu.LevelSelectionView]] and [[it.unibo.pps.caw.game.view.GameView]] */
class GameTest extends ViewTest {

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

  private def clickOnPlayButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "playButton")

  @Test
  def testLevelSelectionViewHasAllControls(robot: FxRobot): Unit = {
    // when player clicks the play button
    clickOnPlayButton(robot)
    // levels should be present
    val levels: Set[Button] = robot.lookup[Button](_.getText.matches("\\d+")).queryAll[Button]().asScala.toSet
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

  private def clickOnLevel(robot: FxRobot): Unit = robot.clickOn[Button](_.getText == "1")

  @Test
  def testGameViewHasAllControls(robot: FxRobot): Unit = {
    // when player clicks on the 1st level
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    // the game board should be present
    // ...
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
    // the mover cell can be dropped inside the playable area
    val moverCell = getImageViewByImage(CellImage.MoverRight.image)(robot)
    robot.drag(moverCell, MouseButton.PRIMARY).dropBy(0, stageHeight * 0.2).drop()
    // the mover cell cannot be dropped outside the playable area
    robot.drag(moverCell, MouseButton.PRIMARY).dropBy(stageWidth * 0.2, 0)
    // the enemy cell cannot be dragged
    robot.drag(getImageViewByImage(CellImage.Enemy.image)(robot), MouseButton.PRIMARY).dropBy(0, -stageHeight * 0.2)
  }

  private def moveMoverCell(robot: FxRobot): Unit =
    robot.drag(getImageViewByImage(CellImage.MoverRight.image)(robot)).dropBy(0, stageHeight * 0.2).drop()

  private def clickOnPlaySimulationButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "playSimulationButton")

  @Test
  def testMoverCellHasMovedDuringSimulation(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    val moverCellBeforePlay: ImageView = getImageViewByImage(CellImage.MoverRight.image)(robot)
    val moverCellPositionBeforePlay: Position =
      Position(GridPane.getColumnIndex(moverCellBeforePlay), GridPane.getRowIndex(moverCellBeforePlay))
    clickOnPlaySimulationButton(robot) // Play
    clickOnPlaySimulationButton(robot) // Pause
    val moverCellAfterPlay: ImageView = getImageViewByImage(CellImage.MoverRight.image)(robot)
    val moverCellPositionAfterPlay: Position =
      Position(GridPane.getColumnIndex(moverCellAfterPlay), GridPane.getRowIndex(moverCellAfterPlay))
    // the mover cell should have moved
    Assertions.assertNotEquals(moverCellPositionAfterPlay, moverCellPositionBeforePlay)
  }

  private def testDisabledButton(buttonId: String, text: String)(robot: FxRobot): Unit = {
    val button: Button = getButtonById(id = buttonId)(robot)
    FxAssertions.assertThat(button).isVisible
    FxAssertions.assertThat(button).isDisabled
    FxAssertions.assertThat(button).hasText(text)
  }

  @Test
  def testGameControlsAfterPlayButtonIsClicked(robot: FxRobot): Unit = {
    //after starting the simulation by clicking the play button
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
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
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    clickOnPlaySimulationButton(robot)
    clickOnPauseSimulationButton(robot)
    // the play simutlation button text should change from 'Pause' to 'Play'
    testDefaultStateButton(buttonId = "playSimulationButton", text = "Play")(robot)
    // the step button should be enabled
    testDefaultStateButton(buttonId = "stepSimulationButton", text = "Step")(robot)
  }

  @Test
  def testGameCellDragAndDropWhileSimulationIsPaused(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    clickOnPlaySimulationButton(robot)
    clickOnPauseSimulationButton(robot)
    // no game cells should be movable
    robot.drag(getImageViewByImage(CellImage.MoverRight.image)(robot)).dropBy(0, -stageHeight * 0.2)
    robot.drag(getImageViewByImage(CellImage.Enemy.image)(robot)).dropBy(0, -stageHeight * 0.2)
  }

  private def clickOnResetSimulationButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "resetButton")

  @Test
  def testResetButton(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    val moverCellBeforePlay: ImageView = getImageViewByImage(CellImage.MoverRight.image)(robot)
    val moverCellPositionBeforePlay: Position =
      Position(GridPane.getColumnIndex(moverCellBeforePlay), GridPane.getRowIndex(moverCellBeforePlay))
    clickOnPlaySimulationButton(robot)
    clickOnResetSimulationButton(robot)
    val moverCellAfterReset: ImageView = getImageViewByImage(CellImage.MoverRight.image)(robot)
    val moverCellPositionAfterReset: Position =
      Position(GridPane.getColumnIndex(moverCellAfterReset), GridPane.getRowIndex(moverCellAfterReset))
    // after reset, the mover cell position should be resetted
    Assertions.assertEquals(moverCellPositionAfterReset, moverCellPositionBeforePlay)
  }

  private def clickOnStepSimulationButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "stepSimulationButton")

  @Test
  def testStepAndPlaySimulationButton(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    val moverCellStep0: ImageView = getImageViewByImage(CellImage.MoverRight.image)(robot)
    val moverCellPositionStep0: Position = Position(GridPane.getColumnIndex(moverCellStep0), GridPane.getRowIndex(moverCellStep0))
    // it should display the game simulation by 1 step
    clickOnStepSimulationButton(robot)
    val moverCellStep1: ImageView = getImageViewByImage(CellImage.MoverRight.image)(robot)
    val moverCellPositionStep1: Position = Position(GridPane.getColumnIndex(moverCellStep1), GridPane.getRowIndex(moverCellStep1))
    Assertions.assertEquals(Position(moverCellPositionStep0.x + 1, moverCellPositionStep0.y), moverCellPositionStep1)
    // it should display the game simulation by 2 step
    clickOnStepSimulationButton(robot)
    val moverCellStep2: ImageView = getImageViewByImage(CellImage.MoverRight.image)(robot)
    val moverCellPositionStep2: Position = Position(GridPane.getColumnIndex(moverCellStep2), GridPane.getRowIndex(moverCellStep2))
    Assertions.assertEquals(Position(moverCellPositionStep0.x + 2, moverCellPositionStep0.y), moverCellPositionStep2)
    // it should show the game simulation
    clickOnPlaySimulationButton(robot)
    // ...
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
    testDisabledButton(buttonId = "playSimulationButton", text = "Pause")(robot)
    // the step simulation button should be disabled
    testDisabledButton(buttonId = "stepSimulationButton", text = "Step")(robot)
    // the next button should be visible
    testDefaultStateButton(buttonId = "nextButton", text = "Next")(robot)
  }

  private def clickOnNextButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "nextButton")

  @Test
  def testNextButtonShouldShowNextLevel(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    val boardLevel1: GridPane = robot.lookup(_.isInstanceOf[GridPane]).query[GridPane]()
    moveMoverCell(robot)
    clickOnPlaySimulationButton(robot)
    WaitForAsyncUtils.sleep(5, TimeUnit.SECONDS)
    // after the simulation has ended
    // clicking the next button should show the next level
    clickOnNextButton(robot)
    // the board of level 2 should not equal the board of level 1
    //...
  }

  private def clickOnBackToMenu(robot: FxRobot): Unit = robot.clickOn(_.getId == "backToMenuButton")

  @Test
  def testBackToMenuButton(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    // when in game, the player clicks the menu button
    clickOnBackToMenu(robot)
    // it should return to main menu
    testPlayButtonIsPresent(robot)
  }
}
