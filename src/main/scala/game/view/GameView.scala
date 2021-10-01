package it.unibo.pps.caw.game.view

import it.unibo.pps.caw.common.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.{AudioPlayer, ModelUpdater, Track, ViewComponent}
import it.unibo.pps.caw.common.model.{Board, Level, Position}
import it.unibo.pps.caw.common.model.cell.{BaseCell, PlayableCell}
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
  * capture the user input while playing and should relay it to its controller, the [[GameController]]. After the controller has
  * processed the input, the [[GameView]] should be used to display the current state of the game. It must be constructed through
  * its companion object.
  */
trait GameView extends ViewComponent[GridPane] {

  /** Makes the application view go back to the main menu, displaying it. */
  def backToMenu(): Unit

  /** Displays the given [[Level]], resetting the component displaying the [[Level]] if necessary. This means that this method is
    * to be called when there is a change between [[Level]] in the game and not when an update of the same [[Level]] is to be
    * displayed.
    *
    * @param level
    *   the [[Level]] to display
    * @param isCompleted
    *   whether or not this [[Level]] has been completed
    */
  def drawLevel(level: Level[PlayableCell], isCompleted: Boolean): Unit

  /** Displays the update to the currently displayed [[Level]], without resetting the component displaying it. This means that
    * this method is not to be called when there is a change between [[Level]], but only during steps over the same [[Level]].
    *
    * @param level
    *   the [[Level]] containing the update
    * @param currentBoard
    *   the current [[Board]] to display
    * @param didEnemyExplode
    *   whether or not an enemy exploded after this update
    * @param isCompleted
    *   whether or not this [[Level]] has been completed
    */
  def drawLevelUpdate(
    update: Level[PlayableCell],
    didEnemyExplode: Boolean,
    isCompleted: Boolean
  ): Unit

  /** Displays again the initial configuration of the [[Level]], resetting the [[GameView]].
    *
    * @param level
    *   the initial configuration of the [[Level]]
    */
  def drawLevelReset(level: Level[PlayableCell]): Unit

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
    with GameView
    with ModelUpdater {
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
    audioPlayer.play(Track.GameMusic)

    private def resetButtons(): Unit = {
      playSimulationButton.setText("Start")
      playSimulationButton.setOnMouseClicked(startSimulationHandler)
      playSimulationButton.setDisable(false)
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
      nextButton.setVisible(false)
      resetButtons()
    })
    stepSimulationButton.setOnMouseClicked(_ => {
      controller.step()
      resetButton.setVisible(true)
    })
    playSimulationButton.setOnMouseClicked(startSimulationHandler)
    backToLevelsButton.setOnMouseClicked(_ => controller.closeGame())
    nextButton.setOnMouseClicked(_ => {
      controller.nextLevel()
      resetButton.setVisible(false)
      resetButtons()
    })

    protected def createController(): GameController

    override def showError(message: String): Unit = Platform.runLater(() => Alert(AlertType.ERROR, message))

    override def drawLevelUpdate(
      update: Level[PlayableCell],
      didEnemyExplode: Boolean,
      isCompleted: Boolean
    ): Unit =
      Platform.runLater(() =>
        boardView.foreach(b => {
          b.drawGameBoard(update.board)
          audioPlayer.play(Track.Step)
          if (didEnemyExplode) {
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

    override def drawLevelReset(level: Level[PlayableCell]): Unit =
      Platform.runLater(() => boardView.foreach(_.drawSetupBoard(level.board)))

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
    scene: Scene
  ) extends AbstractGameView(parentController, audioPlayer, scene) {

    override protected def createController(): GameController = GameController(parentController, this, levels, levelIndex)
  }

  /* Extension of AbstractGameView for displaying a generic level. */
  private class ExternalGameView(
    parentController: ParentGameController,
    audioPlayer: AudioPlayer,
    level: Level[BaseCell],
    scene: Scene
  ) extends AbstractGameView(parentController, audioPlayer, scene) {

    override protected def createController(): GameController = GameController(parentController, this, level)
  }

  /** Returns a new instance of the [[GameView]] trait. It receives a [[ParentDefaultGameController]] so as to be able to complete
    * the construction of a [[GameController]] correctly in order to use it, the [[AudioPlayer]] to be used for playing sounds and
    * music, the sequence of default [[Level]] to be used during this game, the index of the default [[Level]] from which starting
    * the game and the ScalaFX'state [[Scene]] on which displaying the instance after being constructed.
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
    *   the ScalaFX'state [[Scene]] on which displaying the instance after being constructed
    * @return
    *   a new [[GameView]] instance
    */
  def apply(
    parentController: ParentDefaultGameController,
    audioPlayer: AudioPlayer,
    levels: Seq[Level[BaseCell]],
    levelIndex: Int,
    scene: Scene
  ): GameView =
    DefaultGameView(parentController, audioPlayer, levels, levelIndex, scene)

  /** Returns a new instance of the [[GameView]] trait. It receives a [[ParentGameController]] so as to be able to complete the
    * construction of a [[GameController]] correctly in order to use it, the [[AudioPlayer]] to be used for playing sounds and
    * music, the [[Level]] from which starting the game and the ScalaFX'state [[Scene]] on which displaying the instance after
    * being constructed.
    *
    * @param parentController
    *   the controller needed so as to be able to complete the construction of a [[GameController]] correctly
    * @param audioPlayer
    *   the [[AudioPlayer]] to be used for playing sounds and music
    * @param level
    *   the [[Level]] from which starting the game
    * @param scene
    *   the ScalaFX'state [[Scene]] on which displaying the instance after being constructed
    * @return
    *   a new [[GameView]] instance
    */
  def apply(parentController: ParentGameController, audioPlayer: AudioPlayer, level: Level[BaseCell], scene: Scene): GameView =
    ExternalGameView(parentController, audioPlayer, level, scene)
}
