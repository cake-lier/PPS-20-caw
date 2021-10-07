package it.unibo.pps.caw
package view

import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.application.Platform
import org.scalatest.{BeforeAndAfterAll, DoNotDiscover}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.util.WaitForAsyncUtils

@DoNotDiscover
class MainMenuTest extends AnyFunSpec with Matchers with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    System.setProperty("testfx.robot", "glass")
    System.setProperty("testfx.headless", "true")
    System.setProperty("java.awt.headless", "true")
    System.setProperty("prism.order", "sw")
    System.setProperty("prism.text", "t2k")
    FxToolkit.registerPrimaryStage()
    FxToolkit.setupSceneRoot(() => FXMLLoader.load(getClass.getResource("/fxml/main_menu_page.fxml")))
    WaitForAsyncUtils.waitForFxEvents()
    FxToolkit.showStage()
  }

  private val robot = FxRobot()

  describe("The main menu view") {
    import ViewTestHelper._
    it("should have a play button") {
      val playButton: Button = getButtonById("playButton")
      playButton.getText() shouldBe "Play"
      playButton.isDisabled() shouldBe true
      robot.clickOn(playButton)
    }
    it("should have an editor button") {
      val editorButton: Button = getButtonById("editorButton")
      editorButton.getText() shouldBe "Level Editor"
      editorButton.isDisabled() shouldBe false
      robot.clickOn(editorButton)
    }
    it("should have a load level button") {
      val loadButton: Button = getButtonById("loadButton")
      loadButton.getText() shouldBe "Load a level"
      loadButton.isDisabled() shouldBe false
      robot.clickOn(loadButton)
    }
    it("should have a settings button") {
      val settingsButton: Button = getButtonById("settingsButton")
      settingsButton.getText() shouldBe "Settings"
      settingsButton.isDisabled() shouldBe false
      robot.clickOn(settingsButton)
    }
    it("should have an exit button") {
      val exitButton: Button = getButtonById("exitButton")
      exitButton.getText() shouldBe "Exit"
      exitButton.isDisabled() shouldBe false
      robot.clickOn(exitButton)
    }
  }
}
