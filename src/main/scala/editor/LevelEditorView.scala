package it.unibo.pps.caw.editor

import it.unibo.pps.caw.{FilePicker, ViewComponent}
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.{Board, BoardView, CellImage, DragAndDrop, EditorBoardView, ModelUpdater, Position}
import it.unibo.pps.caw.editor.controller.{LevelEditorController, ParentLevelEditorController}
import it.unibo.pps.caw.editor.model.{Level, SetupEnemyCell}
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.geometry.{HPos, Insets, VPos}
import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene

import java.io.File

trait LevelEditorView extends ViewComponent[Pane] {
  def printLevel(level: Level): Unit
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
      with ModelUpdater {

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

    override def printLevel(level: Level): Unit = Platform.runLater(() => {
      val newBoardView: EditorBoardView = EditorBoardView(level, this)
      boardView.foreach(b => innerComponent.getChildren.remove(b.innerComponent))
      GridPane.setValignment(newBoardView.innerComponent, VPos.CENTER)
      GridPane.setHalignment(newBoardView.innerComponent, HPos.CENTER)
      GridPane.setMargin(newBoardView.innerComponent, new Insets(25, 0, 25, 0))
      innerComponent.add(newBoardView.innerComponent, 2, 3, 10, 1)
      boardView = Some(newBoardView)
    })

    override def manageCell(oldPosition: Option[Position], newPosition: Position): Unit = {
      oldPosition.fold(controller.setCell(SetupEnemyCell(newPosition, true)))(oldPosition =>
        controller.updateCellPosition(oldPosition, newPosition)
      )
      setButtonImages()
    }

    private def setButtonImages(): Map[ImageView, Image] = {
      Map(
        setGraphic(enemyCellView, CellImage.Enemy.image),
        setGraphic(wallCellView, CellImage.Wall.image),
        setGraphic(generateCellView, CellImage.GeneratorLeft.image),
        setGraphic(moverCellView, CellImage.MoverRight.image),
        setGraphic(blockCellView, CellImage.Block.image),
        setGraphic(rotateCellView, CellImage.RotatorRight.image)
      )
    }

    private def setGraphic(buttonCellImageView: ImageView, image: Image): Tuple2[ImageView, Image] = {
      DragAndDrop.addDragFeature(buttonCellImageView, true)
      buttonCellImageView.setImage(image)
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
