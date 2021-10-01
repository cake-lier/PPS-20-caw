package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.model.{Board, PlayableArea, Position}

/** Object containing methods useful to the [[GameModel]] for performing its tasks. */
object GameModelHelpers {

  /** Returns whether or not the given [[Position]] is inside the given [[PlayableArea]].
    *
    * @param position
    *   the [[Position]] to be checked
    * @param playableArea
    *   the [[PlayableArea]] in which the [[Position]] could be contained
    * @return
    *   whether or not the given [[Position]] is inside the given [[PlayableArea]]
    */
  def isPositionInsidePlayableArea(position: Position)(playableArea: PlayableArea): Boolean =
    position.x >= playableArea.position.x &&
      position.x <= (playableArea.position.x + playableArea.dimensions.width) &&
      position.y >= playableArea.position.y &&
      position.y <= (playableArea.position.y + playableArea.dimensions.height)

  /** Resets the [[PlayableCell.playable]] property of a [[PlayableCell]] to its base value, <code>false</code>.
    *
    * @param cell
    *   the [[PlayableCell]] to which resetting its [[PlayableCell.playable]] property
    * @return
    *   the same [[PlayableCell]] with its [[PlayableCell.playable]] property reset to <code>false</code>
    */
  def resetPlayableCell(cell: PlayableCell): PlayableCell = cell match {
    case PlayableRotatorCell(p, r, _)   => PlayableRotatorCell(p, r, playable = false)
    case PlayableGeneratorCell(p, o, _) => PlayableGeneratorCell(p, o, playable = false)
    case PlayableEnemyCell(p, _)        => PlayableEnemyCell(p, playable = false)
    case PlayableMoverCell(p, o, _)     => PlayableMoverCell(p, o, playable = false)
    case PlayableBlockCell(p, d, _)     => PlayableBlockCell(p, d, playable = false)
    case PlayableWallCell(p, _)         => PlayableWallCell(p, playable = false)
  }

  /** Changes the [[BaseCell.position]] property of the given [[BaseCell]] with the result of the given function. The function may
    * depend on the [[BaseCell]] on which the property is going to be changed.
    *
    * @param cell
    *   the [[BaseCell]] to which changing its [[BaseCell.position]] property.
    * @param getPosition
    *   the function used for getting the [[Position]] to set to the given [[BaseCell]]
    * @return
    *   a new [[BaseCell]] with its [[Position]] changed according to the result of the given function
    */
  def changeBaseCellPosition(cell: BaseCell)(getPosition: BaseCell => Position): BaseCell = cell match {
    case _: BaseWallCell         => BaseWallCell(getPosition(cell))
    case _: BaseEnemyCell        => BaseEnemyCell(getPosition(cell))
    case BaseRotatorCell(_, r)   => BaseRotatorCell(getPosition(cell), r)
    case BaseGeneratorCell(_, o) => BaseGeneratorCell(getPosition(cell), o)
    case BaseMoverCell(_, o)     => BaseMoverCell(getPosition(cell), o)
    case BaseBlockCell(_, p)     => BaseBlockCell(getPosition(cell), p)
  }

  /** Returns whether or not a [[it.unibo.pps.caw.common.model.Level]] is completed given its [[Board]]. This is the only
    * necessary element for evaluating it because a level is considered completed when no [[EnemyCell]] are present on the board.
    *
    * @param board
    *   the [[Board]] of the [[it.unibo.pps.caw.common.model.Level]] to be used for evaluating if the level has been completed or
    *   not
    * @return
    *   whether or not a [[it.unibo.pps.caw.common.model.Level]] is completed
    */
  def isLevelCompleted(board: Board[? <: Cell]): Boolean = board.filter(_.isInstanceOf[EnemyCell]).size == 0
}
