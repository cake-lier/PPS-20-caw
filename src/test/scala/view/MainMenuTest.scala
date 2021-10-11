package it.unibo.pps.caw.view

import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.scalatest.DoNotDiscover
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.framework.junit5.{Start, Stop}

/** Test for [[it.unibo.pps.caw.menu.MainMenuView]] */
class MainMenuTest extends ViewTest {

  @Start
  def start(stage: Stage): Unit = TestApplicationView(stage)

  @Stop
  def stop(): Unit = FxToolkit.hideStage()

  @Test
  def testPlayButton(robot: FxRobot): Unit = testDefaultStateButton(buttonId = "playButton", text = "Play")(robot)

  @Test
  def testEditorButton(robot: FxRobot): Unit = testDefaultStateButton(buttonId = "editorButton", text = "Level editor")(robot)

  @Test
  def testLoadLevelButton(robot: FxRobot): Unit = testDefaultStateButton(buttonId = "loadButton", text = "Load a level")(robot)

  @Test
  def testSettingsButton(robot: FxRobot): Unit = testDefaultStateButton(buttonId = "settingsButton", text = "Settings")(robot)

  @Test
  def testExitButton(robot: FxRobot): Unit = testDefaultStateButton(buttonId = "exitButton", text = "Exit")(robot)
}
