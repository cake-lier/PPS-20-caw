package it.unibo.pps.caw.editor

import it.unibo.pps.caw.{FilePicker, ViewComponent}
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.{Board, BoardView, CellImage, DragAndDrop, EditorBoardView, ModelUpdater, Position, TileView}
import it.unibo.pps.caw.editor.controller.{LevelEditorController, ParentLevelEditorController}
import it.unibo.pps.caw.editor.model.*
import it.unibo.pps.caw.editor.view.CellView
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.{HPos, Insets, VPos}
import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene
import scala.jdk.StreamConverters
import scala.jdk.StreamConverters.given

import java.io.File

trait LevelEditorView extends ViewComponent[Pane] {
  def createBoard(level: Level): Unit
  def updateLevel(level: Level, isPlayableAreaSet: Boolean): Unit
}

trait PlayableAreaUpdater {
  def createPlayableArea(topRight: Position, downLeft: Position): Unit
}

object LevelEditorView {
  private final class LevelEditorViewImpl(
    parentLevelEditorController: ParentLevelEditorController,
    scene: Scene,
    closeEditorButtonText: String,
    width: Int,
    height: Int,
    level: Option[Level]
  ) extends AbstractViewComponent[Pane]("editor.fxml")
    with LevelEditorView
    with ModelUpdater
    with PlayableAreaUpdater {

    @FXML
    var backButton: Button = _
    @FXML
    var saveButton: Button = _
    @FXML
    var resetAll: Button = _
    @FXML
    var moverCellView: ImageView = _
    @FXML
    var rotateCellView: ImageView = _
    @FXML
    var generateCellView: ImageView = _
    @FXML
    var blockCellView: ImageView = _
    @FXML
    var enemyCellView: ImageView = _
    @FXML
    var wallCellView: ImageView = _
    @FXML
    var playAreaCellView: ImageView = _
    @FXML
    var rotateCellsButton: Button = _
    @FXML
    var levelEditorMenuButton: Button = _

    override val innerComponent: GridPane = loader.load[GridPane]

    private var boardView: Option[EditorBoardView] = None

    private var sprites: Map[ImageView, Image] = setButtonImages()

    private val controller: LevelEditorController = level
      .map(LevelEditorController(parentLevelEditorController, this, _))
      .getOrElse(LevelEditorController(parentLevelEditorController, this, width, height))

    levelEditorMenuButton.setOnMouseClicked(_ => controller.backToLevelEditorMenu())
    backButton.setText(closeEditorButtonText)
    backButton.setOnMouseClicked(_ => controller.closeEditor())
    saveButton.setOnMouseClicked(_ => FilePicker.saveFile(scene).foreach(controller.saveLevel))
    resetAll.setOnMouseClicked(_ => controller.resetLevel())
    rotateCellsButton.setOnMouseClicked(_ => rotateButtons())

    override def createBoard(level: Level): Unit = Platform.runLater(() => {
      val newBoardView: EditorBoardView = EditorBoardView(level, this, this)
      boardView.foreach(b => innerComponent.getChildren.remove(b.innerComponent))
      GridPane.setValignment(newBoardView.innerComponent, VPos.CENTER)
      GridPane.setHalignment(newBoardView.innerComponent, HPos.CENTER)
      GridPane.setMargin(newBoardView.innerComponent, new Insets(25, 0, 25, 0))
      innerComponent.add(newBoardView.innerComponent, 2, 3, 11, 1)
      boardView = Some(newBoardView)
    })

    override def updateLevel(level: Level, isPlayableAreaSet: Boolean): Unit = Platform.runLater(() => {
      boardView.get.drawBoard(level.board, isPlayableAreaSet)
    })

    override def manageCell(cellImage: ImageView, newPosition: Position): Unit = {
      val board = boardView.get.innerComponent
      if (board.getChildren.contains(cellImage)) {
        controller.updateCellPosition(
          Position(GridPane.getColumnIndex(cellImage), GridPane.getRowIndex(cellImage)),
          newPosition
        )
        board.getChildren.remove(cellImage)
        addRemoveCellHandler(cellImage, newPosition)
        board.add(cellImage, newPosition.x, newPosition.y)
      } else {
        val setupCell = getSetupCell(cellImage.getImage, newPosition)
        controller.setCell(setupCell)
        val cellView = CellView.apply(setupCell, board).innerComponent
        DragAndDrop.addDragFeature(cellView)
        addRemoveCellHandler(cellView, newPosition)
        board.add(cellView, newPosition.x, newPosition.y)
      }
    }

    override def createPlayableArea(topRight: Position, downLeft: Position): Unit = {
      val board = boardView.get.innerComponent
      for (x <- topRight.x to downLeft.x; y <- topRight.y to downLeft.y) {
        val tileView = TileView(CellImage.PlayAreaTile.image, board).innerComponent
        addRemovePlayableAreaHandler(tileView)
        DragAndDrop.addDropFeature(tileView, this)
        board.add(tileView, x, y)
      }
      controller.setPlayableArea(topRight, downLeft.x - topRight.x + 1, downLeft.y - topRight.y + 1)
    }

    private def addRemovePlayableAreaHandler(cell: ImageView): Unit = {
      val board = boardView.get.innerComponent
      cell.setOnMouseClicked(e => {
        if (e.getButton.equals(MouseButton.SECONDARY)) {
          board
            .getChildren
            .stream()
            .toScala(Seq)
            .filter(n => n.asInstanceOf[ImageView].getImage.equals(CellImage.PlayAreaTile.image))
            .foreach(n => board.getChildren.remove(n))
          controller.removePlayableArea()
          e.consume()
        }
      })
    }

    private def addRemoveCellHandler(cell: ImageView, newPosition: Position): Unit = {
      cell.setOnMouseClicked(e => {
        if (e.getButton.equals(MouseButton.SECONDARY)) {
          boardView.get.innerComponent.getChildren.remove(cell)
          controller.removeCell(newPosition)
          e.consume()
        }
      })
    }

    private def setButtonImages(): Map[ImageView, Image] = {
      Map(
        setGraphic(enemyCellView, CellImage.Enemy.image),
        setGraphic(wallCellView, CellImage.Wall.image),
        setGraphic(generateCellView, CellImage.GeneratorRight.image),
        setGraphic(moverCellView, CellImage.MoverRight.image),
        setGraphic(blockCellView, CellImage.Block.image),
        setGraphic(rotateCellView, CellImage.RotatorRight.image)
      )
    }

    private def getSetupCell(image: Image, newPosition: Position): SetupCell = image match {
      case CellImage.GeneratorRight.image  => SetupGeneratorCell(newPosition, Orientation.Right, true)
      case CellImage.GeneratorLeft.image   => SetupGeneratorCell(newPosition, Orientation.Left, true)
      case CellImage.GeneratorTop.image    => SetupGeneratorCell(newPosition, Orientation.Top, true)
      case CellImage.GeneratorDown.image   => SetupGeneratorCell(newPosition, Orientation.Down, true)
      case CellImage.RotatorRight.image    => SetupRotatorCell(newPosition, Rotation.Clockwise, true)
      case CellImage.RotatorLeft.image     => SetupRotatorCell(newPosition, Rotation.Counterclockwise, true)
      case CellImage.MoverRight.image      => SetupMoverCell(newPosition, Orientation.Right, true)
      case CellImage.MoverLeft.image       => SetupMoverCell(newPosition, Orientation.Left, true)
      case CellImage.MoverTop.image        => SetupMoverCell(newPosition, Orientation.Top, true)
      case CellImage.MoverDown.image       => SetupMoverCell(newPosition, Orientation.Down, true)
      case CellImage.Block.image           => SetupBlockCell(newPosition, Push.Both, true)
      case CellImage.BlockHorizontal.image => SetupBlockCell(newPosition, Push.Horizontal, true)
      case CellImage.BlockVertical.image   => SetupBlockCell(newPosition, Push.Vertical, true)
      case CellImage.Enemy.image           => SetupEnemyCell(newPosition, true)
      case CellImage.Wall.image            => SetupWallCell(newPosition, true)
    }

    private def setGraphic(buttonCellImageView: ImageView, image: Image): Tuple2[ImageView, Image] = {
      DragAndDrop.addDragFeature(buttonCellImageView)
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
    boardWidth: Int,
    boardHeight: Int
  ): LevelEditorView =
    LevelEditorViewImpl(parentLevelEditorController, scene, closeEditorButtonText, boardWidth, boardHeight, None)

  def apply(
    parentLevelEditorController: ParentLevelEditorController,
    scene: Scene,
    closeEditorButtonText: String,
    level: Level
  ): LevelEditorView =
    LevelEditorViewImpl(parentLevelEditorController, scene, closeEditorButtonText, level.width, level.height, Some(level))

}
