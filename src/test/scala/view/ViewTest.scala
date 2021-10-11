package it.unibo.pps.caw.view

import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.junit.jupiter.api.{BeforeAll, TestInstance}
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.framework.junit5.{ApplicationExtension, Start, Stop}
import org.testfx.assertions.api.Assertions as FXAssertions

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
  }

  protected def getButtonById(id: String)(robot: FxRobot): Button = robot.lookup(_.getId == id).queryButton()

  protected def getImageViewByCoordinates(board: GridPane)(x: Int, y: Int)(robot: FxRobot): ImageView =
    board
      .getChildren
      .stream()
      .filter(n => GridPane.getColumnIndex(n) == x && GridPane.getRowIndex(n) == y)
      .map(_.asInstanceOf[ImageView])
      .findFirst()
      .get()

  protected def testDefaultStateButton(buttonId: String, text: String)(robot: FxRobot): Unit = {
    val button: Button = getButtonById(buttonId)(robot)
    FXAssertions.assertThat(button).isVisible
    FXAssertions.assertThat(button).isEnabled
    FXAssertions.assertThat(button).hasText(text)
  }
}
