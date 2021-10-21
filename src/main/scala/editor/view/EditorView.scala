package it.unibo.pps.caw.editor.view

import it.unibo.pps.caw.common.model.{Level, Position}
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.view.CellImage.*
import it.unibo.pps.caw.common.view.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.view.sounds.{AudioPlayer, Track}
import it.unibo.pps.caw.common.view.{CellImage, DraggableImageView, FilePicker, ModelUpdater, ViewComponent}
import it.unibo.pps.caw.editor.controller.{EditorController, ParentEditorController}
import it.unibo.pps.caw.editor.model.EditorModelState
import javafx.application.Platform
import javafx.fxml.{FXML, FXMLLoader}
import javafx.geometry.{HPos, Insets, VPos}
import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

/** The view that displays the editor.
  *
  * It is responsible of displaying the editor, with its controls and the current level being edited, and capturing the player
  * inputs, relaying them to the [[it.unibo.pps.caw.editor.controller.EditorController]]. After the controller has processed the
  * receiving input, the [[EditorView]] displays the current state of the edited level. It must be constructed through its
  * companion object.
  */
trait EditorView extends ViewComponent[Pane] {

  /** Draws the given [[it.unibo.pps.caw.editor.model.EditorModelState]].
    *
    * @param state
    *   the [[it.unibo.pps.caw.editor.model.EditorModelState]] to be displayed
    */
  def drawState(state: EditorModelState): Unit

  /** Displays the given error message to the player.
    * @param message
    *   the error message to be displayed
    */
  def showError(message: String): Unit
}

/** Updates the editor model when view is modified.
  *
  * Its methods are called when a [[it.unibo.pps.caw.common.model.PlayableArea]] is placed to the board or removed from it by the
  * player and when a [[it.unibo.pps.caw.common.model.cell.Cell]] is removed by the player.
  */
trait EditorUpdater {

  /** Creates a new [[it.unibo.pps.caw.common.model.PlayableArea]], given its upper left
    * [[it.unibo.pps.caw.common.model.Position]] and its lower right [[it.unibo.pps.caw.common.model.Position]].
    * @param topLeft
    *   the upper-left position of the [[it.unibo.pps.caw.common.model.PlayableArea]]
    * @param downRight
    *   the lowest-right position of the [[it.unibo.pps.caw.common.model.PlayableArea]]
    */
  def createPlayableArea(topLeft: Position, downRight: Position): Unit

  /** Removes a [[it.unibo.pps.caw.common.model.cell.Cell]] given its current [[it.unibo.pps.caw.common.model.Position]].
    * @param position
    *   the [[it.unibo.pps.caw.common.model.Position]] of the [[it.unibo.pps.caw.common.model.cell.Cell]] to be removed
    */
  def removeCell(position: Position): Unit

  /** Removes the [[it.unibo.pps.caw.common.model.PlayableArea]]. */
  def removePlayableArea(): Unit
}

/** The abstract view displaying the [[EditorView]].
  *
  * @param scene
  *   the ScalaFX [[scalafx.scene.Scene]] on which the [[EditorView]] will be drawn
  * @param backButtonText
  *   the text displayed in the close/back button
  * @param audioPlayer
  *   the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] that will play the editor music
  */
abstract class AbstractEditorView(
  scene: Scene,
  backButtonText: String,
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
  var deleterCellView: DraggableImageView = _
  @FXML
  var rotateCellsButton: Button = _

  override val innerComponent: GridPane = loader.load[GridPane]

  private val rulesPage = FXMLLoader.load[GridPane](ClassLoader.getSystemResource("fxml/editor_rules.fxml"))
  private var boardView: Option[EditorBoardView] = None
  private var sprites: Map[DraggableImageView, Image] = setButtonImages()
  private val controller: EditorController = createController()

  protected def createController(): EditorController

  audioPlayer.play(Track.EditorMusic)
  backButton.setText(backButtonText)
  backButton.setOnMouseClicked(_ => controller.closeEditor())

  private val levelFilePicker: FilePicker = FilePicker.forLevelFile(scene)

  saveButton.setOnMouseClicked(_ => levelFilePicker.saveFile().foreach(controller.saveLevel))
  rulesButton.setOnMouseClicked(_ => innerComponent.add(rulesPage, 0, 0, 15, 7))
  rulesPage.setOnMouseClicked(_ => innerComponent.getChildren.remove(rulesPage))
  resetAll.setOnMouseClicked(_ => controller.resetLevel())
  rotateCellsButton.setOnMouseClicked(_ => rotateButtons())

  override def drawState(state: EditorModelState): Unit = Platform.runLater(() =>
    boardView match {
      case Some(v) => v.drawState(state)
      case None =>
        val newBoardView: EditorBoardView = EditorBoardView(scene.width.value, scene.height.value, state, this, this)
        boardView.foreach(b => innerComponent.getChildren.remove(b))
        GridPane.setValignment(newBoardView, VPos.CENTER)
        GridPane.setHalignment(newBoardView, HPos.CENTER)
        GridPane.setMargin(newBoardView, Insets(25, 0, 25, 0))
        innerComponent.add(newBoardView, 2, 3, 10, 1)
        boardView = Some(newBoardView)
    }
  )

  override def showError(message: String): Unit = Platform.runLater(() => Alert(AlertType.Error, message).showAndWait())

  override def manageCell(cellImage: ImageView, newPosition: Position): Unit =
    if (boardView.get.getChildren.contains(cellImage))
      controller.updateCellPosition(Position(GridPane.getColumnIndex(cellImage), GridPane.getRowIndex(cellImage)), newPosition)
    else
      controller.addCell(getSetupCell(cellImage.getImage, newPosition))

  override def createPlayableArea(topLeft: Position, downRight: Position): Unit =
    controller.addPlayableArea(topLeft, (downRight.x - topLeft.x + 1, downRight.y - topLeft.y + 1))

  override def removeCell(position: Position): Unit = controller.removeCell(position)

  override def removePlayableArea(): Unit = controller.removePlayableArea()

  private def setButtonImages(): Map[DraggableImageView, Image] =
    Map(
      setGraphic(enemyCellView, CellImage.Enemy.image),
      setGraphic(wallCellView, CellImage.Wall.image),
      setGraphic(generateCellView, CellImage.GeneratorRight.image),
      setGraphic(moverCellView, CellImage.MoverRight.image),
      setGraphic(blockCellView, CellImage.Block.image),
      setGraphic(rotateCellView, CellImage.RotatorClockwise.image),
      setGraphic(deleterCellView, CellImage.Deleter.image)
    )

  private def getSetupCell(image: Image, newPosition: Position): BaseCell = image match {
    case CellImage.GeneratorRight.image          => BaseGeneratorCell(Orientation.Right)(newPosition)
    case CellImage.GeneratorLeft.image           => BaseGeneratorCell(Orientation.Left)(newPosition)
    case CellImage.GeneratorTop.image            => BaseGeneratorCell(Orientation.Top)(newPosition)
    case CellImage.GeneratorDown.image           => BaseGeneratorCell(Orientation.Down)(newPosition)
    case CellImage.RotatorClockwise.image        => BaseRotatorCell(Rotation.Clockwise)(newPosition)
    case CellImage.RotatorCounterclockwise.image => BaseRotatorCell(Rotation.Counterclockwise)(newPosition)
    case CellImage.MoverRight.image              => BaseMoverCell(Orientation.Right)(newPosition)
    case CellImage.MoverLeft.image               => BaseMoverCell(Orientation.Left)(newPosition)
    case CellImage.MoverTop.image                => BaseMoverCell(Orientation.Top)(newPosition)
    case CellImage.MoverDown.image               => BaseMoverCell(Orientation.Down)(newPosition)
    case CellImage.Block.image                   => BaseBlockCell(Push.Both)(newPosition)
    case CellImage.BlockHorizontal.image         => BaseBlockCell(Push.Horizontal)(newPosition)
    case CellImage.BlockVertical.image           => BaseBlockCell(Push.Vertical)(newPosition)
    case CellImage.Enemy.image                   => BaseEnemyCell(newPosition)
    case CellImage.Wall.image                    => BaseWallCell(newPosition)
    case CellImage.Deleter.image                 => BaseDeleterCell(newPosition)
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

/** The companion object of the trait [[EditorView]], containing its factory methods. */
object EditorView {

  /* Concrete implementation of an EditorView displaying an empty level. */
  private final class EmptyEditorViewImpl(
    parentLevelEditorController: ParentEditorController,
    scene: Scene,
    backButtonText: String,
    audioPlayer: AudioPlayer,
    width: Int,
    height: Int
  ) extends AbstractEditorView(scene, backButtonText, audioPlayer) {

    override protected def createController(): EditorController =
      EditorController(parentLevelEditorController, this, width, height)
  }

  /* Concrete implementation of an EditorView displaying an existing level. */
  private final class LevelEditorViewImpl(
    parentLevelEditorController: ParentEditorController,
    scene: Scene,
    backButtonText: String,
    audioPlayer: AudioPlayer,
    level: Level[BaseCell]
  ) extends AbstractEditorView(scene, backButtonText, audioPlayer) {

    override protected def createController(): EditorController =
      EditorController(parentLevelEditorController, this, level)
  }

  /** Returns a new instance of [[EditorView]]. It receives the [[it.unibo.pps.caw.editor.controller.ParentEditorController]] so
    * as to be able to correctly create and then use its [[it.unibo.pps.caw.editor.controller.EditorController]], the ScalaFX
    * [[scalafx.scene.Scene]] where the [[EditorView]] will draw and display itself, the text that the upper left button will
    * display, depending if the [[EditorView]] was called from the menu or as a its own application and the
    * [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used for playing sounds and music for the editor.
    *
    * @param parentLevelEditorController
    *   the controller needed to build the [[it.unibo.pps.caw.editor.controller.EditorController]]
    * @param scene
    *   the ScalaFX [[scalafx.scene.Scene]] where the [[EditorView]] will be drawn and displayed
    * @param backButtonText
    *   the text of the upper left button
    * @param audioPlayer
    *   the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] used to play the music and sounds for the editor
    * @param level
    *   the level that the [[EditorView]] will display
    * @return
    *   a new instance of [[EditorView]] displaying an existing level to be edited by the user
    */
  def apply(
    parentLevelEditorController: ParentEditorController,
    scene: Scene,
    backButtonText: String,
    audioPlayer: AudioPlayer,
    level: Level[BaseCell]
  ): EditorView =
    LevelEditorViewImpl(
      parentLevelEditorController,
      scene,
      backButtonText,
      audioPlayer,
      level
    )

  /** Returns a new instance of [[EditorView]]. It receives the [[it.unibo.pps.caw.editor.controller.ParentEditorController]] so
    * as to be able to correctly create and then use its [[it.unibo.pps.caw.editor.controller.EditorController]], the ScalaFX
    * [[scalafx.scene.Scene]] where the [[EditorView]] will draw and display itself, the text that the upper left button will
    * display, depending if the [[EditorView]] was called from the menu or as a its own application and the
    * [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used for playing sounds and music for the editor.
    *
    * @param parentLevelEditorController
    *   the controller needed to build the [[it.unibo.pps.caw.editor.controller.EditorController]]
    * @param scene
    *   the ScalaFX [[scalafx.scene.Scene]] where the [[EditorView]] will be drawn and displayed
    * @param backButtonText
    *   the text of the upper left button
    * @param audioPlayer
    *   the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] used to play the music and sounds for the editor
    * @param boardWidth
    *   the width of the new empty level
    * @param boardHeight
    *   the height of the new empty level
    * @return
    *   a new instance of [[EditorView]] displaying an new empty level with the given widht and height
    */
  def apply(
    parentLevelEditorController: ParentEditorController,
    scene: Scene,
    backButtonText: String,
    audioPlayer: AudioPlayer,
    boardWidth: Int,
    boardHeight: Int
  ): EditorView =
    EmptyEditorViewImpl(parentLevelEditorController, scene, backButtonText, audioPlayer, boardWidth, boardHeight)
}
