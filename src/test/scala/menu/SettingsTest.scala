package it.unibo.pps.caw
package menu

import it.unibo.pps.caw
import it.unibo.pps.caw.app
import it.unibo.pps.caw.app.TestApplicationView
import it.unibo.pps.caw.game.view
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.framework.junit5.{Start, Stop}

import java.nio.file.{Files, Path, Paths}

class SettingsTest extends AbstractSettingsTest {

  @Start
  def start(stage: Stage): Unit = {
    val settings: Path = Paths.get(System.getProperty("user.home"), ".settings_caw.json")
    if (settings.toFile.exists()) {
      Files.delete(settings)
    }
    app.TestApplicationView(stage)
  }

  @Stop
  def stop(): Unit = FxToolkit.hideStage()

  @Test
  def testSettingsContainsAllControls(robot: FxRobot): Unit = {
    clickOnSettingsButton(robot)
    checkSlider(robot.lookup(_.getId == "musicVolumeSlider").query(), value = 50)
    checkSlider(robot.lookup(_.getId == "effectsVolumeSlider").query(), value = 50)
    testDefaultStateButton(buttonId = "backFromSettingsButton", text = "Back")(robot)
  }

  @Test
  def testChangedSettingsAreKept(robot: FxRobot): Unit = {
    clickOnSettingsButton(robot)
    val musicVolume: Int = 30
    val soundsVolume: Int = 70
    setDifferentSettings(musicVolume, soundsVolume)(robot)
    clickOnBackFromSettingsButton(robot)
    clickOnSettingsButton(robot)
    checkSlider(robot.lookup(_.getId == "musicVolumeSlider").query(), value = musicVolume)
    checkSlider(robot.lookup(_.getId == "effectsVolumeSlider").query(), value = soundsVolume)
  }
}
