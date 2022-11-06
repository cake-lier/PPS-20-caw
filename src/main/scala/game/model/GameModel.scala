package it.unibo.pps.caw
package game.model

import common.model.*
import common.model.Board.*
import common.model.cell.*
import common.model.cell.PlayableCell.toPlayableCell
import game.model.engine.RulesEngine

/** The model of the game, the component containing all its business logic.
  *
  * The model component contains all the logic of the game, deciding how the current state of the game should evolve given the
  * input that the player has given or the passing of a game time "instant". More specifically, a game is made of several
  * [[it.unibo.pps.caw.common.model.Level]] played in sequence or by a single level played alone. Nevertheless, the game behavior
  * is always the same: the player begins playing from the first level that has selected and advances one level at a time, until
  * the last level is reached. When the last level is completed, the game ends. In case the level the player wants to play is only
  * one, it is in fact also the last level and, when completed, the game ends. A level is considered completed if and only if no
  * [[it.unibo.pps.caw.common.model.cell.EnemyCell]] are present on the board. For each level, the game divides in two
  * macro-phases. The first is a "setup" phase, where the player can move around the [[it.unibo.pps.caw.common.model.cell.Cell]]
  * in the [[it.unibo.pps.caw.common.model.PlayableArea]] of the level and arrange the cells as they please. The second phase is
  * the phase where the time starts ticking and the system evolve and the player can not interact with the world anymore. Giving
  * that the player not necessarily configured the level to destroy all enemy cells the first time, it is given to the player the
  * possibility to reset the state of the game to just before the second phase begins.
  */
trait GameModel {

  /** Allows to move a cell during the "setup" phase of the game given its current [[it.unibo.pps.caw.common.model.Position]] and
    * the position in which needs to be moved. If no cell is present in the specified current position, no action is performed. If
    * the game is already in the second phase, so the method [[GameModel.update]] has already been called once, no action is
    * performed.
    *
    * @param currentPosition
    *   the [[it.unibo.pps.caw.common.model.Position]] of the [[it.unibo.pps.caw.common.model.cell.Cell]] to move
    * @param nextPosition
    *   the [[it.unibo.pps.caw.common.model.Position]] of the [[it.unibo.pps.caw.common.model.cell.Cell]] at the current position
    *   to which move it next
    * @return
    *   an updated instance of [[GameModel]] where the [[it.unibo.pps.caw.common.model.cell.Cell]] has been moved, if possible
    */
  def moveCell(currentPosition: Position, nextPosition: Position): GameModel

  /** Updates the current state of the game, obtaining its next one.
    *
    * @return
    *   an updated instance of [[GameModel]] where the state has been updated
    */
  def update: GameModel

  /** Makes the game to switch to the next [[it.unibo.pps.caw.common.model.Level]]. This is only possible if the current level has
    * been completed. If not, no action will be performed.
    *
    * @return
    *   an updated instance of [[GameModel]] where the level has been switched to the next, if possible
    */
  def nextLevel: GameModel

  /** Resets the current [[it.unibo.pps.caw.common.model.Level]] to its state before the second phase of the game begins, so
    * before the first call to the [[GameModel.update]] method, allowing again the player to arrange the
    * [[it.unibo.pps.caw.common.model.cell.Cell]] in this level.
    *
    * @return
    *   an updated instance of [[GameModel]] where its state it is the same as before starting to update it
    */
  def reset: GameModel

  /** Returns the current [[GameState]]. */
  val state: GameState
}

/** Companion object for trait [[GameModel]], containing its factory methods. */
object GameModel {

  /** Returns whether or not the given [[it.unibo.pps.caw.common.model.Position]] is inside the given
    * [[it.unibo.pps.caw.common.model.PlayableArea]].
    *
    * @param position
    *   the [[it.unibo.pps.caw.common.model.Position]] to be checked
    * @param playableArea
    *   the [[it.unibo.pps.caw.common.model.PlayableArea]] in which the [[it.unibo.pps.caw.common.model.Position]] could be
    *   contained
    * @return
    *   whether or not the given [[it.unibo.pps.caw.common.model.Position]] is inside the given
    *   [[it.unibo.pps.caw.common.model.PlayableArea]]
    */
  private def isPositionInsidePlayableArea(position: Position)(playableArea: PlayableArea): Boolean =
    position.x >= playableArea.position.x &&
      position.x < (playableArea.position.x + playableArea.dimensions.width) &&
      position.y >= playableArea.position.y &&
      position.y < (playableArea.position.y + playableArea.dimensions.height)

  /** Returns whether or not a [[it.unibo.pps.caw.common.model.Level]] is completed given its
    * [[it.unibo.pps.caw.common.model.Board]]. This is the only necessary element for evaluating it because a level is considered
    * completed when no [[it.unibo.pps.caw.common.model.cell.EnemyCell]] are present on the board.
    *
    * @param board
    *   the [[it.unibo.pps.caw.common.model.Board]] of the [[it.unibo.pps.caw.common.model.Level]] to be used for evaluating if
    *   the level has been completed or not
    * @return
    *   whether or not a [[it.unibo.pps.caw.common.model.Level]] is completed
    */
  private def isLevelCompleted(board: Board[? <: Cell]): Boolean = !board.exists(_.isInstanceOf[EnemyCell])

  /* Default implementation of the GameModel trait. */
  private class GameModelImpl(
    rulesEngine: RulesEngine,
    val state: GameState,
    isSetupCompleted: Boolean,
    levels: Seq[Level[BaseCell]],
    initialBoard: Board[BaseCell],
    currentBoard: Board[BaseCell]
  ) extends GameModel {

    override def update: GameModel = {
      val updatedBoard: Board[BaseCell] = rulesEngine.update(currentBoard)
      val enemiesInBoard: Board[? <: Cell] => Int = _.count(_.isInstanceOf[EnemyCell])
      GameModelImpl(
        rulesEngine,
        state.copy(
          levelCurrentState = state.levelCurrentState.copy(board = updatedBoard),
          didEnemyDie = enemiesInBoard(state.levelCurrentState.board) > enemiesInBoard(updatedBoard),
          isCurrentLevelCompleted = isLevelCompleted(updatedBoard)
        ),
        isSetupCompleted = true,
        levels,
        initialBoard,
        updatedBoard
      )
    }

    override def nextLevel: GameModel =
      if (state.hasNextLevel && state.isCurrentLevelCompleted)
        GameModel(rulesEngine, levels, state.currentLevelIndex + 1)
      else
        this

    override def reset: GameModel =
      GameModelImpl(
        rulesEngine,
        state.copy(
          levelCurrentState = Level(
            state.levelInitialState.dimensions,
            state.levelInitialState.board.map(_.toBaseCell),
            state.levelInitialState.playableArea
          ),
          didEnemyDie = false,
          isCurrentLevelCompleted = isLevelCompleted(state.levelInitialState.board)
        ),
        isSetupCompleted = false,
        levels,
        initialBoard,
        initialBoard
      )

    override def moveCell(currentPosition: Position, nextPosition: Position): GameModel =
      if (
        !isSetupCompleted &&
        isPositionInsidePlayableArea(nextPosition)(state.levelCurrentState.playableArea)
      ) {
        lazy val current: Position = currentPosition
        (currentBoard.find(_.position == nextPosition), currentBoard.find(_.position == current)) match {
          case (None, Some(c)) =>
            val newBoard: Board[BaseCell] =
              currentBoard.filter(_.position != current) + c.changePositionProperty(_ => nextPosition)
            GameModelImpl(
              rulesEngine,
              state.copy(
                levelInitialState = state
                  .levelInitialState
                  .copy(
                    board = newBoard.map(
                      _.toPlayableCell(c => isPositionInsidePlayableArea(c.position)(state.levelInitialState.playableArea))
                    )
                  ),
                levelCurrentState = state.levelCurrentState.copy(board = newBoard)
              ),
              isSetupCompleted = false,
              levels,
              newBoard,
              newBoard
            )
          case _ => this
        }
      } else {
        this
      }
  }

  /** Returns a new instance of the [[GameModel]] trait to be used when playing a game with the default
    * [[it.unibo.pps.caw.common.model.Level]]. For creating a new [[GameModel]] are indeed necessary the [[Seq]] of all default
    * levels that have been previously stored and the index of the level from which beginning the game. The index of the level is
    * equal to the position of the level in the sequence plus one.
    *
    * @param levels
    *   the [[Seq]] of default [[it.unibo.pps.caw.common.model.Level]] previously stored
    * @param initialIndex
    *   the index of the level from which starting the game
    * @return
    *   a new [[GameModel]] instance
    */
  def apply(rulesEngine: RulesEngine, levels: Seq[Level[BaseCell]], initialIndex: Int): GameModel = {
    val boardWithCorners: Board[BaseCell] =
      levels(initialIndex - 1)
        .board
        .map(_.changePositionProperty(c => (c.position.x + 1, c.position.y + 1))) ++
        Set(
          (0 to levels(initialIndex - 1).dimensions.width + 1).map(i => BaseWallCell((i, 0))),
          (0 to levels(initialIndex - 1).dimensions.width + 1)
            .map(i => BaseWallCell(i, levels(initialIndex - 1).dimensions.height + 1)),
          (1 to levels(initialIndex - 1).dimensions.height).map(i => BaseWallCell(0, i)),
          (1 to levels(initialIndex - 1).dimensions.height)
            .map(i => BaseWallCell(levels(initialIndex - 1).dimensions.width + 1, i))
        ).flatten
    val playableAreaWithCorners: PlayableArea = PlayableArea(levels(initialIndex - 1).playableArea.dimensions)(
      (levels(initialIndex - 1).playableArea.position.x + 1, levels(initialIndex - 1).playableArea.position.y + 1)
    )
    val initialLevel: Level[PlayableCell] = Level(
      (levels(initialIndex - 1).dimensions.width + 2, levels(initialIndex - 1).dimensions.height + 2),
      boardWithCorners.map(
        _.toPlayableCell(c => isPositionInsidePlayableArea(c.position)(playableAreaWithCorners))
      ),
      playableAreaWithCorners
    )
    GameModelImpl(
      rulesEngine,
      GameState(
        initialLevel,
        Level(
          initialLevel.dimensions,
          initialLevel.board.map(_.toBaseCell),
          initialLevel.playableArea
        ),
        initialIndex,
        initialIndex + 1 <= levels.length,
        didEnemyDie = false,
        isLevelCompleted(boardWithCorners)
      ),
      isSetupCompleted = false,
      levels,
      boardWithCorners,
      boardWithCorners
    )
  }

  /** Returns a new instance of the [[GameModel]] trait to be used when playing a game with a
    * [[it.unibo.pps.caw.common.model.Level]] created by a player. For creating a new [[GameModel]] is indeed necessary the level
    * which will be played during the game.
    *
    * @param level
    *   the [[it.unibo.pps.caw.common.model.Level]] created by a player to be played during this game
    * @return
    *   a new [[GameModel]] instance
    */
  def apply(rulesEngine: RulesEngine, level: Level[BaseCell]): GameModel = GameModel(rulesEngine, Seq(level), 1)
}
