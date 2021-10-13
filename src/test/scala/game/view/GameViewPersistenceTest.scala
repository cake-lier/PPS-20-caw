package it.unibo.pps.caw.game.view

import it.unibo.pps.caw.app.{ViewTest, TestApplicationView}
import it.unibo.pps.caw.common.view.CellImage
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.junit.jupiter.api.*
import org.testfx.api.{FxRobot, FxToolkit}
import org.testfx.framework.junit5.{Start, Stop}

import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.given

@TestMethodOrder(classOf[MethodOrderer.OrderAnnotation])
class GameViewPersistenceTest extends ViewTest {

  protected var stageWidth: Double = 0.0
  protected var stageHeight: Double = 0.0

  @BeforeAll
  def deleteSettings(): Unit = {
    val settings: Path = Paths.get(System.getProperty("user.home"), ".settings_caw.json")
    if (settings.toFile.exists()) {
      Files.delete(settings)
    }
  }

  @Start
  def start(stage: Stage): Unit = {
    TestApplicationView(stage)
    stageWidth = stage.getWidth
    stageHeight = stage.getHeight
  }

  @Stop
  def stop(): Unit = FxToolkit.hideStage()

  @Test
  @Order(1)
  def checkLevelsNotHighlited(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    val levels: Set[Button] = getLevels(robot)
    levels
      .foreach(b => {
        println(b.getText)
        println(b.getStyleClass.asScala.find(_ == "completed").isEmpty)
        Assertions.assertTrue(b.getStyleClass.asScala.find(_ == "completed").isEmpty)
      })
  }

  @Test
  @Order(2)
  def completeFirstLevel(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    clickOnLevel(robot)
    moveMoverCell(robot)
    (0 until 5).foreach(_ => robot.clickOn(_.getId == "stepSimulationButton"))
    robot.clickOn(_.getId == "backToMenuButton")
  }

  @Test
  @Order(3)
  def checkFirstLevelIsHighlited(robot: FxRobot): Unit = {
    clickOnPlayButton(robot)
    val levels: Set[Button] = getLevels(robot)
    levels
      .foreach(b => {
        println(b.getText)
        println(b.getStyleClass.asScala.find(_ == "completed").isEmpty)
        b.getText match {
          case "1" => Assertions.assertTrue(b.getStyleClass.asScala.find(_ == "completed").isDefined)
          case _   => Assertions.assertTrue(b.getStyleClass.asScala.find(_ == "completed").isEmpty)
        }
      })
  }

  private def getLevels(robot: FxRobot): Set[Button] =
    robot.lookup[Button](_.getText.matches("\\d+")).queryAll[Button]().asScala.toSet

  private def clickOnPlayButton(robot: FxRobot): Unit = robot.clickOn(_.getId == "playButton")

  private def clickOnLevel(robot: FxRobot): Unit = robot.clickOn[Button](_.getText == "1")

  private def moveMoverCell(robot: FxRobot): Unit =
    robot
      .drag(getImageView(getGameBoard(robot))(CellImage.MoverRight.image), MouseButton.PRIMARY)
      .dropBy(0, stageHeight * 0.2)
      .drop()

  private def getGameBoard(robot: FxRobot): GridPane = {
    val gameView: GridPane = robot.lookup[GridPane](_.isInstanceOf[GridPane]).query()
    gameView
      .getChildren
      .asScala
      .find(n => GridPane.getColumnIndex(n) == 2 && GridPane.getRowIndex(n) == 3 && n.isInstanceOf[GridPane])
      .map(_.asInstanceOf[GridPane])
      .get
  }

}
