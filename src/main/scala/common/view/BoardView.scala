package it.unibo.pps.caw.common.view

import it.unibo.pps.caw.common.model.Position
import it.unibo.pps.caw.common.*
import it.unibo.pps.caw.common.view.ViewComponent.AbstractViewComponent
import javafx.scene.image.ImageView
import javafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints}

/** Updates the model when view is modified.
  *
  * This trait is given to the [[BoardView]] constructor, as it is needed to propagate the view changes to the model. Its only
  * method is called after the player moves an [[ImageView]] to a different position during the setup stage of the game or when
  * the player adds a new [[ImageView]] to the board during the editing of a level.
  */
trait ModelUpdater {

  /** Updates the model with the cell linked to the given [[ImageView]].
    * @param cellImageView
    *   the [[ImageView]] that was moved by the player
    * @param newPosition
    *   the position where the [[ImageView]] was moved to
    */
  def manageCell(cellImageView: ImageView, newPosition: Position): Unit
}

/** The view that displays the board of a [[model.Level]], that is a grid with a certain width and height that displays the
  * structure a [[model.Level]].
  */
trait BoardView extends ViewComponent[GridPane]

/** The abstract view that displays a [[Level]].
  *
  * This abstract implementation of the trait [[BoardView]] provides all the necessary methods to draw a generic board. A board is
  * a grid with a size and a certain number of rows and columns; it has a pavement and potentially a playable area where the cells
  * of a [[Level]] are placed and drawn. It receives in its constructor a [[ModelUpdater]], necessary to update the model after
  * the view is modified by the player.
  *
  * @param screenWidth
  *   the width of the screen necessary to calculate the board width
  * @param screenHeight
  *   the height of the screen necessary to calculate the board height
  * @param levelWidth
  *   the width of the level, that is the number of columns
  * @param levelHeight
  *   the height of the level, that is the number of rows
  * @param modelUpdater
  *   the [[ModelUpdater]] necessary to update the model after view changes
  */
abstract class AbstractBoardView(
  screenWidth: Double,
  screenHeight: Double,
  levelWidth: Int,
  levelHeight: Int,
  modelUpdater: ModelUpdater
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

  /** Places the board pavement. The board pavement can allow other [[ImageView]] to be dropped on top of it. By default, it is
    * not possible to drop an [[ImageView]] on the board pavement.
    *
    * @param droppablePavement
    *   if it is possible to drop an [[ImageView]] on the board pavement
    */
  protected def drawPavement(droppablePavement: Boolean = false): Unit = {
    for {
      x <- 0 until levelWidth
      y <- 0 until levelHeight
    } do
      drawImageView(
        TileView(CellImage.DefaultTile.image, innerComponent, droppablePavement, modelUpdater.manageCell).innerComponent,
        x,
        y
      )
  }

  /** Places the playable area in the board. The playable area can allow other [[ImageView]] to be dropped on top of it. By
    * default, it is not possible to drop an [[ImageView]] on the playable area.
    *
    * @param positionX
    *   the upper left x coordinate of the playable area
    * @param positionY
    *   the upper left y coordinate of the playable area
    * @param playableAreaWidth
    *   the width of the playable area
    * @param playableAreaHeight
    *   the height of the playable area
    * @param droppablePlayableArea
    *   if it is possible to drop [[ImageView]] on the playable area
    */
  protected def drawPlayableArea(
    positionX: Int,
    positionY: Int,
    playableAreaWidth: Int,
    playableAreaHeight: Int,
    droppablePlayableArea: Boolean = false
  ): Unit = {
    for {
      x <- 0 until playableAreaWidth
      y <- 0 until playableAreaHeight
    } do
      drawImageView(
        TileView(CellImage.PlayAreaTile.image, innerComponent, droppablePlayableArea, modelUpdater.manageCell).innerComponent,
        x + positionX,
        y + positionY
      )
  }

  /** Draws a generic [[ImageView]].
    * @param node
    *   the [[ImageView]] to be drawn in the board
    * @param x
    *   the x coordinate where the [[ImageView]] will be placed in the board
    * @param y
    *   the y coordinate where the [[ImageView]] will be placed in the board
    */
  protected def drawImageView(node: ImageView, x: Int, y: Int): Unit = innerComponent.add(node, x, y)

  /** Clears the board. */
  protected def clearComponents(): Unit = innerComponent.getChildren.clear()
}
