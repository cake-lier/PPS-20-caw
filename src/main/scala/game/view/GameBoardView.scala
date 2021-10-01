package it.unibo.pps.caw
package game.view

import common.{AbstractBoardViewImpl, BoardView, CellView, ModelUpdater}
import common.model.{Board, Level}
import common.model.cell.PlayableCell

/** The board displayed during the game. */
trait GameBoardView extends BoardView {

  /** Draws the board when the player is able to manipulate the cells.
    * @param board
    *   the [[Board]] containing the cells to be drawn in the board
    */
  def drawSetupBoard(board: Board[PlayableCell]): Unit

  /** Draws the board once the player hits play.
    * @param board
    *   the [[Board]] containing the cells to be drawn in the board
    */
  def drawGameBoard(board: Board[PlayableCell]): Unit
}

/** Companion object of the [[GameBoardView]] trait. */
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

    override def drawSetupBoard(board: Board[PlayableCell]): Unit = draw(board, droppablePlayableArea = true)

    private def draw(
      board: Board[PlayableCell],
      droppablePlayableArea: Boolean = false
    ): Unit = {
      clearComponents()
      drawPavement()
      drawPlayableArea(
        initialLevel.playableArea.position.x,
        initialLevel.playableArea.position.y,
        initialLevel.playableArea.dimensions.width,
        initialLevel.playableArea.dimensions.height,
        droppablePlayableArea
      )
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
  }
}
