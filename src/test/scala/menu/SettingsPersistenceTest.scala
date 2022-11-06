package it.unibo.pps.caw
package menu

import app.TestApplicationView

import javafx.stage.Stage
import org.junit.jupiter.api.*
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.framework.junit5.{Start, Stop}

import java.nio.file.*

@TestMethodOrder(classOf[MethodOrderer.OrderAnnotation])
class SettingsPersistenceTest extends AbstractSettingsTest {

  @BeforeAll
  def deleteSettings(): Unit = {
    val settings: Path = Paths.get(System.getProperty("user.home"), ".settings_caw.json")
    if (settings.toFile.exists()) {
      Files.delete(settings)
    }
  }

  @Start
  def start(stage: Stage): Unit = TestApplicationView(stage)

  @Stop
  def stop(): Unit = FxToolkit.hideStage()

  private val musicVolume: Int = 10
  private val soundsVolume: Int = 10

  @Test
  @Order(1)
  def changeSettings(robot: FxRobot): Unit = {
    clickOnSettingsButton(robot)
    setDifferentSettings(musicVolume, soundsVolume)(robot)
    clickOnBackFromSettingsButton(robot)
  }

  @Test
  @Order(2)
  def testChangedSettingsArePersisted(robot: FxRobot): Unit = {
    clickOnSettingsButton(robot)
    checkSlider(robot.lookup(_.getId == "musicVolumeSlider").query(), musicVolume)
    checkSlider(robot.lookup(_.getId == "effectsVolumeSlider").query(), soundsVolume)
  }
}
