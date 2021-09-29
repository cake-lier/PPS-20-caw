package it.unibo.pps.caw.editor.view

import it.unibo.pps.caw.common.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.*
import it.unibo.pps.caw.common.model.{Level, Position}
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.editor.controller.{LevelEditorController, ParentLevelEditorController}
import it.unibo.pps.caw.editor.model.LevelBuilder
import it.unibo.pps.caw.common.{AudioPlayer, Track, ViewComponent}
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

trait LevelEditorView extends ViewComponent[Pane] {
  def printLevel(level: LevelBuilder): Unit

  def showError(message: String): Unit
}

trait PlayableAreaUpdater {
  def createPlayableArea(topRight: Position, downLeft: Position): Unit
  def removeCell(position: Position): Unit
  def removePlayableArea(): Unit
}

object LevelEditorView {
  private final class LevelEditorViewImpl(
    parentLevelEditorController: ParentLevelEditorController,
    scene: Scene,
    closeEditorButtonText: String,
    audioPlayer: AudioPlayer,
    width: Int,
    height: Int,
    level: Option[Level[BaseCell]]
  ) extends AbstractViewComponent[Pane]("editor.fxml")
    with LevelEditorView
    with ModelUpdater
    with PlayableAreaUpdater {

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

    private val controller: LevelEditorController = level
      .map(LevelEditorController(parentLevelEditorController, this, _))
      .getOrElse(LevelEditorController(parentLevelEditorController, this, width, height))

    audioPlayer.play(Track.EditorMusic)
    backButton.setText(closeEditorButtonText)
    backButton.setOnMouseClicked(_ => controller.closeEditor())
    saveButton.setOnMouseClicked(_ => FilePicker.saveFile(scene).foreach(f => controller.saveLevel(f.getPath)))
    rulesButton.setOnMouseClicked(_ => {
      innerComponent.add(rulesPage, 0, 0, 15, 7)
    })
    rulesPage.setOnMouseClicked(_ => {
      innerComponent.getChildren.remove(rulesPage)
    })
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

    override def createPlayableArea(topRight: Position, downLeft: Position): Unit = {
      val board = boardView.get.innerComponent
      controller.setPlayableArea(topRight, (downLeft.x - topRight.x + 1, downLeft.y - topRight.y + 1))
    }

    override def removeCell(position: Position): Unit = controller.removeCell(position)

    override def removePlayableArea(): Unit = controller.removePlayableArea()

    private def setButtonImages(): Map[DraggableImageView, Image] = {
      Map(
        setGraphic(enemyCellView, CellImage.Enemy.image),
        setGraphic(wallCellView, CellImage.Wall.image),
        setGraphic(generateCellView, CellImage.GeneratorRight.image),
        setGraphic(moverCellView, CellImage.MoverRight.image),
        setGraphic(blockCellView, CellImage.Block.image),
        setGraphic(rotateCellView, CellImage.RotatorRight.image)
      )
    }

    private def getSetupCell(image: Image, newPosition: Position): BaseCell = image match {
      case CellImage.GeneratorRight.image  => BaseGeneratorCell(newPosition, Orientation.Right)
      case CellImage.GeneratorLeft.image   => BaseGeneratorCell(newPosition, Orientation.Left)
      case CellImage.GeneratorTop.image    => BaseGeneratorCell(newPosition, Orientation.Top)
      case CellImage.GeneratorDown.image   => BaseGeneratorCell(newPosition, Orientation.Down)
      case CellImage.RotatorRight.image    => BaseRotatorCell(newPosition, Rotation.Clockwise)
      case CellImage.RotatorLeft.image     => BaseRotatorCell(newPosition, Rotation.Counterclockwise)
      case CellImage.MoverRight.image      => BaseMoverCell(newPosition, Orientation.Right)
      case CellImage.MoverLeft.image       => BaseMoverCell(newPosition, Orientation.Left)
      case CellImage.MoverTop.image        => BaseMoverCell(newPosition, Orientation.Top)
      case CellImage.MoverDown.image       => BaseMoverCell(newPosition, Orientation.Down)
      case CellImage.Block.image           => BaseBlockCell(newPosition, Push.Both)
      case CellImage.BlockHorizontal.image => BaseBlockCell(newPosition, Push.Horizontal)
      case CellImage.BlockVertical.image   => BaseBlockCell(newPosition, Push.Vertical)
      case CellImage.Enemy.image           => BaseEnemyCell(newPosition)
      case CellImage.Wall.image            => BaseWallCell(newPosition)
    }

    private def setGraphic(buttonCellImageView: DraggableImageView, image: Image): Tuple2[DraggableImageView, Image] = {
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
            case CellImage.RotatorRight.image => CellImage.RotatorLeft.image
            case _                            => CellImage.RotatorRight.image
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

  def apply(
    parentLevelEditorController: ParentLevelEditorController,
    scene: Scene,
    closeEditorButtonText: String,
    audioPlayer: AudioPlayer,
    boardWidth: Int,
    boardHeight: Int
  ): LevelEditorView =
    LevelEditorViewImpl(parentLevelEditorController, scene, closeEditorButtonText, audioPlayer, boardWidth, boardHeight, None)

  def apply(
    parentLevelEditorController: ParentLevelEditorController,
    scene: Scene,
    closeEditorButtonText: String,
    audioPlayer: AudioPlayer,
    level: Level[BaseCell]
  ): LevelEditorView =
    LevelEditorViewImpl(
      parentLevelEditorController,
      scene,
      closeEditorButtonText,
      audioPlayer,
      level.dimensions.width,
      level.dimensions.height,
      Some(level)
    )
}
