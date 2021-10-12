package it.unibo.pps.caw.view

import javafx.scene.control.Button
import javafx.scene.Node
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.GridPane
import org.junit.jupiter.api.{BeforeAll, TestInstance}
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.{FxRobot}
import org.testfx.framework.junit5.{ApplicationExtension}
import org.testfx.assertions.api.Assertions as FXAssertions
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
  }

  protected def getButtonById(id: String)(robot: FxRobot): Button = robot.lookup(_.getId == id).queryButton()

  protected def getImageView(board: GridPane)(x: Int, y: Int)(image: Image): ImageView =
    board
      .getChildren
      .asScala
      .map(_.asInstanceOf[ImageView])
      .find(n => n.getImage == image && GridPane.getColumnIndex(n) == x && GridPane.getRowIndex(n) == y)
      .get

  protected def testDefaultStateButton(buttonId: String, text: String)(robot: FxRobot): Unit = {
    val button: Button = getButtonById(buttonId)(robot)
    FXAssertions.assertThat(button).isVisible
    FXAssertions.assertThat(button).isEnabled
    FXAssertions.assertThat(button).hasText(text)
  }
}
