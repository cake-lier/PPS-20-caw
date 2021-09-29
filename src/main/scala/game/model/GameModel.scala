package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.model.{Board, Level, PlayableArea, Position}
import it.unibo.pps.caw.common.model.Board.*
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.game.model.engine.RulesEngine

import scala.annotation.tailrec

/** The model of the game, containing all its business logic
  */
sealed trait GameModel {

  /** Update the getPosition of a cell during setup phase
    *
    * @param previousPosition:
    *   previuos [[Position]] for previuos [[Cell]]
    * @param currentPosition:
    *   new [[Position]] for [[Cell]]
    * @return
    *   updated instance of [[GameModel]]
    */
  def updateCell(previousPosition: Position, currentPosition: Position): GameModel

  /** Calculate the next state of the current [[Board]]
    *
    * @return
    *   updated instance of [[GameModel]]
    */
  def update: GameModel

  /** Set the current board as the initial [[Board]]
    *
    * @return
    *   resetted instance of [[GameModel]]
    */
  def reset: GameModel

  val state: GameState
}

/** Companion object for trait [[GameModel]]. */
object GameModel {

  def isPositionInsidePlayableArea(playableArea: PlayableArea)(position: Position): Boolean =
    position.x >= playableArea.position.x &&
      position.x <= (playableArea.position.x + playableArea.dimensions.width) &&
      position.y >= playableArea.position.y &&
      position.y <= (playableArea.position.y + playableArea.dimensions.height)

  private def resetPlayableCell(cell: PlayableCell): PlayableCell = cell match {
    case PlayableRotatorCell(p, r, _)   => PlayableRotatorCell(p, r, false)
    case PlayableGeneratorCell(p, o, _) => PlayableGeneratorCell(p, o, false)
    case PlayableEnemyCell(p, _)        => PlayableEnemyCell(p, false)
    case PlayableMoverCell(p, o, _)     => PlayableMoverCell(p, o, false)
    case PlayableBlockCell(p, d, _)     => PlayableBlockCell(p, d, false)
    case PlayableWallCell(p, _)         => PlayableWallCell(p, false)
  }

  private def changeBaseCellPosition(cell: BaseCell)(getPosition: BaseCell => Position): BaseCell = cell match {
    case _: BaseWallCell         => BaseWallCell(getPosition(cell))
    case _: BaseEnemyCell        => BaseEnemyCell(getPosition(cell))
    case BaseRotatorCell(_, r)   => BaseRotatorCell(getPosition(cell), r)
    case BaseGeneratorCell(_, o) => BaseGeneratorCell(getPosition(cell), o)
    case BaseMoverCell(_, o)     => BaseMoverCell(getPosition(cell), o)
    case BaseBlockCell(_, p)     => BaseBlockCell(getPosition(cell), p)
  }

  private def isLevelCompleted(board: Board[? <: Cell]): Boolean = board.filter(_.isInstanceOf[EnemyCell]).size == 0

  /* Default implementation of the GameModel trait. */
  private class GameModelImpl(val state: GameState, initialBoard: Board[BaseCell], currentBoard: Board[BaseCell])
    extends GameModel {

    def this(initialLevel: Level[PlayableCell], levelIndex: Option[Int], initialBoard: Board[BaseCell]) =
      this(
        GameState(
          initialLevel,
          initialLevel.copy(board = initialLevel.board.map(resetPlayableCell(_))),
          levelIndex.map(_ + 1).filter(_ < 30),
          isLevelCompleted(initialBoard),
          false
        ),
        initialBoard,
        initialBoard
      )

    override def update: GameModel = {
      @tailrec
      def update(cells: Seq[UpdateCell], board: Board[UpdateCell]): Board[UpdateCell] = {
        Seq(
          cells.filter(_.isInstanceOf[GeneratorCell]).toSeq.sorted,
          cells.filter(_.isInstanceOf[RotatorCell]).toSeq.sorted,
          cells.filter(_.isInstanceOf[MoverCell]).toSeq.sorted
        ).flatten match {
          case h :: t if (!h.updated) => {
            val newBoard = RulesEngine().nextState(board, h)
            update(newBoard.cells.toSeq, newBoard)
          }
          case h :: t => update(t, board) // ignore already updated cells
          case _      => board
        }
      }
      val originalBoard: Board[UpdateCell] =
        currentBoard
          .zipWithIndex
          .map((c, i) =>
            c match {
              case BaseMoverCell(p, o)     => UpdateMoverCell(p, o, i, false)
              case BaseGeneratorCell(p, o) => UpdateGeneratorCell(p, o, i, false)
              case BaseRotatorCell(p, r)   => UpdateRotatorCell(p, r, i, false)
              case BaseBlockCell(p, d)     => UpdateBlockCell(p, d, i, false)
              case BaseEnemyCell(p)        => UpdateEnemyCell(p, i, false)
              case BaseWallCell(p)         => UpdateWallCell(p, i, false)
            }
          )
      val updatedBoard: Board[BaseCell] =
        update(originalBoard.toSeq, originalBoard)
          .map(_ match {
            case UpdateRotatorCell(p, r, _, _)   => BaseRotatorCell(p, r)
            case UpdateGeneratorCell(p, o, _, _) => BaseGeneratorCell(p, o)
            case UpdateEnemyCell(p, _, _)        => BaseEnemyCell(p)
            case UpdateMoverCell(p, o, _, _)     => BaseMoverCell(p, o)
            case UpdateBlockCell(p, d, _, _)     => BaseBlockCell(p, d)
            case UpdateWallCell(p, _, _)         => BaseWallCell(p)
          })
      GameModelImpl(
        state.copy(
          currentStateLevel = state.currentStateLevel.copy(board = updatedBoard.toPlayableCells(_ => false)),
          didEnemyDie = state.currentStateLevel.board.cells.filter(_.isInstanceOf[EnemyCell]).size >
            updatedBoard.filter(_.isInstanceOf[EnemyCell]).size,
          isCurrentLevelCompleted = isLevelCompleted(updatedBoard)
        ),
        initialBoard,
        updatedBoard
      )
    }

    override def reset: GameModel =
      GameModelImpl(
        state.copy(
          currentStateLevel = state
            .initialStateLevel
            .copy(board = state.initialStateLevel.board.map(resetPlayableCell(_))),
          didEnemyDie = false,
          isCurrentLevelCompleted = isLevelCompleted(state.initialStateLevel.board)
        ),
        initialBoard,
        initialBoard
      )

    override def updateCell(previousPosition: Position, currentPosition: Position): GameModel =
      currentBoard
        .find(_.position == previousPosition)
        .map(changeBaseCellPosition(_)(_ => currentPosition))
        .map(c => currentBoard.filter(_.position != previousPosition) + c)
        .map(b =>
          GameModelImpl(
            state.copy(
              initialStateLevel = state
                .currentStateLevel
                .copy(board =
                  b.toPlayableCells(c => isPositionInsidePlayableArea(state.currentStateLevel.playableArea)(c.position))
                ),
              currentStateLevel = state.currentStateLevel.copy(board = b.toPlayableCells(_ => false))
            ),
            b,
            b
          )
        )
        .getOrElse(this)
  }

  def apply(initialLevelState: Level[BaseCell], levelIndex: Option[Int]): GameModel = {
    val boardWithCorners: Board[BaseCell] =
      initialLevelState
        .board
        .map(changeBaseCellPosition(_)(c => (c.position.x + 1, c.position.y + 1))) ++
        Set(
          (0 to initialLevelState.dimensions.width + 1).map(i => BaseWallCell((i, 0))),
          (0 to initialLevelState.dimensions.width + 1).map(i => BaseWallCell(i, initialLevelState.dimensions.height + 1)),
          (1 to initialLevelState.dimensions.height).map(i => BaseWallCell(0, i)),
          (1 to initialLevelState.dimensions.height).map(i => BaseWallCell(initialLevelState.dimensions.width + 1, i))
        ).flatten
    val playableAreaWithCorners: PlayableArea = PlayableArea(
      (initialLevelState.playableArea.position.x + 1, initialLevelState.playableArea.position.y + 1),
      initialLevelState.playableArea.dimensions
    )
    GameModelImpl(
      Level(
        (initialLevelState.dimensions.width + 2, initialLevelState.dimensions.height + 2),
        boardWithCorners.toPlayableCells(c => isPositionInsidePlayableArea(playableAreaWithCorners)(c.position)),
        playableAreaWithCorners
      ),
      levelIndex,
      boardWithCorners
    )
  }
}
