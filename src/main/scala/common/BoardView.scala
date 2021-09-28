package it.unibo.pps.caw.common

import it.unibo.pps.caw.editor.model.{Board as EditorBoard, Cell as EditorCell, Level as EditorLevel, SetupCell as EditorSetupCell}
import it.unibo.pps.caw.common.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.common.{DragAndDrop, ModelUpdater, TileView}
import it.unibo.pps.caw.common.CellImage
import it.unibo.pps.caw.common.model.{Board, Level, Position}
import it.unibo.pps.caw.common.model.cell.PlayableCell
import it.unibo.pps.caw.editor.{LevelEditorView, PlayableAreaUpdater}
import it.unibo.pps.caw.game.view.CellView as GameCellView
import it.unibo.pps.caw.editor.view.CellView as EditorCellView
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.effect.Glow
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.MouseButton
import javafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints}

import scala.jdk.StreamConverters
import scala.reflect.ClassTag

trait ModelUpdater {
  def manageCell(cell: ImageView, newPosition: Position): Unit
}

trait BoardView extends ViewComponent[GridPane]

abstract class AbstractBoardViewImpl(
  screenWidth: Double,
  screenHeight: Double,
  levelWidth: Int,
  levelHeight: Int,
  model: ModelUpdater
) extends AbstractViewComponent[GridPane](fxmlFileName = "board.fxml")
  with BoardView {

  override val innerComponent: GridPane = loader.load[GridPane]
  private var boardWidth: Double = screenWidth * 0.7
  private var boardHeight: Double = screenHeight * 0.7

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
      drawImageView(
        TileView.apply(CellImage.DefaultTile.image, innerComponent).innerComponent,
        x,
        y,
        model,
        droppable = droppablePavement
      )
    }
  }

  protected def applyHandler(f: Node => Boolean, h: Node => Unit): Unit = {
    innerComponent
      .getChildren
      .stream()
      .filter(f(_))
      .forEach(h(_))
  }

  protected def applyHandler(h: Node => Unit): Unit = {
    applyHandler(_ => true, h)
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
    } do
      drawImageView(
        TileView.apply(CellImage.PlayAreaTile.image, innerComponent).innerComponent,
        x + positionX,
        y + positionY,
        model,
        droppable = droppablePlayableArea
      )
  }

  protected def drawImageView(
    node: ImageView,
    x: Int,
    y: Int,
    model: ModelUpdater,
    droppable: Boolean = false,
    draggable: Boolean = false
  ): Unit = {
    if (droppable) { DragAndDrop.addDropFeature(node, model) }
    if (draggable) { DragAndDrop.addDragFeature(node) }
    innerComponent.add(node, x, y)
  }

  protected def clearComponents(): Unit = {
    innerComponent.getChildren.clear()
  }
}

sealed trait GameBoardView extends BoardView {
  def drawSetupBoard(board: Board[PlayableCell]): Unit
  def drawGameBoard(board: Board[PlayableCell]): Unit
}

object GameBoardView {
  def apply(
             screenWidth: Double,
             screenHeight: Double,
             initialLevel: Level[PlayableCell],
             model: ModelUpdater
  ): GameBoardView =
    GameBoardViewImpl(screenWidth, screenHeight, initialLevel, model)
  private case class GameBoardViewImpl(
                                        screenWidth: Double,
                                        screenHeight: Double,
                                        initialLevel: Level[PlayableCell],
                                        modelUpdater: ModelUpdater
  ) extends AbstractBoardViewImpl(
      screenWidth,
      screenHeight,
      initialLevel.dimensions.width,
      initialLevel.dimensions.height,
      modelUpdater
    )
    with GameBoardView {

    drawSetupBoard(initialLevel.board)

    override def drawGameBoard(board: Board[PlayableCell]): Unit = draw(board)

    override def drawSetupBoard(board: Board[PlayableCell]): Unit = draw(board, true, true)

    private def draw(
                      board: Board[PlayableCell],
                      draggableCell: Boolean = false,
                      droppablePlayableArea: Boolean = false
    ): Unit = {
      clearComponents()
      drawPavement(false)
      drawPlayableArea(
        initialLevel.playableArea.position.x,
        initialLevel.playableArea.position.y,
        initialLevel.playableArea.dimensions.width,
        initialLevel.playableArea.dimensions.height,
        droppablePlayableArea
      )
      board
        .cells
        .foreach(c => {
          val node = GameCellView(c, innerComponent)
          drawImageView(
            node.innerComponent,
            c.position.x,
            c.position.y,
            modelUpdater,
            draggable = draggableCell && c.playable
          )
        })
    }
  }
}

sealed trait EditorBoardView extends BoardView {
  def drawBoard(board: EditorBoard[EditorSetupCell]): Unit
}

object EditorBoardView {
  def apply(
    screenWidth: Double,
    screenHeight: Double,
    level: EditorLevel,
    model: ModelUpdater,
    updater: PlayableAreaUpdater
  ): EditorBoardView =
    EditorBoardViewImpl(screenWidth, screenHeight, level, model, updater)
  private case class EditorBoardViewImpl(
    screenWidth: Double,
    screenHeight: Double,
    initialLevel: EditorLevel,
    modelUpdater: ModelUpdater,
    updater: PlayableAreaUpdater
  ) extends AbstractBoardViewImpl(screenWidth, screenHeight, initialLevel.width, initialLevel.height, modelUpdater)
    with EditorBoardView {

    var startPosition = Position(0, 0)
    var endPosition = Position(0, 0)
    drawBoard(initialLevel.board)

    override def drawBoard(board: EditorBoard[EditorSetupCell]): Unit = {
      clearComponents()
      drawPavement(true)
      if (initialLevel.playableArea.isEmpty) {
        applyHandler(n => createPlayableArea(n.asInstanceOf[ImageView]))
      }
      initialLevel
        .playableArea
        .foreach(p => drawPlayableArea(p.position.x, p.position.y, p.dimensions.width, p.dimensions.height, true))
      applyHandler(n => n.asInstanceOf[ImageView].getImage.equals(CellImage.PlayAreaTile.image), n => removePlayableArea(n))

      board
        .cells
        .foreach(c => {
          val node: ImageView = EditorCellView(c, innerComponent).innerComponent
          node.setOnMouseClicked(e => {
            if (e.getButton.equals(MouseButton.SECONDARY)) {
              updater.removeCell(c.position.x, c.position.y)
              e.consume()
            }
          })
          drawImageView(
            node,
            c.position.x,
            c.position.y,
            modelUpdater,
            draggable = c.playable
          )
        })
    }

    private def removePlayableArea(node: Node) = {
      node.setOnMouseClicked(e => {
        if (e.getButton.equals(MouseButton.SECONDARY)) {
          updater.removePlayableArea()
          e.consume()
        }
      })
    }

    private def createPlayableArea(imageView: ImageView) = {
      val glow = new Glow()
      imageView.setOnDragDetected(e => {
        startPosition = Position(GridPane.getColumnIndex(imageView), GridPane.getRowIndex(imageView))
        imageView.startFullDrag()
        e.consume()
      })

      imageView.setOnMouseDragReleased(e => {
        endPosition = Position(GridPane.getColumnIndex(imageView), GridPane.getRowIndex(imageView))
        updater.createPlayableArea(startPosition, endPosition)
        e.consume()
      })

      imageView.setOnMouseDragEntered(e => {
        glow.setLevel(0.3)
        applyHandler(
          n =>
            n.asInstanceOf[ImageView].getImage.equals(CellImage.DefaultTile.image) &&
              GridPane.getRowIndex(n) >= startPosition.y && GridPane.getRowIndex(n) <= GridPane.getRowIndex(imageView) &&
              GridPane.getColumnIndex(n) >= startPosition.x && GridPane.getColumnIndex(n) <= GridPane.getColumnIndex(imageView),
          n => n.setEffect(glow)
        )
      })

      imageView.setOnMouseDragExited(e => {
        glow.setLevel(0)
        applyHandler(
          n =>
            n.asInstanceOf[ImageView].getImage.equals(CellImage.DefaultTile.image) &&
              GridPane.getRowIndex(n) < startPosition.y && GridPane.getRowIndex(n) >= GridPane.getRowIndex(imageView) &&
              GridPane.getColumnIndex(n) < startPosition.x && GridPane.getColumnIndex(n) >= GridPane.getColumnIndex(imageView),
          n => n.setEffect(glow)
        )
      })
    }
  }
}
