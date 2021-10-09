package it.unibo.pps.caw
package view

import javafx.scene.control.Button
import javafx.stage.Stage
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.{Assertions, BeforeAll, Test, TestInstance}
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions as FxAssertions
import org.testfx.framework.junit5.{ApplicationExtension, Start}

@ExtendWith(Array(classOf[ApplicationExtension]))
@TestInstance(Lifecycle.PER_CLASS)
/** Test for [[it.unibo.pps.caw.menu.MainMenuView]] */
class MainMenuTest {

  @BeforeAll
  def beforeAll(): Unit = {
    System.setProperty("testfx.robot", "glass")
    System.setProperty("testfx.headless", "true")
    System.setProperty("java.awt.headless", "true")
    System.setProperty("prism.order", "sw")
    System.setProperty("prism.text", "t2k")
  }

  @Start
  def start(stage: Stage): Unit = {
    TestApplicationView(stage)
  }

  import ViewTestHelper._

  @Test
  def testPlayButton(robot: FxRobot): Unit = {
    val playButton: Button = getButtonById("playButton")
    FxAssertions.assertThat(playButton).hasText("Play")
    FxAssertions.assertThat(playButton).isVisible
    FxAssertions.assertThat(playButton).isEnabled
  }

  @Test
  def testEditorButton(robot: FxRobot): Unit = {
    val editorButton: Button = getButtonById("editorButton")
    FxAssertions.assertThat(editorButton).hasText("Level editor")
    FxAssertions.assertThat(editorButton).isVisible
    FxAssertions.assertThat(editorButton).isEnabled
  }

  @Test
  def testLoadLevelButton(robot: FxRobot): Unit = {
    val loadButton: Button = getButtonById("loadButton")
    FxAssertions.assertThat(loadButton).hasText("Load a level")
    FxAssertions.assertThat(loadButton).isVisible
    FxAssertions.assertThat(loadButton).isEnabled
  }

  @Test
  def testSettingsButton(robot: FxRobot): Unit = {
    val settingsButton: Button = getButtonById("settingsButton")
    FxAssertions.assertThat(settingsButton).hasText("Settings")
    FxAssertions.assertThat(settingsButton).isVisible
    FxAssertions.assertThat(settingsButton).isEnabled
  }

  @Test
  def testExitButton(robot: FxRobot): Unit = {
    val exitButton: Button = getButtonById("exitButton")
    FxAssertions.assertThat(exitButton).hasText("Exit")
    FxAssertions.assertThat(exitButton).isVisible
    FxAssertions.assertThat(exitButton).isEnabled
  }

}
