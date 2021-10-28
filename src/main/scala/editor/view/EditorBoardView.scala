package it.unibo.pps.caw.editor.view

import it.unibo.pps.caw.common.model.cell.PlayableCell
import it.unibo.pps.caw.common.model.{Board, Position}
import it.unibo.pps.caw.common.*
import it.unibo.pps.caw.editor.model.EditorModelState

import it.unibo.pps.caw.common.view.{AbstractBoardView, BoardView, CellView, CellImage, ModelUpdater}
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane

/* Updates the editor model when the view is modified.
 *
 * Its methods are called when a PlayableArea is placed to the board or removed from it by the player and when a Cell is
 * removed by the player.
 */
private trait EditorModelUpdater extends ModelUpdater {

  /* Creates a new PlayableArea, given its upper left Position and its lower right Position. */
  def addPlayableArea(topLeftCorner: Position, downRightCorner: Position): Unit

  /* Removes a Cell given its current Position. */
  def removeCell(position: Position): Unit

  /* Removes the previously added PlayableArea. */
  def removePlayableArea(): Unit
}

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
   * the EditorModelUpdater, necessary to update the model after the player modifies the view.
   */
  def apply(
    screenWidth: Double,
    screenHeight: Double,
    levelState: EditorModelState,
    modelUpdater: EditorModelUpdater
  ): EditorBoardView = EditorBoardViewImpl(screenWidth, screenHeight, levelState, modelUpdater)

  /* An extension of AbstractBoardView for the EditorView. */
  private class EditorBoardViewImpl(
    screenWidth: Double,
    screenHeight: Double,
    initialState: EditorModelState,
    modelUpdater: EditorModelUpdater
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
      drawFloor(isDroppable = true)
      state.playableArea match {
        case Some(p) =>
          drawPlayableArea(p.position, p.dimensions, isDroppable = true)
        case _ => applyHandler(n => enablePlayableAreaSelection(n.asInstanceOf[ImageView]))
      }
      state
        .board
        .foreach(c => drawImageView(CellView(c, innerComponent).innerComponent, c.position.x, c.position.y))
    }

    override protected def drawImageView(imageView: ImageView, x: Int, y: Int): Unit = {
      import it.unibo.pps.caw.common.view.{DraggableImageView, DroppableImageView}
      import javafx.scene.input.MouseButton
      imageView match {
        case tile: DroppableImageView if (tile.getImage == CellImage.PlayAreaTile.image) =>
          tile.setOnMouseClicked(e => if (e.getButton.equals(MouseButton.SECONDARY)) modelUpdater.removePlayableArea())
        case cell: DraggableImageView =>
          cell.setOnMouseClicked(e => if (e.getButton.equals(MouseButton.SECONDARY)) modelUpdater.removeCell(x, y))
        case _ =>
      }
      super.drawImageView(imageView, x, y)
    }

    /* Given an image view, adds the necessary JavaFX event handlers to select a playable area with the mouse and to highlight
     * the selected tiles.
     */
    private def enablePlayableAreaSelection(imageView: ImageView): Unit = {
      import javafx.scene.effect.Glow
      val glow = new Glow()
      imageView.setOnDragDetected(e => {
        startPosition = Position(GridPane.getColumnIndex(imageView), GridPane.getRowIndex(imageView))
        imageView.startFullDrag()
        e.consume()
      })

      imageView.setOnMouseDragReleased(e => {
        endPosition = Position(GridPane.getColumnIndex(imageView), GridPane.getRowIndex(imageView))
        modelUpdater.addPlayableArea(startPosition, endPosition)
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
