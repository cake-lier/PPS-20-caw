package it.unibo.pps.caw
package common

import common.ViewComponent.AbstractViewComponent
import common.{TileView, CellImage}
import common.model.Position
import javafx.scene.image.ImageView
import javafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints}

/** Updates the model when view is modified.
  *
  * Its method is called after a [[CellView]] is moved by the user or when the user adds a new cell to the board.
  */
trait ModelUpdater {

  /** Updates the model with the cell linked to the [[cellImageView]]
    * @param cellImageView
    *   the original [[ImageView]] that was moved
    * @param newPosition
    *   the position where [[cellImageView]] was moved to
    */
  def manageCell(cellImageView: ImageView, newPosition: Position): Unit
}

trait BoardView extends ViewComponent[GridPane]

/** This view displays the board, whose concrete implementations are [[GameBoardView]] and [[EditorBoardView]].
  *
  * This abstract class provides methods to draw a generic board: it sets its size, adds the pavement, the playable area and the
  * board cells. It receives in its constructor a [[ModelUpdater]], necessary to apply the drop handler
  * [[ModelUpdater.manageCell]] after the view is modified by the user.
  *
  * @param screenWidth
  *   the width of the screen
  * @param screenHeight
  *   the height of the screen
  * @param levelWidth
  *   the width of the level
  * @param levelHeight
  *   the height of the level
  * @param model
  *   the [[ModelUpdater]]
  */
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

  if (levelWidth / levelHeight >= 2)
    boardHeight = (boardWidth / levelWidth) * levelHeight
  else
    boardWidth = (boardHeight / levelHeight) * levelWidth

  innerComponent.setPrefSize(boardWidth, boardHeight)
  (0 until levelWidth).foreach(_ => {
    val columnConstraints: ColumnConstraints = ColumnConstraints()
    columnConstraints.setPercentWidth(boardWidth / levelWidth)
    innerComponent.getColumnConstraints.add(columnConstraints)
  })
  (0 until levelHeight).foreach(_ => {
    val rowConstraints: RowConstraints = RowConstraints()
    rowConstraints.setPercentHeight(boardHeight / levelHeight)
    innerComponent.getRowConstraints.add(rowConstraints)
  })

  /** Places the board pavement. The board pavement can allow other [[ImageView]] to be dropped on top of it. By default, it's not
    * possible to drop [[ImageView]] on the board pavement.
    *
    * @param droppablePavement
    *   if it's possible to drop [[ImageView]] on the board pavement
    */
  protected def drawPavement(droppablePavement: Boolean = false): Unit = {
    for {
      x <- 0 until levelWidth
      y <- 0 until levelHeight
    } do
      drawImageView(
        TileView.apply(CellImage.DefaultTile.image, innerComponent, droppablePavement, model.manageCell).innerComponent,
        x,
        y
      )
  }

  /** Places the playable area in the board. The playable area can allow other [[ImageView]] to be dropped on top of it. By
    * default, it's possible to drop [[ImageView]] on the playable area.
    *
    * @param positionX
    *   the top-left x coordinate of the playable area
    * @param positionY
    *   the top-left y coordinate of the playable area
    * @param playableAreaWidth
    *   the width of the playable area
    * @param playableAreaHeight
    *   the height of the playable area
    * @param droppablePlayableArea
    *   if it's possible to drop [[ImageView]] on the playable area
    */
  protected def drawPlayableArea(
    positionX: Int,
    positionY: Int,
    playableAreaWidth: Int,
    playableAreaHeight: Int,
    droppablePlayableArea: Boolean = true
  ): Unit = {
    for {
      x <- 0 until playableAreaWidth
      y <- 0 until playableAreaHeight
    } do
      drawImageView(
        TileView.apply(CellImage.PlayAreaTile.image, innerComponent, droppablePlayableArea, model.manageCell).innerComponent,
        x + positionX,
        y + positionY
      )
  }

  /** Adds a generic ImageView to the board.
    * @param node
    *   the [[ImageView]] to be added to the board
    * @param x
    *   the x coordinate of the [[node]]
    * @param y
    *   the y coordinate of the [[node]]
    */
  protected def drawImageView(node: ImageView, x: Int, y: Int): Unit = innerComponent.add(node, x, y)

  /** Clears the board. */
  protected def clearComponents(): Unit = innerComponent.getChildren.clear()
}
