package it.unibo.pps.caw
package common

import it.unibo.pps.caw.game.model.{CellConverter, Cell as GameCell, Level as GameLevel, SetupCell as GameSetupCell}
import it.unibo.pps.caw.editor.model.{Cell as EditorCell, Level as EditorLevel, SetupCell as EditorSetupCell}
import it.unibo.pps.caw.{ViewComponent, game}
import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.{Board, DragAndDrop, ModelUpdater, PlayableArea, TileView}
import it.unibo.pps.caw.game.{model, view}
import it.unibo.pps.caw.common.CellImage
import it.unibo.pps.caw.editor.{LevelEditorView, PlayableAreaUpdater}
import it.unibo.pps.caw.game.view.CellView as GameCellView
import it.unibo.pps.caw.editor.view.CellView as EditorCellView
import javafx.application.Platform
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseButton
import javafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints}

import scala.jdk.StreamConverters
import scala.reflect.ClassTag

trait BoardView extends ViewComponent[GridPane]

abstract class AbstractBoardImpl(levelWidth: Int, levelHeight: Int, model: ModelUpdater)
  extends AbstractViewComponent[GridPane](fxmlFileName = "board.fxml")
  with BoardView {

  override val innerComponent: GridPane = loader.load[GridPane]
  private var boardWidth: Double = innerComponent.getPrefWidth
  private var boardHeight: Double = innerComponent.getPrefHeight

  if (levelWidth / levelHeight >= 2) {
    boardHeight = (boardWidth / levelWidth) * levelHeight
  } else {
    boardWidth = (boardHeight / levelHeight) * levelWidth
  }

  innerComponent.setPrefSize(boardWidth, boardHeight)
  (0 until levelWidth).foreach { _ =>
    val columnConstraints: ColumnConstraints = ColumnConstraints()
    columnConstraints.setPercentWidth(boardWidth / levelWidth)
    innerComponent.getColumnConstraints.add(columnConstraints)
  }
  (0 until levelHeight).foreach { _ =>
    val rowConstraints: RowConstraints = RowConstraints()
    rowConstraints.setPercentHeight(boardHeight / levelHeight)
    innerComponent.getRowConstraints.add(rowConstraints)
  }

  /* Places the default tiles in the grid. */
  protected def drawPavement(droppablePavement: Boolean): Unit = {
    for {
      x <- 0 until levelWidth
      y <- 0 until levelHeight
    } do {
      drawImageView(CellImage.DefaultTile.image, x, y, model, droppable = droppablePavement)
    }
  }

  protected def drawPlayableArea(
    positionX: Int,
    positionY: Int,
    playableAreaWidth: Int,
    playableAreaHeight: Int,
    droppablePlayableArea: Boolean
  ): Unit = {
    for {
      x <- 0 until playableAreaWidth
      y <- 0 until playableAreaHeight
    } do drawImageView(CellImage.PlayAreaTile.image, x + positionX, y + positionY, model, droppable = droppablePlayableArea)
  }

  protected def drawImageView(
    image: Image,
    x: Int,
    y: Int,
    model: ModelUpdater,
    droppable: Boolean = false,
    draggable: Boolean = false
  ): Unit = {
    val node = TileView(image, innerComponent).innerComponent
    if (droppable) { DragAndDrop.addDropFeature(node, model) }
    if (draggable) { DragAndDrop.addDragFeature(node) }
    innerComponent.add(node, x, y)
  }

  protected def clearComponents(): Unit = {
    innerComponent.getChildren.clear()
  }
}

sealed trait GameBoardView extends BoardView {
  def drawSetupBoard(board: Board[GameSetupCell]): Unit
  def drawGameBoard(board: Board[GameCell]): Unit
}

object GameBoardView {
  def apply(initialLevel: GameLevel, model: ModelUpdater): GameBoardView = GameBoardViewImpl(initialLevel, model)
  private case class GameBoardViewImpl(initialLevel: GameLevel, modelUpdater: ModelUpdater)
    extends AbstractBoardImpl(initialLevel.width, initialLevel.height, modelUpdater)
    with GameBoardView {

    drawSetupBoard(initialLevel.setupBoard)

    override def drawGameBoard(board: Board[GameCell]): Unit = draw(board)

    override def drawSetupBoard(board: Board[GameSetupCell]): Unit = draw(board, true, true)

    private def draw(
      board: Board[_ <: GameCell],
      draggableCell: Boolean = false,
      droppablePlayableArea: Boolean = false
    ): Unit = {
      clearComponents()
      drawPavement(false)
      val playableAreaPosition = initialLevel.playableArea.position
      drawPlayableArea(
        playableAreaPosition.x,
        playableAreaPosition.y,
        initialLevel.playableArea.width,
        initialLevel.playableArea.height,
        droppablePlayableArea
      )
      board
        .cells
        .foreach(c =>
          drawImageView(
            GameCellView(
              if (c.isInstanceOf[GameSetupCell]) CellConverter.fromSetup(c.asInstanceOf[GameSetupCell]) else c,
              innerComponent
            ).innerComponent.getImage,
            c.position.x,
            c.position.y,
            modelUpdater,
            draggable = draggableCell && c.isInstanceOf[GameSetupCell] && c.asInstanceOf[GameSetupCell].playable
          )
        )
    }
  }
}

sealed trait EditorBoardView extends BoardView {
  def drawBoard(board: Board[EditorSetupCell], playableAreaSet: Boolean): Unit
}

object EditorBoardView {
  def apply(level: EditorLevel, model: ModelUpdater, updater: PlayableAreaUpdater): EditorBoardView =
    EditorBoardViewImpl(level, model, updater)
  private case class EditorBoardViewImpl(
    initialLevel: EditorLevel,
    modelUpdater: ModelUpdater,
    updater: PlayableAreaUpdater
  ) extends AbstractBoardImpl(initialLevel.width, initialLevel.height, modelUpdater)
    with EditorBoardView {

    var topLeftPosition = Position(0, 0)
    var downRightPosition = Position(0, 0)
    var isPlayableAreaSet = false
    drawBoard(initialLevel.board, false)

    override def drawBoard(board: Board[EditorSetupCell], playableAreaSet: Boolean): Unit = {
      clearComponents()
      drawPavement(true)
      isPlayableAreaSet = playableAreaSet
      innerComponent.getChildren.forEach(n => createPlayableArea(n.asInstanceOf[ImageView]))
      initialLevel
        .playableArea
        .foreach(p => {
          val playableArea = initialLevel.playableArea
          val playableAreaPosition = p.position
          drawPlayableArea(playableAreaPosition.x, playableAreaPosition.y, p.width, p.height, true)
        })
      board
        .cells
        .foreach(c =>
          drawImageView(
            EditorCellView(c, innerComponent).innerComponent.getImage,
            c.position.x,
            c.position.y,
            modelUpdater,
            draggable = c.playable
          )
        )
    }

    private def createPlayableArea(imageView: ImageView) = {
      imageView.setOnDragDetected(e => {
        if (!isPlayableAreaSet) {
          topLeftPosition = Position(GridPane.getColumnIndex(imageView), GridPane.getRowIndex(imageView))
          imageView.startFullDrag()
          e.consume()
        }
      })

      imageView.setOnMouseDragReleased(e => {
        downRightPosition = Position(GridPane.getColumnIndex(imageView), GridPane.getRowIndex(imageView))
        updater.createPlayableArea(topLeftPosition, downRightPosition)
        isPlayableAreaSet = true
        e.consume()
      })

    }
  }
}
