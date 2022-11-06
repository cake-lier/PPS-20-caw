package it.unibo.pps.caw
package game.view

import common.BoardViewTest
import common.view.CellImage

import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import org.testfx.api.FxRobot

import scala.jdk.CollectionConverters.given

trait GameTest extends BoardViewTest {

  def getLevels(robot: FxRobot): Set[Button] =
    robot.lookup[Button](_.getText.matches("\\d+")).queryAll[Button]().asScala.toSet

  def clickOnPlayButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "playButton")

  def clickOnLevel(robot: FxRobot): Unit = robot.clickOn[Button](_.getText == "1")

  def moveMoverCell(robot: FxRobot): Unit =
    val gameBoard = getBoard(robot)
    robot
      .drag(getImageView(gameBoard)(CellImage.MoverRight.image), MouseButton.PRIMARY)
      .dropTo(getDropTile(gameBoard)(2, 4))
}
