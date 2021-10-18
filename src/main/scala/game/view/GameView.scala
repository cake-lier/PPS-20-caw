package it.unibo.pps.caw.game.view

import it.unibo.pps.caw.common.view.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.model.{Board, Level, Position}
import it.unibo.pps.caw.common.model.cell.{BaseCell, PlayableCell}
import it.unibo.pps.caw.common.view.sounds.{AudioPlayer, Track}
import it.unibo.pps.caw.common.view.{ModelUpdater, ViewComponent}
import it.unibo.pps.caw.game.controller.{GameController, ParentDefaultGameController, ParentGameController}
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.{HPos, Insets, VPos}
import javafx.scene.control.{Alert, Button}
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import scalafx.scene.Scene

/** The view which displays the game part of an application.
  *
  * This view is responsible for displaying everything related to a game in an application. This means that this component should
  * capture the user input while playing and should relay it to its controller, the
  * [[it.unibo.pps.caw.game.controller.GameController]]. After the controller has processed the input, the [[GameView]] should be
  * used to display the current state of the game. It must be constructed through its companion object.
  */
trait GameView extends ViewComponent[GridPane] {

  /** Makes the application view go back to the main menu, displaying it. */
  def backToMenu(): Unit

  /** Displays the given [[it.unibo.pps.caw.common.model.Level]], resetting the component displaying it. This means that this
    * method is to be called when there is a change between level in the game and not when an update of the same level is to be
    * displayed.
    *
    * @param level
    *   the [[it.unibo.pps.caw.common.model.Level]] to display
    * @param isCompleted
    *   whether or not this [[it.unibo.pps.caw.common.model.Level]] has been completed
    */
  def drawLevel(level: Level[PlayableCell], isCompleted: Boolean): Unit

  /** Displays an update to the [[it.unibo.pps.caw.common.model.Board]] of the currently displayed
    * [[it.unibo.pps.caw.common.model.Level]]. This means that this method is not to be called when there is a change between
    * levels, but only during update steps over the same level.
    *
    * @param board
    *   the updated [[it.unibo.pps.caw.common.model.Board]] to be displayed
    * @param didEnemyDie
    *   whether or not an enemy died after this update
    * @param isCompleted
    *   whether or not this [[it.unibo.pps.caw.common.model.Level]] has been completed
    */
  def drawPlayBoard(
    board: Board[BaseCell],
    didEnemyDie: Boolean,
    isCompleted: Boolean
  ): Unit

  /** Displays the initial configuration of the [[it.unibo.pps.caw.common.model.Board]] of the currently displayed
    * [[it.unibo.pps.caw.common.model.Level]], actually resetting this view.
    *
    * @param board
    *   the initial configuration of the [[it.unibo.pps.caw.common.model.Board]] to be displayed
    */
  def drawSetupBoard(board: Board[PlayableCell]): Unit

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
  private abstract class AbstractGameView(
    parentController: ParentGameController,
    audioPlayer: AudioPlayer,
    scene: Scene,
    backButtonText: String
  ) extends AbstractViewComponent[GridPane]("game.fxml")
    with GameView
    with ModelUpdater {
    @FXML
    var resetButton: Button = _
    @FXML
    var stepSimulationButton: Button = _
    @FXML
    var playSimulationButton: Button = _
    @FXML
    var backToMenuButton: Button = _
    @FXML
    var nextButton: Button = _

    override val innerComponent: GridPane = loader.load[GridPane]

    private val controller: GameController = createController()
    private var boardView: Option[GameBoardView] = None

    audioPlayer.play(Track.GameMusic)

    /* Resets the "play" and "step" buttons. */
    private def resetButtons(): Unit = {
      playSimulationButton.setText("Play")
      playSimulationButton.setOnMouseClicked(startSimulationHandler)
      playSimulationButton.setDisable(false)
      stepSimulationButton.setDisable(false)
    }

    private var startSimulationHandler: EventHandler[MouseEvent] = _ => {
      controller.startUpdates()
      playSimulationButton.setText("Pause")
      playSimulationButton.setOnMouseClicked(endSimulationHandler)
      stepSimulationButton.setDisable(true)
      resetButton.setVisible(true)
    }
    private var endSimulationHandler: EventHandler[MouseEvent] = _ => {
      controller.pauseUpdates()
      resetButtons()
    }

    resetButton.setOnMouseClicked(_ => {
      controller.resetLevel()
      resetButton.setVisible(false)
      nextButton.setVisible(false)
      resetButtons()
    })
    stepSimulationButton.setOnMouseClicked(_ => {
      controller.step()
      resetButton.setVisible(true)
    })
    playSimulationButton.setOnMouseClicked(startSimulationHandler)
    backToMenuButton.setText(backButtonText)
    backToMenuButton.setOnMouseClicked(_ => controller.closeGame())
    nextButton.setOnMouseClicked(_ => {
      controller.nextLevel()
      resetButton.setVisible(false)
      resetButtons()
    })

    /* Creates the GameController to be used by this GameView instance. */
    protected def createController(): GameController

    override def showError(message: String): Unit = Platform.runLater(() => Alert(AlertType.ERROR, message))

    override def drawPlayBoard(
      board: Board[BaseCell],
      didEnemyDie: Boolean,
      isCompleted: Boolean
    ): Unit =
      Platform.runLater(() =>
        boardView.foreach(b => {
          b.drawGameBoard(board)
          audioPlayer.play(Track.Step)
          if (didEnemyDie) {
            audioPlayer.play(Track.Explosion)
          }
          if (isCompleted) {
            audioPlayer.play(Track.Victory)
            stepSimulationButton.setDisable(true)
            playSimulationButton.setDisable(true)
          }
          nextButton.setVisible(isCompleted)
        })
      )

    override def drawSetupBoard(board: Board[PlayableCell]): Unit =
      Platform.runLater(() => boardView.foreach(_.drawSetupBoard(board)))

    override def drawLevel(level: Level[PlayableCell], isCompleted: Boolean): Unit = Platform.runLater(() => {
      val newBoardView: GameBoardView = GameBoardView(scene.getWidth, scene.getHeight, level, this)
      boardView.foreach(b => innerComponent.getChildren.remove(b.innerComponent))
      GridPane.setValignment(newBoardView.innerComponent, VPos.CENTER)
      GridPane.setHalignment(newBoardView.innerComponent, HPos.CENTER)
      GridPane.setMargin(newBoardView.innerComponent, Insets(25, 0, 25, 0))
      innerComponent.add(newBoardView.innerComponent, 2, 3, 3, 1)
      boardView = Some(newBoardView)
      if (isCompleted) {
        stepSimulationButton.setDisable(true)
        playSimulationButton.setDisable(true)
      }
      nextButton.setVisible(isCompleted)
    })

    override def backToMenu(): Unit = controller.closeGame()

    override def manageCell(cellImageView: ImageView, newPosition: Position): Unit = {
      val board = boardView.get.innerComponent
      controller.moveCell(Position(GridPane.getColumnIndex(cellImageView), GridPane.getRowIndex(cellImageView)))(newPosition)
      board.getChildren.remove(cellImageView)
      board.add(cellImageView, newPosition.x, newPosition.y)
    }
  }

  /* Extension of AbstractGameView for displaying default levels. */
  private class DefaultGameView(
    parentController: ParentDefaultGameController,
    audioPlayer: AudioPlayer,
    levels: Seq[Level[BaseCell]],
    levelIndex: Int,
    scene: Scene,
    backButtonText: String
  ) extends AbstractGameView(parentController, audioPlayer, scene, backButtonText) {

    override protected def createController(): GameController = GameController(parentController, this, levels, levelIndex)
  }

  /* Extension of AbstractGameView for displaying a generic level. */
  private class ExternalGameView(
    parentController: ParentGameController,
    audioPlayer: AudioPlayer,
    level: Level[BaseCell],
    scene: Scene,
    backButtonText: String
  ) extends AbstractGameView(parentController, audioPlayer, scene, backButtonText) {

    override protected def createController(): GameController = GameController(parentController, this, level)
  }

  /** Returns a new instance of the [[GameView]] trait. It receives a
    * [[it.unibo.pps.caw.game.controller.ParentDefaultGameController]] so as to be able to complete the construction of a
    * [[it.unibo.pps.caw.game.controller.GameController]] correctly in order to use it, the
    * [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used for playing sounds and music, the sequence of default
    * [[it.unibo.pps.caw.common.model.Level]] to be used during this game, the index of the default level from which starting the
    * game and the ScalaFX [[scalafx.scene.Scene]] on which displaying the instance after being constructed.
    *
    * @param parentController
    *   the controller needed so as to be able to complete the construction of a
    *   [[it.unibo.pps.caw.game.controller.GameController]] correctly
    * @param audioPlayer
    *   the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used for playing sounds and music
    * @param levels
    *   the sequence of default [[it.unibo.pps.caw.common.model.Level]] to be used during this game
    * @param levelIndex
    *   the index of the default level from which starting the game
    * @param scene
    *   the ScalaFX [[scalafx.scene.Scene]] on which displaying the instance after being constructed
    * @return
    *   a new [[GameView]] instance
    */
  def apply(
    parentController: ParentDefaultGameController,
    audioPlayer: AudioPlayer,
    levels: Seq[Level[BaseCell]],
    levelIndex: Int,
    scene: Scene,
    backButtonText: String
  ): GameView =
    DefaultGameView(parentController, audioPlayer, levels, levelIndex, scene, backButtonText)

  /** Returns a new instance of the [[GameView]] trait. It receives a [[it.unibo.pps.caw.game.controller.ParentGameController]] so
    * as to be able to complete the construction of a [[it.unibo.pps.caw.game.controller.GameController]] correctly in order to
    * use it, the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used for playing sounds and music, the
    * [[it.unibo.pps.caw.common.model.Level]] from which starting the game and the ScalaFX [[scalafx.scene.Scene]] on which
    * displaying the instance after being constructed.
    *
    * @param parentController
    *   the controller needed so as to be able to complete the construction of a
    *   [[it.unibo.pps.caw.game.controller.GameController]] correctly
    * @param audioPlayer
    *   the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used for playing sounds and music
    * @param level
    *   the [[it.unibo.pps.caw.common.model.Level]] from which starting the game
    * @param scene
    *   the ScalaFX [[scalafx.scene.Scene]] on which displaying the instance after being constructed
    * @return
    *   a new [[GameView]] instance
    */
  def apply(
    parentController: ParentGameController,
    audioPlayer: AudioPlayer,
    level: Level[BaseCell],
    scene: Scene,
    backButtonText: String
  ): GameView =
    ExternalGameView(parentController, audioPlayer, level, scene, backButtonText)
}
