package it.unibo.pps.caw
package view

import common.view.*

import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.Node
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseButton
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, DoNotDiscover}
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.robot.Motion
import org.testfx.util.WaitForAsyncUtils

import java.util.concurrent.TimeUnit

/** Test for [[it.unibo.pps.caw.menu.LevelSelectionView]] and [[it.unibo.pps.caw.game.view.GameView]] */
class GameTest extends AnyFunSpec with Matchers with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    System.setProperty("testfx.robot", "glass")
    System.setProperty("testfx.headless", "true")
    System.setProperty("java.awt.headless", "true")
    System.setProperty("prism.order", "sw")
    System.setProperty("prism.text", "t2k")
    FxToolkit.registerPrimaryStage()
    Platform.runLater(() => TestApplicationView(FxToolkit.toolkitContext().getRegisteredStage))
    WaitForAsyncUtils.waitForFxEvents()
    width = FxToolkit.toolkitContext().getRegisteredStage.getWidth
    height = FxToolkit.toolkitContext().getRegisteredStage.getHeight
  }

  private var width: Double = 0
  private var height: Double = 0
  private val robot = new FxRobot

  import ViewTestHelper._

  describe("in the main menu, when the player clicks play") {
    it("should show the level selection page") {
      val button: Button = getButtonById("playButton")
      robot.clickOn(button)
    }
  }

  describe("in the level selection menu") {
    describe("when the player clicks 1st level") {
      it("should show the 1st level") {
        val level1: Button = robot
          .lookup((b: Button) => b.getText.equals("1"))
          .queryButton()
        robot.clickOn(level1)
      }
    }
  }

  describe("during the game") {
    describe("if the player tries to move the mover cell") {
      it("should be droppable inside the playable area") {
        robot
          .drag(getImageViewByImage(CellImage.MoverRight.image), MouseButton.PRIMARY)
          .dropBy(0, height * 0.2)
          .drop()
      }
      it("should not be droppable outside the playable area") {
        robot
          .drag(getImageViewByImage(CellImage.MoverRight.image), MouseButton.PRIMARY)
          .dropBy(width * 0.2, 0)
      }
    }

    describe("if the player tries to move the enemy cell") {
      it("should not be movable") {
        robot
          .drag(getImageViewByImage(CellImage.Enemy.image), MouseButton.PRIMARY)
          .dropBy(0, -height * 0.2)
      }
    }

    describe("before the simulation starts") {
      it("should be visible and clickable the play button") {
        val playSimulationButton = getButtonById("playSimulationButton")
        playSimulationButton.getText shouldBe "Play"
        playSimulationButton.isVisible shouldBe true
        playSimulationButton.isDisabled shouldBe false
      }
      it("should be visible and clickable the step button") {
        val stepSimulationButton: Button = getButtonById("stepSimulationButton")
        stepSimulationButton.getText shouldBe "Step"
        stepSimulationButton.isVisible shouldBe true
        stepSimulationButton.isDisabled shouldBe false
      }
      it("should not be visible the reset button") {
        val resetButton: Button = getButtonById("resetButton")
        resetButton.getText shouldBe "Reset"
        resetButton.isVisible shouldBe false
        resetButton.isDisabled shouldBe false
      }
      it("should hide the next button") {
        val nextButton: Button = getButtonById("nextButton")
        nextButton.getText shouldBe "Next"
        nextButton.isVisible shouldBe false
        nextButton.isDisabled shouldBe false
      }
    }

    describe("when the player clicks play") {
      it("should start the simulation") {
        robot.clickOn(getButtonById("playSimulationButton"))
        WaitForAsyncUtils.sleep(2, TimeUnit.SECONDS)
      }
      it("should change the text of the simulation button from 'Play' to 'Pause'") {
        getButtonById("playSimulationButton").getText shouldBe "Pause"
      }
      it("shoul disable the step button") {
        getButtonById("stepSimulationButton").isDisabled shouldBe true
      }
      it("should be visible the reset button") {
        getButtonById("resetButton").isVisible shouldBe true
      }
    }

    describe("when the player clicks pause") {
      it("should stop the simulation") {
        robot.clickOn(getButtonById("playSimulationButton"))
      }
      it("should change the text of the simulation button from 'Pause' to 'Play'") {
        getButtonById("playSimulationButton").getText shouldBe "Play"
      }
      it("should enable the step button") {
        getButtonById("stepSimulationButton").isDisabled shouldBe false
      }
      describe("while the simulation is stopped") {
        it("should not be able to move any cells") {
          robot
            .drag(getImageViewByImage(CellImage.MoverRight.image), MouseButton.PRIMARY)
            .dropBy(0, -height * 0.2)
          robot
            .drag(getImageViewByImage(CellImage.Enemy.image), MouseButton.PRIMARY)
            .dropBy(0, -height * 0.2)
        }
      }
    }

    describe("when player clicks play again") {
      it("should restart the simulation") {
        robot.clickOn(getButtonById("playSimulationButton"))
        WaitForAsyncUtils.sleep(3, TimeUnit.SECONDS)
      }
    }

    describe("when the player clicks reset") {
      it("should reset the level") {
        robot.clickOn(getButtonById("resetButton"))
      }
    }

    describe("when the player clicks step") {
      it("should display the simulation step by step") {
        (0 until 3).foreach(_ => {
          robot.clickOn(getButtonById("stepSimulationButton"))
          WaitForAsyncUtils.sleep(500, TimeUnit.MILLISECONDS)
        })
      }
      describe("and then play") {
        it("should start the simulation") {
          robot.clickOn(getButtonById("playSimulationButton"))
          WaitForAsyncUtils.sleep(2, TimeUnit.SECONDS)
        }
      }
    }
  }

  describe("after the level is complete") {
    it("should disable step and play button") {
      getButtonById("playSimulationButton").isDisabled shouldBe true
      getButtonById("stepSimulationButton").isDisabled shouldBe true
    }
    it("should make visible the next button") {
      getButtonById("nextButton").isVisible shouldBe true
    }

    describe("when the player clicks next") {
      it("should show the next level") {
        robot.clickOn(getButtonById("nextButton"))
      }
    }
  }

  describe("when the player clicks back") {
    it("should show the menu") {
      val backToMenuButton: Button = getButtonById("backToMenuButton")
      backToMenuButton.getText shouldBe "Back"
      backToMenuButton.isVisible shouldBe true
      backToMenuButton.isDisabled shouldBe false
      robot.clickOn(backToMenuButton)
      WaitForAsyncUtils.sleep(300, TimeUnit.MILLISECONDS)
    }
  }
}
