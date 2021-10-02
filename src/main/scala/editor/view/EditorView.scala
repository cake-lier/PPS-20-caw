package it.unibo.pps.caw
package editor.view

import common.ViewComponent.AbstractViewComponent
import common.*
import common.model.{Level, PlayableArea, Position}
import common.model.cell.*
import editor.controller.{EditorController, ParentLevelEditorController}
import editor.model.LevelBuilder

import it.unibo.pps.caw.menu.MainMenuController
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.{FXML, FXMLLoader}
import javafx.geometry.{HPos, Insets, VPos}
import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{ClipboardContent, MouseButton, MouseEvent, TransferMode}
import javafx.scene.layout.{FlowPane, GridPane, Pane}
import scalafx.scene.Scene
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

import java.io.File

/** The view that displays the editor.
  *
  * It is responsible of displaying the editor, with its controls and the current level being edited, and capturing the player
  * inputs, relaying them to the [[EditorController]]. After the controller has processed the receiving input, the [[EditorView]]
  * displays the current state of the edited level.
  */
trait EditorView extends ViewComponent[Pane] {

  /** Draws the given [[LevelBuilder]].
    * @param level
    *   the [[LevelBuilder]] to be displayed
    */
  def printLevel(level: LevelBuilder): Unit

  /** Displays the given error message to the player.
    * @param message
    *   the error message to be displayed
    */
  def showError(message: String): Unit
}

/** Updates the editor model when view is modified.
  *
  * Its methods are called when a [[PlayableArea]] is placed to the board or removed from it by the player and when a [[Cell]] is
  * removed by the player.
  */
trait EditorUpdater {

  /** Creates a new [[PlayableArea]], given its upper left [[Position]] and its lower right [[Position]].
    */
  def createPlayableArea(topLeft: Position, downRight: Position): Unit

  /** Removes a [[Cell]] given its current [[Position]]. */
  def removeCell(position: Position): Unit

  /** Removes the [[PlayableArea]]. */
  def removePlayableArea(): Unit
}

/** The abstract view displaying the [[EditorView]].
  *
  * @param scene
  *   the ScalaFX [[Scene]] on which the [[EditorView]] will be drawn
  * @param closeEditorButtonText
  *   the text displayed in the close/back button
  * @param audioPlayer
  *   the [[AudioPlayer]] that will play the editor music
  */
abstract class AbstractEditorView(
  scene: Scene,
  closeEditorButtonText: String,
  audioPlayer: AudioPlayer
) extends AbstractViewComponent[Pane]("editor.fxml")
  with EditorView
  with ModelUpdater
  with EditorUpdater {
  @FXML
  var backButton: Button = _
  @FXML
  var rulesButton: Button = _
  @FXML
  var saveButton: Button = _
  @FXML
  var resetAll: Button = _
  @FXML
  var moverCellView: DraggableImageView = _
  @FXML
  var rotateCellView: DraggableImageView = _
  @FXML
  var generateCellView: DraggableImageView = _
  @FXML
  var blockCellView: DraggableImageView = _
  @FXML
  var enemyCellView: DraggableImageView = _
  @FXML
  var wallCellView: DraggableImageView = _
  @FXML
  var rotateCellsButton: Button = _

  override val innerComponent: GridPane = loader.load[GridPane]

  private val rulesPage = FXMLLoader.load[GridPane](ClassLoader.getSystemResource("fxml/editor_rules.fxml"))
  private var boardView: Option[EditorBoardView] = None
  private var sprites: Map[DraggableImageView, Image] = setButtonImages()

  protected val controller: EditorController

  audioPlayer.play(Track.EditorMusic)
  backButton.setText(closeEditorButtonText)
  backButton.setOnMouseClicked(_ => controller.closeEditor())
  saveButton.setOnMouseClicked(_ => FilePicker.saveFile(scene).foreach(f => controller.saveLevel(f.getPath)))
  rulesButton.setOnMouseClicked(_ => innerComponent.add(rulesPage, 0, 0, 15, 7))
  rulesPage.setOnMouseClicked(_ => innerComponent.getChildren.remove(rulesPage))
  resetAll.setOnMouseClicked(_ => controller.resetLevel())
  rotateCellsButton.setOnMouseClicked(_ => rotateButtons())

  override def printLevel(level: LevelBuilder): Unit = Platform.runLater(() => {
    val newBoardView: EditorBoardView = EditorBoardView(scene.getWidth, scene.getHeight, level, this, this)

    boardView.foreach(b => innerComponent.getChildren.remove(b.innerComponent))
    GridPane.setValignment(newBoardView.innerComponent, VPos.CENTER)
    GridPane.setHalignment(newBoardView.innerComponent, HPos.CENTER)
    GridPane.setMargin(newBoardView.innerComponent, new Insets(25, 0, 25, 0))
    innerComponent.add(newBoardView.innerComponent, 2, 3, 11, 1)
    boardView = Some(newBoardView)
  })

  override def showError(message: String): Unit = Platform.runLater(() => Alert(AlertType.Error, message).showAndWait())

  override def manageCell(cellImage: ImageView, newPosition: Position): Unit = {
    val board = boardView.get.innerComponent
    if (board.getChildren.contains(cellImage)) {
      controller.updateCellPosition(
        Position(GridPane.getColumnIndex(cellImage), GridPane.getRowIndex(cellImage)),
        newPosition
      )
    } else {
      controller.setCell(getSetupCell(cellImage.getImage, newPosition))
    }
  }

  override def createPlayableArea(topLeft: Position, downRight: Position): Unit =
    controller.setPlayableArea(topLeft, (downRight.x - topLeft.x + 1, downRight.y - topLeft.y + 1))

  override def removeCell(position: Position): Unit = controller.removeCell(position)

  override def removePlayableArea(): Unit = controller.removePlayableArea()

  private def setButtonImages(): Map[DraggableImageView, Image] = {
    Map(
      setGraphic(enemyCellView, CellImage.Enemy.image),
      setGraphic(wallCellView, CellImage.Wall.image),
      setGraphic(generateCellView, CellImage.GeneratorRight.image),
      setGraphic(moverCellView, CellImage.MoverRight.image),
      setGraphic(blockCellView, CellImage.Block.image),
      setGraphic(rotateCellView, CellImage.RotatorClockwise.image)
    )
  }

  private def getSetupCell(image: Image, newPosition: Position): BaseCell = image match {
    case CellImage.GeneratorRight.image          => BaseGeneratorCell(newPosition, Orientation.Right)
    case CellImage.GeneratorLeft.image           => BaseGeneratorCell(newPosition, Orientation.Left)
    case CellImage.GeneratorTop.image            => BaseGeneratorCell(newPosition, Orientation.Top)
    case CellImage.GeneratorDown.image           => BaseGeneratorCell(newPosition, Orientation.Down)
    case CellImage.RotatorClockwise.image        => BaseRotatorCell(newPosition, Rotation.Clockwise)
    case CellImage.RotatorCounterclockwise.image => BaseRotatorCell(newPosition, Rotation.Counterclockwise)
    case CellImage.MoverRight.image              => BaseMoverCell(newPosition, Orientation.Right)
    case CellImage.MoverLeft.image               => BaseMoverCell(newPosition, Orientation.Left)
    case CellImage.MoverTop.image                => BaseMoverCell(newPosition, Orientation.Top)
    case CellImage.MoverDown.image               => BaseMoverCell(newPosition, Orientation.Down)
    case CellImage.Block.image                   => BaseBlockCell(newPosition, Push.Both)
    case CellImage.BlockHorizontal.image         => BaseBlockCell(newPosition, Push.Horizontal)
    case CellImage.BlockVertical.image           => BaseBlockCell(newPosition, Push.Vertical)
    case CellImage.Enemy.image                   => BaseEnemyCell(newPosition)
    case CellImage.Wall.image                    => BaseWallCell(newPosition)
  }

  private def setGraphic(buttonCellImageView: DraggableImageView, image: Image): (DraggableImageView, Image) = {
    buttonCellImageView.setImage(image)
    buttonCellImageView.setFitHeight(70)
    buttonCellImageView.setPreserveRatio(true)
    buttonCellImageView -> image
  }

  private def rotateButtons(): Unit = {
    sprites = Map(
      setGraphic(
        rotateCellView,
        sprites(rotateCellView) match {
          case CellImage.RotatorClockwise.image => CellImage.RotatorCounterclockwise.image
          case _                                => CellImage.RotatorClockwise.image
        }
      ),
      setGraphic(
        moverCellView,
        sprites(moverCellView) match {
          case CellImage.MoverRight.image => CellImage.MoverDown.image
          case CellImage.MoverDown.image  => CellImage.MoverLeft.image
          case CellImage.MoverLeft.image  => CellImage.MoverTop.image
          case _                          => CellImage.MoverRight.image
        }
      ),
      setGraphic(
        generateCellView,
        sprites(generateCellView) match {
          case CellImage.GeneratorRight.image => CellImage.GeneratorDown.image
          case CellImage.GeneratorDown.image  => CellImage.GeneratorLeft.image
          case CellImage.GeneratorLeft.image  => CellImage.GeneratorTop.image
          case _                              => CellImage.GeneratorRight.image
        }
      ),
      setGraphic(
        blockCellView,
        sprites(blockCellView) match {
          case CellImage.Block.image         => CellImage.BlockVertical.image
          case CellImage.BlockVertical.image => CellImage.BlockHorizontal.image
          case _                             => CellImage.Block.image
        }
      )
    )
  }
}

/** The companion object of the trait [[EditorView]]. */
object EditorView {
  /* Concrete implementation of an EditorView displaying an empty level. */
  private final class EmptyEditorViewImpl(
    parentLevelEditorController: ParentLevelEditorController,
    scene: Scene,
    closeEditorButtonText: String,
    audioPlayer: AudioPlayer,
    width: Int,
    height: Int
  ) extends AbstractEditorView(scene, closeEditorButtonText, audioPlayer) {
    override val controller: EditorController = EditorController(parentLevelEditorController, this, width, height)
  }

  /* Concrete implementation of an EditorView displaying an existing level. */
  private final class LevelEditorViewImpl(
    parentLevelEditorController: ParentLevelEditorController,
    scene: Scene,
    closeEditorButtonText: String,
    audioPlayer: AudioPlayer,
    level: Level[BaseCell]
  ) extends AbstractEditorView(scene, closeEditorButtonText, audioPlayer) {
    override protected val controller: EditorController = EditorController(
      parentLevelEditorController,
      this,
      level
    )
  }

  /** Returns a new instance of [[EditorView]]. It receives the [[ParentLevelEditorController]] so as to be able to correctly
    * create and then use its [[EditorController]], the ScalaFX [[Scene]] where the [[EditorView]] will draw and display itself,
    * the text that the upper left button will display, depending if the [[EditorView]] was called from the menu or as a its own
    * application and the [[AudioPlayer]] to be used for playing sounds and music for the editor.
    *
    * @param parentLevelEditorController
    *   the controller needed to build the [[EditorController]]
    * @param scene
    *   the ScalaFX [[Scene]] where the [[EditorView]] will be drawn and displayed
    * @param closeEditorButtonText
    *   the text of the upper left button
    * @param audioPlayer
    *   the [[AudioPlayer]] used to play the music and sounds for the editor
    * @param level
    *   the level that the [[EditorView]] will display
    * @return
    *   a new instance of [[EditorView]] displaying an existing level to be edited by the user
    */
  def apply(
    parentLevelEditorController: ParentLevelEditorController,
    scene: Scene,
    closeEditorButtonText: String,
    audioPlayer: AudioPlayer,
    level: Level[BaseCell]
  ): EditorView =
    LevelEditorViewImpl(
      parentLevelEditorController,
      scene,
      closeEditorButtonText,
      audioPlayer,
      level
    )

  /** Returns a new instance of [[EditorView]]. It receives the [[ParentLevelEditorController]] so as to be able to correctly
    * create and then use its [[EditorController]], the ScalaFX [[Scene]] where the [[EditorView]] will draw and display itself,
    * the text that the upper left button will display, depending if the [[EditorView]] was called from the menu or as a its own
    * application and the [[AudioPlayer]] to be used for playing sounds and music for the editor.
    *
    * @param parentLevelEditorController
    *   the controller needed to build the [[EditorController]]
    * @param scene
    *   the ScalaFX [[Scene]] where the [[EditorView]] will be drawn and displayed
    * @param closeEditorButtonText
    *   the text of the upper left button
    * @param audioPlayer
    *   the [[AudioPlayer]] used to play the music and sounds for the editor
    * @param boardWidth
    *   the width of the new empty level
    * @param boardHeight
    *   the height of the new empty level
    * @return
    *   a new instance of [[EditorView]] displaying an new empty level with the given widht and height
    */
  def apply(
    parentLevelEditorController: ParentLevelEditorController,
    scene: Scene,
    closeEditorButtonText: String,
    audioPlayer: AudioPlayer,
    boardWidth: Int,
    boardHeight: Int
  ): EditorView =
    EmptyEditorViewImpl(parentLevelEditorController, scene, closeEditorButtonText, audioPlayer, boardWidth, boardHeight)
}
