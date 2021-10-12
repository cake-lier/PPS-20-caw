package it.unibo.pps.caw.view

import it.unibo.pps.caw.common.model.Position
import it.unibo.pps.caw.common.view.*
import javafx.scene.control.Button
import javafx.scene.image.ImageView
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
    val gameBoard: GridPane = getGameBoard(robot)
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

  private def getGameBoard(robot: FxRobot): GridPane = {
    val gameView: GridPane = robot.lookup[GridPane](_.isInstanceOf[GridPane]).query()
    gameView
      .getChildren
      .asScala
      .find(n => GridPane.getColumnIndex(n) == 2 && GridPane.getRowIndex(n) == 3 && n.isInstanceOf[GridPane])
      .map(_.asInstanceOf[GridPane])
      .get
  }

  @Test
  def testGameCellDragAndDropDuringSetupPhase(robot: FxRobot): Unit = {
    // during the setup phase
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    val gameBoard: GridPane = getGameBoard(robot)
    // the mover cell can be dropped inside the playable area
    val moverCell: ImageView = getImageView(gameBoard)(2, 2)(CellImage.MoverRight.image)
    robot.drag(moverCell, MouseButton.PRIMARY).dropBy(0, stageHeight * 0.2).drop()
    val moverCellAfterDrag = getImageView(gameBoard)(2, 4)(CellImage.MoverRight.image)
    // the mover cell cannot be dropped outside the playable area
    robot.drag(moverCellAfterDrag, MouseButton.PRIMARY).dropBy(stageWidth * 0.2, 0)
    getImageView(gameBoard)(2, 4)(CellImage.MoverRight.image)
    // the enemy cell cannot be dragged
    robot
      .drag(getImageView(gameBoard)(7, 4)(CellImage.Enemy.image), MouseButton.PRIMARY)
      .dropBy(0, -stageHeight * 0.2)
    getImageView(gameBoard)(7, 4)(CellImage.Enemy.image)
  }

  private def moveMoverCell(robot: FxRobot): Unit =
    robot
      .drag(getImageView(getGameBoard(robot))(2, 2)(CellImage.MoverRight.image), MouseButton.PRIMARY)
      .dropBy(0, stageHeight * 0.2)
      .drop()

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
    getImageView(getGameBoard(robot))(3, 4)(CellImage.MoverRight.image)
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
    val gameBoard = getGameBoard(robot)
    robot.drag(getImageView(gameBoard)(3, 4)(CellImage.MoverRight.image)).dropBy(0, -stageHeight * 0.2)
    robot.drag(getImageView(gameBoard)(7, 4)(CellImage.Enemy.image)).dropBy(0, -stageHeight * 0.2)
  }

  private def clickOnResetSimulationButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "resetButton")

  @Test
  def testResetButton(robot: FxRobot): Unit = {
    setUpGame(robot)
    clickOnPlaySimulationButton(robot)
    WaitForAsyncUtils.sleep(2, TimeUnit.SECONDS)
    clickOnResetSimulationButton(robot)
    // after reset, the mover cell should be found in the original position
    getImageView(getGameBoard(robot))(2, 4)(CellImage.MoverRight.image)
  }

  private def clickOnStepSimulationButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "stepSimulationButton")

  @Test
  def testStepAndPlaySimulationButton(robot: FxRobot): Unit = {
    setUpGame(robot)
    val gameBoard = getGameBoard(robot)
    // it should display the game simulation by 1 step
    clickOnStepSimulationButton(robot)
    // after 1 step, the mover cell should be found at coordinate (3,4)
    getImageView(gameBoard)(3, 4)(CellImage.MoverRight.image)
    // it should display the game simulation by 2 step
    clickOnStepSimulationButton(robot)
    // after 2 steps, the mover cell should be found at coordinate (4,4)
    getImageView(gameBoard)(4, 4)(CellImage.MoverRight.image)
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
    val boardLevel1: GridPane = getGameBoard(robot)
    moveMoverCell(robot)
    (0 until 5).foreach(_ => clickOnStepSimulationButton(robot))
    // after the simulation has ended
    // clicking the next button should show the next level
    testDefaultStateButton(buttonId = "nextButton", text = "Next")(robot)
    clickOnNextButton(robot)
    // the board of level 2 should not equal the board of level 1
    val boardLevel2: GridPane = getGameBoard(robot)
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

  @Test
  def testFirstLevelIsHighlitedAsCompleted(robot: FxRobot): Unit = {
    setUpGame(robot)
    (0 until 5).foreach(_ => clickOnStepSimulationButton(robot))
    clickOnBackToMenu(robot)
    clickOnPlayButton(robot)

    //after the first level is completed, its button should be blue
    val levels: Set[Button] = robot.lookup[Button](_.getText.matches("\\d+")).queryAll[Button]().asScala.toSet
    levels
      .foreach(b => {
        println(b.getText)
        println(b.getStyleClass.asScala.find(_ == "completed").isEmpty)
        b.getText match {
          case "1" => Assertions.assertTrue(b.getStyleClass.asScala.find(_ == "completed").isDefined)
          case _   => Assertions.assertTrue(b.getStyleClass.asScala.find(_ == "completed").isEmpty)
        }
      })
  }
}
