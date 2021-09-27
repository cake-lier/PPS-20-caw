package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.game.model.engine.RulesEngine

import scala.annotation.tailrec

/** Trait representing the model of the game. Changing the state creates another [[GameModel]] instance */
sealed trait GameModel {

  /** Update the position of a cell during setup phase
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
  private class GameModelImpl(val state: GameState, initialBoard: Board[BaseCell], currentBoard: Board[BaseCell])
    extends GameModel {

    def this(
      initialLevel: Level[SetupCell],
      levelIndex: Option[Int],
      initialBoard: Board[BaseCell]
    ) =
      this(
        GameState(
          initialLevel,
          initialLevel.copy(board = Board(initialLevel.board.cells.map(convertToCurrentSetup(_)))),
          levelIndex.map(_ + 1).filter(_ < 30),
          initialBoard.cells.filter(_.isInstanceOf[EnemyCell]).size == 0,
          false
        ),
        initialBoard,
        initialBoard
      )

    override def update: GameModel = {
      @tailrec
      def update(cells: Seq[UpdateCell], board: Board[UpdateCell]): Board[UpdateCell] = {
        Seq(
          cells.filter(_.isInstanceOf[UpdateGeneratorCell]).toSeq.sorted,
          cells.filter(_.isInstanceOf[UpdateRotatorCell]).toSeq.sorted,
          cells.filter(_.isInstanceOf[UpdateMoverCell]).toSeq.sorted
        ).flatten match {
          case h :: t if (!h.updated) => {
            val newBoard = RulesEngine().nextState(board, h)
            update(newBoard.cells.toSeq, newBoard)
          }
          case h :: t => update(t, board) // ignore already updated cells
          case _      => board
        }
      }
      // parse Board[Cell] to Board[UpdateCell]
      val idBoard: Board[UpdateCell] = Board(
        currentBoard
          .cells
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
      )
      val updatedBoard = Board(
        update(idBoard.cells.toSeq, idBoard)
          .cells
          .map(_ match {
            case UpdateRotatorCell(p, r, _, _)   => BaseRotatorCell(p, r)
            case UpdateGeneratorCell(p, o, _, _) => BaseGeneratorCell(p, o)
            case UpdateEnemyCell(p, _, _)        => BaseEnemyCell(p)
            case UpdateMoverCell(p, o, _, _)     => BaseMoverCell(p, o)
            case UpdateBlockCell(p, d, _, _)     => BaseBlockCell(p, d)
            case UpdateWallCell(p, _, _)         => BaseWallCell(p)
          })
      )
      GameModelImpl(
        state.copy(
          currentStateLevel = state.currentStateLevel.copy(board = Board(updatedBoard.cells.map(convertBaseToCurrentSetup(_)))),
          didEnemyDie = state.currentStateLevel.board.cells.filter(_.isInstanceOf[EnemyCell]).size >
            updatedBoard.cells.filter(_.isInstanceOf[EnemyCell]).size,
          isCurrentLevelCompleted = updatedBoard.cells.filter(_.isInstanceOf[EnemyCell]).size == 0
        ),
        initialBoard,
        updatedBoard
      )
    }

    override def reset: GameModel = GameModelImpl(
      state.copy(
        currentStateLevel = state
          .initialStateLevel
          .copy(board = Board(state.initialStateLevel.board.cells.map(convertToCurrentSetup(_)))),
        didEnemyDie = false,
        isCurrentLevelCompleted = state.initialStateLevel.board.cells.filter(_.isInstanceOf[SetupEnemyCell]).size == 0
      ),
      initialBoard,
      initialBoard
    )

    override def updateCell(previousPosition: Position, currentPosition: Position): GameModel = {
      val updatedCell: BaseCell =
        currentBoard
          .cells
          .find(_.position == previousPosition)
          .map(_ match {
            case _: BaseWallCell         => BaseWallCell(currentPosition)
            case _: BaseEnemyCell        => BaseEnemyCell(currentPosition)
            case BaseRotatorCell(_, r)   => BaseRotatorCell(currentPosition, r)
            case BaseGeneratorCell(_, o) => BaseGeneratorCell(currentPosition, o)
            case BaseMoverCell(_, o)     => BaseMoverCell(currentPosition, o)
            case BaseBlockCell(_, p)     => BaseBlockCell(currentPosition, p)
          })
          .get
      val updatedBoard: Board[BaseCell] = Board(currentBoard.cells.filter(_.position != previousPosition) + updatedCell)
      GameModelImpl(
        state.copy(currentStateLevel =
          state.currentStateLevel.copy(board = Board(updatedBoard.cells.map(convertBaseToCurrentSetup(_))))
        ),
        initialBoard,
        updatedBoard
      )
    }
  }

  def apply(initialLevelState: Level[BaseCell], levelIndex: Option[Int]): GameModel = {
    val cornerWalls: Set[BaseCell] =
      (0 to initialLevelState.dimensions.width + 1).map(i => BaseWallCell((i, 0))).toSet ++
        (0 to initialLevelState.dimensions.width + 1).map(i => BaseWallCell(i, initialLevelState.dimensions.height + 1)) ++
        (1 to initialLevelState.dimensions.height).map(i => BaseWallCell(0, i)) ++
        (1 to initialLevelState.dimensions.height).map(i => BaseWallCell(initialLevelState.dimensions.width + 1, i))
    val boardWithCorners: Board[BaseCell] = Board(
      initialLevelState
        .board
        .cells
        .map(_ match {
          case BaseRotatorCell(p, r)   => BaseRotatorCell((p.x + 1, p.y + 1), r)
          case BaseGeneratorCell(p, o) => BaseGeneratorCell((p.x + 1, p.y + 1), o)
          case BaseEnemyCell(p)        => BaseEnemyCell((p.x + 1, p.y + 1))
          case BaseMoverCell(p, o)     => BaseMoverCell((p.x + 1, p.y + 1), o)
          case BaseBlockCell(p, d)     => BaseBlockCell((p.x + 1, p.y + 1), d)
          case BaseWallCell(p)         => BaseWallCell((p.x + 1, p.y + 1))
        }) ++ cornerWalls
    )
    val playableAreaWithCorners: PlayableArea = PlayableArea(
      (initialLevelState.playableArea.position.x + 1, initialLevelState.playableArea.position.y + 1),
      initialLevelState.playableArea.dimensions
    )
    val levelWithCorners: Level[SetupCell] = Level(
      (initialLevelState.dimensions.width + 2, initialLevelState.dimensions.height + 2),
      Board(boardWithCorners.cells.map(convertBaseToInitialSetup(playableAreaWithCorners)(_))),
      playableAreaWithCorners
    )
    GameModelImpl(levelWithCorners, levelIndex, boardWithCorners)
  }

  private def convertBaseToCurrentSetup(cell: BaseCell): SetupCell = cell match {
    case BaseRotatorCell(p, r)   => SetupRotatorCell(p, r)
    case BaseGeneratorCell(p, o) => SetupGeneratorCell(p, o)
    case BaseEnemyCell(p)        => SetupEnemyCell(p)
    case BaseMoverCell(p, o)     => SetupMoverCell(p, o)
    case BaseBlockCell(p, d)     => SetupBlockCell(p, d)
    case BaseWallCell(p)         => SetupWallCell(p)
  }

  private def convertToCurrentSetup(cell: SetupCell): SetupCell = cell match {
    case SetupRotatorCell(p, r, _)   => SetupRotatorCell(p, r)
    case SetupGeneratorCell(p, o, _) => SetupGeneratorCell(p, o)
    case SetupEnemyCell(p, _)        => SetupEnemyCell(p)
    case SetupMoverCell(p, o, _)     => SetupMoverCell(p, o)
    case SetupBlockCell(p, d, _)     => SetupBlockCell(p, d)
    case SetupWallCell(p, _)         => SetupWallCell(p)
  }

  private def convertBaseToInitialSetup(playableArea: PlayableArea)(cell: BaseCell): SetupCell = {
    def isCellInsidePlayableArea(position: Position): Boolean =
      position.x >= playableArea.position.x &&
        position.x <= (playableArea.position.x + playableArea.dimensions.width) &&
        position.y >= playableArea.position.y &&
        position.y <= (playableArea.position.y + playableArea.dimensions.height)
    cell match {
      case BaseRotatorCell(p, r)   => SetupRotatorCell(p, r, isCellInsidePlayableArea(p))
      case BaseGeneratorCell(p, o) => SetupGeneratorCell(p, o, isCellInsidePlayableArea(p))
      case BaseEnemyCell(p)        => SetupEnemyCell(p, isCellInsidePlayableArea(p))
      case BaseMoverCell(p, o)     => SetupMoverCell(p, o, isCellInsidePlayableArea(p))
      case BaseBlockCell(p, d)     => SetupBlockCell(p, d, isCellInsidePlayableArea(p))
      case BaseWallCell(p)         => SetupWallCell(p, isCellInsidePlayableArea(p))
    }
  }
}
