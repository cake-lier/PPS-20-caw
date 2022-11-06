package it.unibo.pps.caw
package common

import app.ViewTest

import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.GridPane
import org.testfx.api.FxRobot

import scala.jdk.CollectionConverters.given

trait BoardViewTest extends ViewTest {
  def getBoard(robot: FxRobot): GridPane = robot.lookup(_.getId == "board").query[GridPane]

  def getDropTile(board: GridPane)(x: Int, y: Int): ImageView =
    board
      .getChildren
      .asScala
      .find(n => GridPane.getColumnIndex(n) == x && GridPane.getRowIndex(n) == y && n.isInstanceOf[ImageView])
      .map(_.asInstanceOf[ImageView])
      .get

  def getImageView(board: GridPane)(image: Image): ImageView =
    board
      .getChildren
      .asScala
      .filter(e => e.isInstanceOf[ImageView])
      .map(_.asInstanceOf[ImageView])
      .find(_.getImage == image)
      .get

  def getImageViewByImage(image: Image)(robot: FxRobot): ImageView =
    robot.lookup[ImageView](_.getImage == image).query()
}
