package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.model.{Board, Level, PlayableArea, Position}
import it.unibo.pps.caw.common.model.Board.*
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.model.cell.PlayableCell.toPlayableCell
import it.unibo.pps.caw.game.model.engine.RulesEngine

/** The model of the game, the component containing all its business logic.
  *
  * The model component contains all the logic of the game, deciding how the current state of the game should evolve given the
  * input that the player has given or the passing of a game time "instant". More specifically, a game is made of several
  * [[Level]] played in sequence or by a single level played alone. Nevertheless, the game behavior is always the same: the player
  * begins playing from the first level that has selected and advances one level at a time, until the last level is reached. When
  * the last level is completed, the game ends. In case the level the player wants to play is only one, it is in fact also the
  * last level and, when completed, the game ends. A level is considered completed if and only if no [[EnemyCell]] are present on
  * the board. For each level, the game divides in two macro-phases. The first is a "setup" phase, where the player can move
  * around the [[Cell]] in the [[PlayableArea]] of the [[Level]] and arrange the cells as they please. The second phase is the
  * phase where the time starts ticking and the system evolve and the player can not interact with the world anymore. Giving that
  * the player not necessarily configured the level to destroy all enemy cells the first time, it is given to the player the
  * possibility to reset the state of the game to just before the second phase beginned.
  */
sealed trait GameModel {

  /** Allows to move a cell during the "setup" phase of the game given its current [[Position]] and the [[Position]] in which
    * needs to be moved. If no cell is present in the specified current position, no action is performed. If the game is already
    * in the second phase, so the method [[GameModel.update]] has already been called once, no action is performed.
    *
    * @param currentPosition
    *   the [[Position]] of the [[Cell]] to move
    * @param nextPosition
    *   the [[Position]] of the [[Cell]] at the current position to which move it next
    * @return
    *   an updated instance of [[GameModel]] where the [[Cell]] has been moved, if possible
    */
  def moveCell(currentPosition: Position)(nextPosition: Position): GameModel

  /** Updates the current state of the game, obtaining its next one.
    *
    * @return
    *   an updated instance of [[GameModel]] where the state has been updated
    */
  def update: GameModel

  /** Makes the game to swith to the next [[Level]]. This is only possible if the current level has been completed. If not, no
    * action will be performed.
    *
    * @return
    *   an updated instance of [[GameModel]] where the level has been switched to the next, if possible
    */
  def nextLevel: GameModel

  /** Resets the current [[Level]] to its state before the second phase of the game beginned, so before the first call to the
    * [[GameModel.update]] method, allowing again the player to arrange the [[Cell]] in this level.
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

  /** Returns whether or not the given [[Position]] is inside the given [[PlayableArea]].
    *
    * @param position
    *   the [[Position]] to be checked
    * @param playableArea
    *   the [[PlayableArea]] in which the [[Position]] could be contained
    * @return
    *   whether or not the given [[Position]] is inside the given [[PlayableArea]]
    */
  private def isPositionInsidePlayableArea(position: Position)(playableArea: PlayableArea): Boolean =
    position.x >= playableArea.position.x &&
      position.x <= (playableArea.position.x + playableArea.dimensions.width) &&
      position.y >= playableArea.position.y &&
      position.y <= (playableArea.position.y + playableArea.dimensions.height)

  /** Returns whether or not a [[it.unibo.pps.caw.common.model.Level]] is completed given its [[Board]]. This is the only
    * necessary element for evaluating it because a level is considered completed when no [[EnemyCell]] are present on the board.
    *
    * @param board
    *   the [[Board]] of the [[it.unibo.pps.caw.common.model.Level]] to be used for evaluating if the level has been completed or
    *   not
    * @return
    *   whether or not a [[it.unibo.pps.caw.common.model.Level]] is completed
    */
  private def isLevelCompleted(board: Board[? <: Cell]): Boolean = board.filter(_.isInstanceOf[EnemyCell]).size == 0

  /* Default implementation of the GameModel trait. */
  private class GameModelImpl(
    rulesEngine: RulesEngine,
    val state: GameState,
    isSetupCompleted: Boolean,
    levels: Seq[Level[BaseCell]],
    initialBoard: Board[BaseCell],
    currentBoard: Board[BaseCell]
  ) extends GameModel {

    /* Alternative constructor to be used by the "apply" factory method. */
    def this(
      rulesEngine: RulesEngine,
      levels: Seq[Level[BaseCell]],
      initialLevel: Level[PlayableCell],
      initialIndex: Int,
      initialBoard: Board[BaseCell]
    ) =
      this(
        rulesEngine,
        GameState(
          initialLevel,
          initialLevel.copy(board = initialLevel.board.map(_.changePlayableProperty(playable = false))),
          initialIndex,
          initialIndex + 1 <= levels.length,
          didEnemyDie = false,
          isLevelCompleted(initialBoard)
        ),
        isSetupCompleted = false,
        levels,
        initialBoard,
        initialBoard
      )

    override def update: GameModel = {
      val updatedBoard: Board[BaseCell] = rulesEngine.update(currentBoard)
      val enemiesInBoard: Board[? <: Cell] => Int = _.filter(_.isInstanceOf[EnemyCell]).size
      GameModelImpl(
        rulesEngine,
        state.copy(
          currentStateLevel = state.levelCurrentState.copy(board = updatedBoard.map(_.toPlayableCell(_ => false))),
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
          currentStateLevel =
            state.levelInitialState.copy(board = state.levelInitialState.board.map(_.changePlayableProperty(playable = false))),
          didEnemyDie = false,
          isCurrentLevelCompleted = isLevelCompleted(state.levelInitialState.board)
        ),
        isSetupCompleted = false,
        levels,
        initialBoard,
        initialBoard
      )

    override def moveCell(currentPosition: Position)(nextPosition: Position): GameModel =
      if (!isSetupCompleted)
        currentBoard
          .find(_.position == currentPosition)
          .map(_.changePositionProperty(_ => nextPosition))
          .map(c => currentBoard.filter(_.position != currentPosition) + c)
          .map(b =>
            GameModelImpl(
              rulesEngine,
              state.copy(
                initialStateLevel = state
                  .levelCurrentState
                  .copy(board =
                    b.map(_.toPlayableCell(c => isPositionInsidePlayableArea(c.position)(state.levelCurrentState.playableArea)))
                  ),
                currentStateLevel = state.levelCurrentState.copy(board = b.map(_.toPlayableCell(_ => false)))
              ),
              isSetupCompleted = false,
              levels,
              b,
              b
            )
          )
          .getOrElse(this)
      else this
  }

  /** Returns a new instance of the [[GameModel]] trait to be used when playing a game with the default [[Level]]. For creating a
    * new [[GameModel]] are indeed necessary the [[Seq]] of all default levels that have been previously stored and the index of
    * the level from which beginning the game. The index of the level is equal to the position of the level in the sequence plus
    * one.
    *
    * @param levels
    *   the [[Seq]] of default [[Level]] previously stored
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
    GameModelImpl(
      rulesEngine,
      levels,
      Level(
        (levels(initialIndex - 1).dimensions.width + 2, levels(initialIndex - 1).dimensions.height + 2),
        boardWithCorners.map(
          _.toPlayableCell(c => isPositionInsidePlayableArea(c.position)(playableAreaWithCorners))
        ),
        playableAreaWithCorners
      ),
      initialIndex,
      boardWithCorners
    )
  }

  /** Returns a new instance of the [[GameModel]] trait to be used when playing a game with a [[Level]] created by a player. For
    * creating a new [[GameModel]] is indeed necessary the level which will be played during the game.
    *
    * @param level
    *   the [[Level]] created by a player to be played during this game
    * @return
    *   a new [[GameModel]] instance
    */
  def apply(rulesEngine: RulesEngine, level: Level[BaseCell]): GameModel = GameModel(rulesEngine, Seq(level), 1)
}
