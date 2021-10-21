package it.unibo.pps.caw.editor.view

import it.unibo.pps.caw.common.model.cell.PlayableCell
import it.unibo.pps.caw.common.model.{Board, Position}
import it.unibo.pps.caw.common.*
import it.unibo.pps.caw.editor.model.EditorModelState

import it.unibo.pps.caw.common.view.{AbstractBoardView, BoardView, CellImage, CellView, DraggableImageView, ModelUpdater}
import javafx.scene.Node
import javafx.scene.effect.Glow
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.GridPane

/* The board displayed in the editor.
 *
 * In the editor, the player can modify the level board as they please: they can select and deselect the
 * PlayableArea and add, move and remove cells both inside and outside the PlayableArea. It must be constructed through its
 * companion object.
 */
private trait EditorBoardView extends BoardView {

  /* Draws the EditorModelState received in input. */
  def drawState(state: EditorModelState): Unit
}

/* Companion object of the EditorBoardView trait, containing its factory method. */
private object EditorBoardView {

  /* Returns a new instance of EditorBoardView. It receives the screen width and height, necessary to calculate the size of
   * the board, the EditorModelState containing all the necessary information to draw the level, the ModelUpdater and
   * the EditorUpdater, necessary to update the model after the player modifies the view.
   */
  def apply(
    screenWidth: Double,
    screenHeight: Double,
    levelState: EditorModelState,
    model: ModelUpdater,
    updater: EditorUpdater
  ): EditorBoardView =
    EditorBoardViewImpl(screenWidth, screenHeight, levelState, model, updater)

  /* An extension of AbstractBoardView for the EditorView. */
  private class EditorBoardViewImpl(
    screenWidth: Double,
    screenHeight: Double,
    initialState: EditorModelState,
    modelUpdater: ModelUpdater,
    updater: EditorUpdater
  ) extends AbstractBoardView(
      screenWidth,
      screenHeight,
      initialState.dimensions.width,
      initialState.dimensions.height,
      modelUpdater
    )
    with EditorBoardView {
    private var startPosition: Position = Position(0, 0)
    private var endPosition: Position = Position(0, 0)

    drawState(initialState)

    override def drawState(state: EditorModelState): Unit = {
      clearComponents()
      drawPavement(droppablePavement = true)
      state.playableArea match {
        case Some(p) =>
          drawPlayableArea(p.position.x, p.position.y, p.dimensions.width, p.dimensions.height, droppablePlayableArea = true)
        case _ => applyHandler(n => enablePlayableAreaSelection(n.asInstanceOf[ImageView]))
      }
      state
        .board
        .foreach(c => drawImageView(CellView(c, innerComponent).innerComponent, c.position.x, c.position.y))
    }

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
     * the selected tiles.
     */
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
