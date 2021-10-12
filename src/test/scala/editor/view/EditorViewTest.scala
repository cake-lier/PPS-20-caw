package it.unibo.pps.caw.editor.view

import it.unibo.pps.caw.common.view.{CellImage, DraggableImageView}
import it.unibo.pps.caw.editor.view.{EditorView, LevelEditorMenuView}
import it.unibo.pps.caw.game.model.GameModel
import com.sun.javafx.scene.input.DragboardHelper.DragboardAccessor
import it.unibo.pps.caw
import it.unibo.pps.caw.app
import it.unibo.pps.caw.app.ViewTest
import it.unibo.pps.caw.view.TestApplicationView
import javafx.scene.Node
import javafx.scene.control.{Button, Slider, TextField}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.{Assertions, BeforeAll, Order, Test, TestInstance, TestMethodOrder}
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.assertions.api.Assertions as FxAssertions
import org.testfx.framework.junit5.{ApplicationExtension, Start, Stop}
import org.testfx.util.WaitForAsyncUtils

import java.nio.file.{Files, Path, Paths}
import java.util.concurrent.TimeUnit
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.given

/** Tests for class [[EditorView]] and [[LevelEditorMenuView]] */
@TestMethodOrder(classOf[OrderAnnotation])
class EditorViewTest extends ViewTest {

  protected var stageWidth: Double = 0.0
  protected var stageHeight: Double = 0.0

  @Start
  def start(stage: Stage): Unit = {
    TestApplicationView(stage)
    stageWidth = stage.getWidth
    stageHeight = stage.getHeight
  }

  @Stop
  def stop(): Unit = {
    FxToolkit.cleanupStages()
    FxToolkit.hideStage()
  }

  @Test
  def testEditorMenuHasAllControls(robot: FxRobot): Unit = {
    clickOnLevelEditorButton(robot)
    // Go button
    testDisabledButton(buttonId = "continue", text = "Go")(robot)
    // Load button
    testDefaultStateButton(buttonId = "loadFile", text = "Load a level from file")(robot)
    // width text field
    testDefaultTextField(textFieldId = "width")(robot)
    // height text field
    testDefaultTextField(textFieldId = "height")(robot)
    // menu button
    testDefaultStateButton(buttonId = "backButton", text = "Menu")(robot)
  }

  @Test
  def testGoButtonBecomesEnabledWhenWidthAndHeightAreSet(robot: FxRobot): Unit = {
    testEditorMenuHasAllControls(robot)
    // set 5 to width
    writeOnTextField(textFieldId = "width")(value = 5)(robot)
    // set 5 to height
    writeOnTextField(textFieldId = "height")(value = 5)(robot)
    // check if go button is enabled
    testDefaultStateButton(buttonId = "continue", text = "Go")(robot)

    // test under 2 value
    clearTextField(textFieldId = "height")(robot)
    clearTextField(textFieldId = "width")(robot)
    // set 1 to width
    writeOnTextField(textFieldId = "width")(value = 1)(robot)
    // set 1 to height
    writeOnTextField(textFieldId = "height")(value = 1)(robot)
    testDisabledButton(buttonId = "continue", text = "Go")(robot)

    // test 50 value
    clearTextField(textFieldId = "height")(robot)
    clearTextField(textFieldId = "width")(robot)
    // set 50 to width
    writeOnTextField(textFieldId = "width")(value = 50)(robot)
    // set 50 to height
    writeOnTextField(textFieldId = "height")(value = 50)(robot)
    // check if go button is enabled
    testDefaultStateButton(buttonId = "continue", text = "Go")(robot)
  }

  @Test
  def testEditorHasAllControls(robot: FxRobot): Unit = {
    enterEmptyLevelEditor(robot)
    // reset button should be present
    testDefaultStateButton(buttonId = "reset-all", text = "Reset")(robot)
    // rotate cell button should be present
    testDefaultStateButton(buttonId = "rotate-cells-button", text = "Rotate")(robot)
    // back button should be present
    testDefaultStateButton(buttonId = "back-button", text = "Menu")(robot)
    // rules button should be present
    testDefaultStateButton(buttonId = "rules-button", text = "How to")(robot)
    // save button should be present
    testDefaultStateButton(buttonId = "save-button", text = "Save")(robot)
  }

  @Test
  def testEditorHasCellDispenser(robot: FxRobot): Unit = {
    enterEmptyLevelEditor(robot)
    // mover right image view should be present
    testDefaultCellDispenserImageView(CellImage.MoverRight.image)(robot)
    // generator right image view should be present
    testDefaultCellDispenserImageView(CellImage.GeneratorRight.image)(robot)
    // rotator clockwise image view should be present
    testDefaultCellDispenserImageView(CellImage.RotatorClockwise.image)(robot)
    // block image view should be present
    testDefaultCellDispenserImageView(CellImage.Block.image)(robot)
    // enemy image view should be present
    testDefaultCellDispenserImageView(CellImage.Enemy.image)(robot)
    // wall image view should be present
    testDefaultCellDispenserImageView(CellImage.Wall.image)(robot)
    // deleter image view should be present
    testDefaultCellDispenserImageView(CellImage.Deleter.image)(robot)
  }

  private def clickOnHowTo(robot: FxRobot): Unit = clickOnButton(buttonId = "rules-button")(robot)

  @Test
  def testHowTo(robot: FxRobot): Unit = {
    def getTopLeftNodeGridPane(robot: FxRobot): Option[Node] =
      robot
        .lookup[GridPane](_.isInstanceOf[GridPane])
        .query[GridPane]
        .getChildren
        .asScala
        .find(n => GridPane.getColumnIndex(n) == 0 && GridPane.getRowIndex(n) == 0)

    enterEmptyLevelEditor(robot)
    // test left top corner empty
    getTopLeftNodeGridPane(robot) match {
      case Some(_) => Assertions.fail
      case _       =>
    }
    // click on how to
    clickOnHowTo(robot)
    // check if it's present
    getTopLeftNodeGridPane(robot) match {
      case None => Assertions.fail
      case _    =>
    }
    // click onto how to pane
    robot.clickOn(stageWidth / 2, stageHeight / 2)
    // check if it's gone
    getTopLeftNodeGridPane(robot) match {
      case Some(_) => Assertions.fail
      case _       =>
    }
  }

  private def clickOnRotateCellsButton(robot: FxRobot): Unit = clickOnButton(buttonId = "rotate-cells-button")(robot)

  @Test
  def testRotateCellDispenser(robot: FxRobot): Unit = {
    val defaultImages = Set(
      CellImage.MoverRight.image,
      CellImage.GeneratorRight.image,
      CellImage.RotatorClockwise.image,
      CellImage.Block.image,
      CellImage.Enemy.image,
      CellImage.Wall.image,
      CellImage.Deleter.image
    )
    // default image views are present
    testEditorHasCellDispenser(robot)

    def rotateImageView(image: Image): Image = image match {
      case CellImage.MoverRight.image => CellImage.MoverDown.image
      case CellImage.MoverDown.image  => CellImage.MoverLeft.image
      case CellImage.MoverLeft.image  => CellImage.MoverTop.image
      case CellImage.MoverTop.image   => CellImage.MoverRight.image

      case CellImage.RotatorClockwise.image        => CellImage.RotatorCounterclockwise.image
      case CellImage.RotatorCounterclockwise.image => CellImage.RotatorClockwise.image

      case CellImage.GeneratorRight.image => CellImage.GeneratorDown.image
      case CellImage.GeneratorDown.image  => CellImage.GeneratorLeft.image
      case CellImage.GeneratorLeft.image  => CellImage.GeneratorTop.image
      case CellImage.GeneratorTop.image   => CellImage.GeneratorRight.image

      case CellImage.Block.image           => CellImage.BlockVertical.image
      case CellImage.BlockVertical.image   => CellImage.BlockHorizontal.image
      case CellImage.BlockHorizontal.image => CellImage.Block.image

      case CellImage.Wall.image    => CellImage.Wall.image
      case CellImage.Enemy.image   => CellImage.Enemy.image
      case CellImage.Deleter.image => CellImage.Deleter.image
    }
    @tailrec
    def testImages(clickTimes: Int, prevIcons: Set[Image]): Unit = clickTimes match {
      case v if v > 0 =>
        clickOnRotateCellsButton(robot)
        val newImages = prevIcons.map(rotateImageView)
        newImages.foreach(testDefaultCellDispenserImageView(_)(robot))
        testImages(v - 1, newImages)
      case _ =>
    }
    testImages(clickTimes = 10, defaultImages)
  }

  private def clickOnMenuButton(robot: FxRobot): Unit = clickOnButton(buttonId = "back-button")(robot)

  @Test
  def testBackToMenu(robot: FxRobot): Unit = {
    enterEmptyLevelEditor(robot)
    // back to editor menu
    clickOnMenuButton(robot)
    // go button is present
    testEditorMenuHasAllControls(robot)
  }

  @Test
  def testDeleteCell(robot: FxRobot): Unit = {
    enterEmptyLevelEditor(robot)
    robot
      .drag(getImageViewByImage(CellImage.MoverRight.image)(robot), MouseButton.PRIMARY)
      .dropBy(stageHeight * 0.1, -stageHeight * 0.2)
    WaitForAsyncUtils.sleep(500, TimeUnit.MILLISECONDS)
    robot.rightClickOn()
  }

  @Test
  def testDrawPlayableArea(robot: FxRobot): Unit = {
    enterEmptyLevelEditor(robot)
    robot
      .drag(stageWidth * 0.4, stageHeight * 0.3)
      .dropBy(stageWidth * 0.09, stageHeight * 0.2)
  }

  @Test
  def testDeleteDrawPlayableArea(robot: FxRobot): Unit = {
    testDrawPlayableArea(robot)
    WaitForAsyncUtils.sleep(500, TimeUnit.MILLISECONDS)
    robot.rightClickOn()
    WaitForAsyncUtils.sleep(500, TimeUnit.MILLISECONDS)
  }

  @Test
  def testDragAndDropImageView(robot: FxRobot): Unit = {
    enterEmptyLevelEditor(robot)
    robot
      .drag(getImageViewByImage(CellImage.MoverRight.image)(robot))
      .dropBy(0, -stageHeight * 0.2)
      .drag(getImageViewByImage(CellImage.GeneratorRight.image)(robot))
      .drag(getImageViewByImage(CellImage.RotatorClockwise.image)(robot))
      .dropBy(0, -stageHeight * 0.2)
      .drag(getImageViewByImage(CellImage.Block.image)(robot))
      .dropBy(0, -stageHeight * 0.2)
      .drag(getImageViewByImage(CellImage.Enemy.image)(robot))
      .dropBy(-stageWidth * 0.05, -stageHeight * 0.2)
      .drag(getImageViewByImage(CellImage.Wall.image)(robot))
      .dropBy(-stageWidth * 0.1, -stageHeight * 0.3)
      .drag(getImageViewByImage(CellImage.Deleter.image)(robot))
      .dropBy(-stageWidth * 0.25, -stageHeight * 0.3)

      // out of border
      .drag(getImageViewByImage(CellImage.Enemy.image)(robot))
      .dropBy(stageHeight * 0.3, -stageHeight * 0.2)
  }

  protected def testDefaultCellDispenserImageView(defaultImage: Image)(robot: FxRobot): Unit = {
    val imageView: ImageView = getImageViewByImage(defaultImage)(robot)
    FxAssertions.assertThat(imageView).isVisible
    FxAssertions.assertThat(imageView).isEnabled
  }

  protected def testDefaultTextField(textFieldId: String)(robot: FxRobot): Unit = {
    val textField: TextField = robot.lookup(_.getId == textFieldId).query[TextField]
    FxAssertions.assertThat(textField).isEnabled
    FxAssertions.assertThat(textField).isVisible
  }

  // navigation
  private def clickOnLevelEditorButton(robot: FxRobot): Unit = clickOnButton(buttonId = "editorButton")(robot)

  private def writeOnTextField(textFieldId: String)(value: Int)(robot: FxRobot): Unit = {
    robot.clickOn(_.getId == textFieldId)
    robot.write(value.toString)
  }

  private def clearTextField(textFieldId: String)(robot: FxRobot): Unit = {
    val textField: TextField = robot.lookup[TextField](_.getId == textFieldId).query[TextField]
    textField.setText("")
  }

  private def clickOnGoButton(robot: FxRobot): Unit = clickOnButton(buttonId = "continue")(robot)

  private def enterEmptyLevelEditor(robot: FxRobot): Unit = {
    clickOnLevelEditorButton(robot)
    writeOnTextField("height")(value = 5)(robot)
    writeOnTextField("width")(value = 5)(robot)
    clickOnGoButton(robot)
  }

  private def clickOnButton(buttonId: String)(robot: FxRobot): Unit = robot.clickOn(_.getId == buttonId)
}
