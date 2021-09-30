package it.unibo.pps.caw.editor.view

import it.unibo.pps.caw.common.{AbstractBoardViewImpl, BoardView, CellImage, CellView, DraggableImageView, ModelUpdater}
import it.unibo.pps.caw.common.model.{Board, Position}
import it.unibo.pps.caw.common.model.cell.PlayableCell
import it.unibo.pps.caw.editor.model.LevelBuilder
import javafx.scene.Node
import javafx.scene.effect.Glow
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane

/** The board displayed in the editor. */
sealed trait EditorBoardView extends BoardView {

  /** Draws the level editor board.
    * @param board
    *   the [[Board]] containing the cells to be drawn in the [[EditorBoardView]]
    */
  def drawBoard(board: Board[PlayableCell]): Unit
}

/** Companion object of the [[EditorBoardView]] trait. */
object EditorBoardView {

  def apply(
    screenWidth: Double,
    screenHeight: Double,
    level: LevelBuilder,
    model: ModelUpdater,
    updater: EditorUpdater
  ): EditorBoardView =
    EditorBoardViewImpl(screenWidth, screenHeight, level, model, updater)

  private case class EditorBoardViewImpl(
    screenWidth: Double,
    screenHeight: Double,
    initialLevel: LevelBuilder,
    modelUpdater: ModelUpdater,
    updater: EditorUpdater
  ) extends AbstractBoardViewImpl(screenWidth, screenHeight, initialLevel.width, initialLevel.height, modelUpdater)
    with EditorBoardView {
    private var startPosition = Position(0, 0)
    private var endPosition = Position(0, 0)

    drawBoard(initialLevel.board)

    override def drawBoard(board: Board[PlayableCell]): Unit = {
      clearComponents()
      drawPavement(droppablePavement = true)
      initialLevel.playableArea match {
        case Some(p) =>
          drawPlayableArea(p.position.x, p.position.y, p.dimensions.width, p.dimensions.height, droppablePlayableArea = true)
        case None => applyHandler(n => enablePlayableAreaSelection(n.asInstanceOf[ImageView]))
      }
      board
        .cells
        .foreach(c =>
          drawImageView(
            CellView(c, innerComponent).innerComponent,
            c.position.x,
            c.position.y
          )
        )
    }

    /** Adds an [[ImageView]] to the editor board, that is a view that can be removed if it was added by the user.
      * @param node
      *   the [[ImageView]] to be added to the board
      * @param x
      *   the x coordinate of the [[node]]
      * @param y
      *   the y coordinate of the [[node]]
      */
    override protected def drawImageView(node: ImageView, x: Int, y: Int): Unit = {
      node.getImage match {
        case CellImage.PlayAreaTile.image =>
          node.setOnMouseClicked(e =>
            if (e.getButton.equals(MouseButton.SECONDARY)) {
              updater.removePlayableArea()
              e.consume()
            }
          )
        case _ =>
          if (node.isInstanceOf[DraggableImageView]) {
            node.setOnMouseClicked(e => {
              if (e.getButton.equals(MouseButton.SECONDARY)) {
                updater.removeCell(x, y)
                e.consume()
              }
            })
          }
      }
      super.drawImageView(node, x, y)
    }

    /* Given an image view, adds the necessary JavaFX event handlers to select a playable area with the mouse and to highlight
     * the selected tiles. */
    private def enablePlayableAreaSelection(imageView: ImageView): Unit = {
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

    /* Filters the ImageView of this GridPane with the given predicate and applys a handler. */
    private def applyHandler(predicate: Node => Boolean, handler: Node => Unit): Unit = {
      innerComponent
        .getChildren
        .stream()
        .filter(predicate(_))
        .forEach(handler(_))
    }

    /* Applys a handler. */
    private def applyHandler(handler: Node => Unit): Unit = {
      applyHandler(_ => true, handler)
    }
  }
}