package it.unibo.pps.caw
package game.view

import common.model.{Board, Level}
import common.model.cell.*
import common.view.*

/** The board displayed during the game.
  *
  * During the setup phase, the player can only move the cells drawn in the [[it.unibo.pps.caw.common.model.PlayableArea]] and
  * arrange them as they please inside the playable area. It is not possible to drop cells outside of the playable area nor move
  * cells placed outside of the playable area. After the player clicks play, the game phase starts and no cells is movable.
  */
trait GameBoardView extends BoardView {

  /** Draws the [[it.unibo.pps.caw.common.model.cell.PlayableCell]] received in input during the setup phase.
    *
    * @param board
    *   the [[it.unibo.pps.caw.common.model.Board]] containing the [[it.unibo.pps.caw.common.model.cell.PlayableCell]] to be drawn
    *   in the board
    */
  def drawSetupBoard(board: Board[PlayableCell]): Unit

  /** Draws the [[it.unibo.pps.caw.common.model.cell.BaseCell]] received in input during the game phase.
    *
    * @param board
    *   the [[it.unibo.pps.caw.common.model.Board]] containing the [[it.unibo.pps.caw.common.model.cell.BaseCell]] to be drawn in
    *   the board
    */
  def drawGameBoard(board: Board[BaseCell]): Unit
}

/** Companion object of the [[GameBoardView]] trait, containing its factory method. */
object GameBoardView {

  /** Returns a new instance of the [[GameBoardView]] trait. It receives the screen width and height, necessary to calculate the
    * size of the board, the [[it.unibo.pps.caw.common.model.Level]] to be drawn and the
    * [[it.unibo.pps.caw.common.view.ModelUpdater]], necessary to update the model after the player modifies the view.
    *
    * @param screenWidth
    *   the width of the screen necessary to calculate the board width
    * @param screenHeight
    *   the height of the screen necessary to calculate the board height
    * @param initialLevel
    *   the [[it.unibo.pps.caw.common.model.Level]] to be drawn
    * @param modelUpdater
    *   the [[it.unibo.pps.caw.common.view.ModelUpdater]] necessary to update the model after view changes
    * @return
    *   a new instance of [[GameBoardView]]
    */
  def apply(
    screenWidth: Double,
    screenHeight: Double,
    initialLevel: Level[PlayableCell],
    modelUpdater: ModelUpdater
  ): GameBoardView =
    GameBoardViewImpl(screenWidth, screenHeight, initialLevel, modelUpdater)

  /* Extension of AbstractBoardView */
  private class GameBoardViewImpl(
    screenWidth: Double,
    screenHeight: Double,
    initialLevel: Level[PlayableCell],
    modelUpdater: ModelUpdater
  ) extends AbstractBoardView(
      screenWidth,
      screenHeight,
      initialLevel.dimensions.width,
      initialLevel.dimensions.height,
      modelUpdater
    )
    with GameBoardView {

    drawSetupBoard(initialLevel.board)

    override def drawGameBoard(board: Board[BaseCell]): Unit = draw(board)

    override def drawSetupBoard(board: Board[PlayableCell]): Unit = draw(board, droppablePlayableArea = true)

    private def draw(cells: Set[PlayableCell] | Set[BaseCell], droppablePlayableArea: Boolean = false): Unit = {
      clearComponents()
      drawFloor()
      drawPlayableArea(initialLevel.playableArea.position, initialLevel.playableArea.dimensions, droppablePlayableArea)
      cells.foreach(c => drawImageView(CellView(c, innerComponent).innerComponent, c.position.x, c.position.y))
    }
  }
}
