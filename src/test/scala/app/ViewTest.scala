package it.unibo.pps.caw.app

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.GridPane
import org.testfx.util.WaitForAsyncUtils
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions as FXAssertions
import org.testfx.framework.junit5.ApplicationExtension
import org.junit.jupiter.api.{BeforeAll, TestInstance}
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.extension.ExtendWith

import scala.jdk.CollectionConverters.given

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(Array(classOf[ApplicationExtension]))
abstract class ViewTest {

  @BeforeAll
  def setup(): Unit = {
    System.setProperty("testfx.robot", "glass")
    System.setProperty("testfx.headless", "true")
    System.setProperty("java.awt.headless", "true")
    System.setProperty("prism.order", "sw")
    System.setProperty("prism.text", "t2k")
    WaitForAsyncUtils.checkAllExceptions = false;
    WaitForAsyncUtils.autoCheckException = false;
  }

  protected def getButtonById(id: String)(robot: FxRobot): Button = robot.lookup(_.getId == id).queryButton()

  protected def getGameBoard(robot: FxRobot): GridPane = robot.lookup(_.getId == "board").query[GridPane]

  protected def getDropTile(board: GridPane)(x: Int, y: Int): ImageView =
    board
      .getChildren
      .asScala
      .find(n => GridPane.getColumnIndex(n) == x && GridPane.getRowIndex(n) == y && n.isInstanceOf[ImageView])
      .map(_.asInstanceOf[ImageView])
      .get

  protected def getImageView(board: GridPane)(image: Image): ImageView =
    board
      .getChildren
      .asScala
      .find(n => n.isInstanceOf[ImageView] && n.asInstanceOf[ImageView].getImage == image)
      .map(_.asInstanceOf[ImageView])
      .get

  protected def testDefaultStateButton(buttonId: String, text: String)(robot: FxRobot): Unit = {
    val button: Button = getButtonById(buttonId)(robot)
    FXAssertions.assertThat(button).isVisible
    FXAssertions.assertThat(button).isEnabled
    FXAssertions.assertThat(button).hasText(text)
  }

  protected def getImageViewByImage(image: Image)(robot: FxRobot): ImageView =
    robot.lookup[ImageView](_.getImage == image).query()

  protected def testDisabledButton(buttonId: String, text: String)(robot: FxRobot): Unit = {
    val button: Button = getButtonById(id = buttonId)(robot)
    FXAssertions.assertThat(button).isVisible
    FXAssertions.assertThat(button).isDisabled
    FXAssertions.assertThat(button).hasText(text)
  }
}
