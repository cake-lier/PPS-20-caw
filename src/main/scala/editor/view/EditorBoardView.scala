package it.unibo.pps.caw.editor.view

import it.unibo.pps.caw.common.model.cell.PlayableCell
import it.unibo.pps.caw.common.model.{Board, Position}
import it.unibo.pps.caw.common.*
import it.unibo.pps.caw.editor.model.LevelBuilderState

import it.unibo.pps.caw.common.view.{AbstractBoardView, BoardView, CellImage, CellView, DraggableImageView, ModelUpdater}
import javafx.scene.Node
import javafx.scene.effect.Glow
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane

/** The board displayed in the editor.
  *
  * In the editor, the player can modify the level board as they please: they can select and deselect the
  * [[it.unibo.pps.caw.common.model.PlayableArea]] and add, move and remove cells both inside and outside the
  * [[it.unibo.pps.caw.common.model.PlayableArea]]. It must be constructed through its companion object.
  */
sealed trait EditorBoardView extends BoardView {

  /** Draws the [[LevelBuilderState]] received in input.
    *
    * @param levelState
    *   the [[LevelBuilderState]] to be drawn
    */
  def drawLevelState(levelState: LevelBuilderState): Unit
}

/** Companion object of the [[EditorBoardView]] trait, containing its factory method. */
object EditorBoardView {

  /** Returns a new instance of [[EditorBoardView]]. It receives the screen width and height, necessary to calculate the size of
    * the board, the [[LevelBuilderState]] containing all the necessary information to draw the level, the [[ModelUpdater]] and
    * the [[EditorUpdater]], necessary to update the model after the player modifies the view.
    *
    * @param screenWidth
    *   the width of the screen necessary to calculate the board width
    * @param screenHeight
    *   the height of the screen necessary to calculate the board width
    * @param levelState
    *   the [[LevelBuilderState]] to be drawn
    * @param model
    *   the [[ModelUpdater]] necessary to update the model after view changes
    * @param updater
    *   the [[EditorUpdater]] necessary to update the model after view changes
    * @return
    *   a new instance of [[EditorBoardView]]
    */
  def apply(
    screenWidth: Double,
    screenHeight: Double,
    levelState: LevelBuilderState,
    model: ModelUpdater,
    updater: EditorUpdater
  ): EditorBoardView =
    EditorBoardViewImpl(screenWidth, screenHeight, levelState, model, updater)

  /* Extension of AbstractBoardView */
  private case class EditorBoardViewImpl(
    screenWidth: Double,
    screenHeight: Double,
    initialLevelState: LevelBuilderState,
    modelUpdater: ModelUpdater,
    updater: EditorUpdater
  ) extends AbstractBoardView(
      screenWidth,
      screenHeight,
      initialLevelState.dimensions.width,
      initialLevelState.dimensions.height,
      modelUpdater
    )
    with EditorBoardView {
    private var startPosition = Position(0, 0)
    private var endPosition = Position(0, 0)

    drawLevelState(initialLevel)

    override def drawLevelState(levelState: LevelBuilderState): Unit = {
      clearComponents()
      drawPavement(droppablePavement = true)
      levelState.playableArea match {
        case Some(p) =>
          drawPlayableArea(p.position.x, p.position.y, p.dimensions.width, p.dimensions.height, droppablePlayableArea = true)
        case _ => applyHandler(n => enablePlayableAreaSelection(n.asInstanceOf[ImageView]))
      }
      levelState.board.foreach(c => drawImageView(CellView(c, innerComponent).innerComponent, c.position.x, c.position.y))
    }

    /** Adds an [[ImageView]] to the editor board, that is a view that can be removed if it was added by the user.
      * @param node
      *   the [[ImageView]] to be drawn to the board
      * @param x
      *   the x coordinate where the [[ImageView]] will be placed in the board
      * @param y
      *   the y coordinate where the [[ImageView]] will be placed in the board
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

      imageView.setOnMouseDragEntered(_ => {
        glow.setLevel(0.3)
        applyHandler(
          n =>
            n.asInstanceOf[ImageView].getImage.equals(CellImage.DefaultTile.image) &&
              GridPane.getRowIndex(n) >= startPosition.y && GridPane.getRowIndex(n) <= GridPane.getRowIndex(imageView) &&
              GridPane.getColumnIndex(n) >= startPosition.x && GridPane.getColumnIndex(n) <= GridPane.getColumnIndex(imageView),
          n => n.setEffect(glow)
        )
      })

      imageView.setOnMouseDragExited(_ => {
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

    /* Filters the ImageView of this GridPane with the given predicate and applies a handler. */
    private def applyHandler(predicate: Node => Boolean, handler: Node => Unit): Unit =
      innerComponent
        .getChildren
        .stream()
        .filter(predicate(_))
        .forEach(handler(_))

    /* Applies a handler. */
    private def applyHandler(handler: Node => Unit): Unit = applyHandler(_ => true, handler)
  }
}
