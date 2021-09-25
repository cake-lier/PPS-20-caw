package it.unibo.pps.caw
package game.view

import it.unibo.pps.caw.{AudioPlayer, Track, ViewComponent}
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.{Board, BoardView, GameBoardView, ModelUpdater, Position}
import it.unibo.pps.caw.game.model.{Cell, Level}
import it.unibo.pps.caw.game.controller.{GameController, ParentGameController}
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.{HPos, Insets, VPos}
import javafx.scene.control.{Alert, Button}
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{GridPane, Pane}
import scalafx.collections.ObservableBuffer.Update
import scalafx.scene.Scene

import java.io.File
import java.nio.file.Path

/** The view which displays the game part of an application.
  *
  * This view is responsible for displaying everything related to a game in an application. This means that this component should
  * capture the user input while playing and should relay it to its controller, the [[GameController]]. After the controller has
  * processed the input, the [[GameView]] should be used to display the current state of the game. It must be constructed through
  * its companion object.
  */
trait GameView extends ViewComponent[GridPane] with ModelUpdater {

  /** Makes the application view go back to the main menu, displaying it. */
  def backToMenu(): Unit

  /** Displays the given [[Level]], resetting the component displaying the [[Level]] if necessary. This means that thi method is
    * to be called when there is a change between [[Level]] in the game and not when an update of the same [[Level]] is to be
    * displayed.
    *
    * @param level
    *   the [[Level]] to display
    * @param isCompleted
    *   whether or not this [[Level]] has been completed
    */
  def drawLevel(level: Level, isCompleted: Boolean): Unit

  /** Displays the update to the currently displayed [[Level]], without resetting the component displaying it. This means that
    * this method is not to be called when there is a change between [[Level]], but only during steps over the same [[Level]].
    *
    * @param level
    *   the [[Level]] containing the update
    * @param isCompleted
    *   whether or not this [[Level]] has been completed
    */
  def drawLevelUpdate(level: Level, currentBoard: Board[Cell], isCompleted: Boolean): Unit

  /** Displays again the initial configuration of the [[Level]], resetting the [[GameView]].
    *
    * @param level
    *   the initial configuration of the [[Level]]
    */
  def drawLevelReset(level: Level): Unit

  /** Displays the given error message to the player.
    *
    * @param message
    *   the error message to display
    */
  def showError(message: String): Unit
}

/** Companion object of the [[GameView]] trait, containing its factory method. */
object GameView {

  /* Abstract implementation of the GameView trait for factorizing common behaviors. */
  private abstract class AbstractGameView(parentController: ParentGameController, audioPlayer: AudioPlayer, scene: Scene)
    extends AbstractViewComponent[GridPane]("game.fxml")
    with GameView {
    @FXML
    var resetButton: Button = _
    @FXML
    var stepSimulationButton: Button = _
    @FXML
    var playSimulationButton: Button = _
    @FXML
    var backToLevelsButton: Button = _
    @FXML
    var nextButton: Button = _
    override val innerComponent: GridPane = loader.load[GridPane]
    private val controller: GameController = createController()
    private var boardView: Option[GameBoardView] = None
    private var isCurrentLevelCompleted: Boolean = false
    audioPlayer.play(Track.GameMusic)

    private def resetButtons(): Unit = {
      playSimulationButton.setText("Start")
      playSimulationButton.setOnMouseClicked(startSimulationHandler)
      stepSimulationButton.setDisable(false)
    }

    var startSimulationHandler: EventHandler[MouseEvent] = _ => {
      controller.startUpdates()
      playSimulationButton.setText("Pause")
      playSimulationButton.setOnMouseClicked(endSimulationHandler)
      stepSimulationButton.setDisable(true)
      resetButton.setVisible(true)
    }
    var endSimulationHandler: EventHandler[MouseEvent] = _ => {
      controller.pauseUpdates()
      resetButtons()
    }
    resetButton.setOnMouseClicked(_ => {
      controller.resetLevel()
      resetButton.setVisible(false)
      resetButtons()
    })
    stepSimulationButton.setOnMouseClicked(_ => {
      controller.step()
      resetButton.setVisible(true)
    })
    playSimulationButton.setOnMouseClicked(startSimulationHandler)
    backToLevelsButton.setOnMouseClicked(_ => controller.goBack())
    nextButton.setOnMouseClicked(_ => {
      controller.nextLevel()
      resetButton.setVisible(false)
      resetButtons()
    })

    protected def createController(): GameController

    override def showError(message: String): Unit = Platform.runLater(() => Alert(AlertType.ERROR, message))

    override def drawLevelUpdate(level: Level, currentBoard: Board[Cell], isCompleted: Boolean): Unit = Platform.runLater(() =>
      boardView match {
        case Some(b) => {
          b.drawGameBoard(currentBoard)
          if (!isCurrentLevelCompleted && isCompleted) {
            audioPlayer.play(Track.Victory)
          }
          isCurrentLevelCompleted = isCompleted
          nextButton.setVisible(isCompleted)
        }
        case None => Console.err.print("The board was not initialized")
      }
    )

    override def drawLevelReset(level: Level): Unit = Platform.runLater(() => {
      boardView match {
        case Some(b) => b.drawSetupBoard(level.setupBoard)
        case None    => Console.err.print("The board was not initialized")
      }
    })

    override def drawLevel(level: Level, isCompleted: Boolean): Unit = Platform.runLater(() => {
      val newBoardView: GameBoardView = GameBoardView(level, this)
      boardView.foreach(b => innerComponent.getChildren.remove(b.innerComponent))
      GridPane.setValignment(newBoardView.innerComponent, VPos.CENTER)
      GridPane.setHalignment(newBoardView.innerComponent, HPos.CENTER)
      GridPane.setMargin(newBoardView.innerComponent, new Insets(25, 0, 25, 0))
      innerComponent.add(newBoardView.innerComponent, 2, 3, 3, 1)
      boardView = Some(newBoardView)
      isCurrentLevelCompleted = false
      nextButton.setVisible(false)
    })

    override def backToMenu(): Unit = controller.goBack()

    override def manageCell(cell: ImageView, newPosition: Position): Unit = {
      val board = boardView.get.innerComponent
      board.getChildren.remove(cell)
      board.add(cell, newPosition.x, newPosition.y)
      controller.updateModel(Position(GridPane.getColumnIndex(cell), GridPane.getRowIndex(cell)), newPosition)
    }
  }

  /* Extension of AbstractGameView for displaying default levels. */
  private class DefaultGameView(
    parentController: ParentGameController,
    audioPlayer: AudioPlayer,
    levels: Seq[Level],
    levelIndex: Int,
    scene: Scene
  ) extends AbstractGameView(parentController, audioPlayer, scene) {
    override protected def createController(): GameController = GameController(parentController, this, levels, levelIndex)
  }

  /* Extension of AbstractGameView for displaying a generic level. */
  private class ExternalGameView(parentController: ParentGameController, audioPlayer: AudioPlayer, level: Level, scene: Scene)
    extends AbstractGameView(parentController, audioPlayer, scene) {
    override protected def createController(): GameController = GameController(parentController, this, level)
  }

  /** Returns a new instance of the [[GameView]] trait. It receives a [[ParentGameController]] so as to be able to complete the
    * construction of a [[GameController]] correctly in order to use it, the [[AudioPlayer]] to be used for playing sounds and
    * music, the sequence of default [[Level]] to be used during this game, the index of the default [[Level]] from which starting
    * the game and the ScalaFX's [[Scene]] on which displaying the instance after being constructed.
    *
    * @param parentController
    *   the controller needed so as to be able to complete the construction of a [[GameController]] correctly
    * @param audioPlayer
    *   the [[AudioPlayer]] to be used for playing sounds and music
    * @param levels
    *   the sequence of default [[Level]] to be used during this game
    * @param levelIndex
    *   the index of the default level from which starting the game
    * @param scene
    *   the ScalaFX's [[Scene]] on which displaying the instance after being constructed
    * @return
    *   a new [[GameView]] instance
    */
  def apply(
    parentController: ParentGameController,
    audioPlayer: AudioPlayer,
    levels: Seq[Level],
    levelIndex: Int,
    scene: Scene
  ): GameView =
    DefaultGameView(parentController, audioPlayer, levels, levelIndex, scene)

  /** Returns a new instance of the [[GameView]] trait. It receives a [[ParentGameController]] so as to be able to complete the
    * construction of a [[GameController]] correctly in order to use it, the [[AudioPlayer]] to be used for playing sounds and
    * music, the [[Level]] from which starting the game and the ScalaFX's [[Scene]] on which displaying the instance after being
    * constructed.
    *
    * @param parentController
    *   the controller needed so as to be able to complete the construction of a [[GameController]] correctly
    * @param audioPlayer
    *   the [[AudioPlayer]] to be used for playing sounds and music
    * @param level
    *   the [[Level]] from which starting the game
    * @param scene
    *   the ScalaFX's [[Scene]] on which displaying the instance after being constructed
    * @return
    *   a new [[GameView]] instance
    */
  def apply(parentController: ParentGameController, audioPlayer: AudioPlayer, level: Level, scene: Scene): GameView =
    ExternalGameView(parentController, audioPlayer, level, scene)
}
