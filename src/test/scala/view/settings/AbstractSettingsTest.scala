package it.unibo.pps.caw
package view.settings

import view.ViewTest

import javafx.scene.control.Slider
import org.junit.jupiter.api.Assertions
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions as FxAssertions

class AbstractSettingsTest extends ViewTest {

  protected def clickOnSettingsButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "settingsButton")

  protected def setDifferentSettings(musicVolume: Int, soundVolume: Int)(robot: FxRobot): Unit = {
    robot.lookup(_.getId == "musicVolumeSlider").query[Slider]().setValue(musicVolume)
    robot.lookup(_.getId == "effectsVolumeSlider").query[Slider]().setValue(soundVolume)
  }

  protected def clickOnBackFromSettingsButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "backFromSettingsButton")

  protected def checkSlider(slider: Slider, value: Int): Unit = {
    FxAssertions.assertThat(slider).isVisible
    FxAssertions.assertThat(slider).isEnabled
    Assertions.assertEquals(100, slider.getMax)
    Assertions.assertEquals(0, slider.getMin)
    Assertions.assertEquals(value, slider.getValue)
  }
}
