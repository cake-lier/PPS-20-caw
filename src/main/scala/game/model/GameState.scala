package it.unibo.pps.caw
package game.model

import common.model.Level
import common.model.cell.{BaseCell, PlayableCell}

/** The state of the game, an entity collecting all information about the state of the [[GameModel]], which runs the current game.
  *
  * This entity has to hold the state of the [[GameModel]], which is made of pieces of information which are useful to be observed
  * externally from it and contribute to let the player understand what is happening in the game world right now and what is going
  * to happen next. These pieces of information regards the current [[it.unibo.pps.caw.common.model.Level]] being played, its
  * initial and current state, its index, if it has been completed or not, if there is a next level to be played and whether or
  * not an enemy died in the last game update. It must be constructed through its companion object.
  */
trait GameState {

  /** Returns the state of the [[it.unibo.pps.caw.common.model.Level]] currently being played from which the game begins updating
    * it.
    */
  val levelInitialState: Level[PlayableCell]

  /** Returns the state of the [[it.unibo.pps.caw.common.model.Level]] currently being played at its current state, after the last
    * update made by the game.
    */
  val levelCurrentState: Level[BaseCell]

  /** Returns whether or not the [[it.unibo.pps.caw.common.model.Level]] currently being played has been completed or not. */
  val isCurrentLevelCompleted: Boolean

  /** Returns the index of the [[it.unibo.pps.caw.common.model.Level]] currently being played. If the player is not playing the
    * default levels, its value is always zero.
    */
  val currentLevelIndex: Int

  /** Returns whether or not there is a [[it.unibo.pps.caw.common.model.Level]] to be played after the current one. */
  val hasNextLevel: Boolean

  /** Returns whether or not an [[it.unibo.pps.caw.common.model.cell.EnemyCell]] has died and was removed from the board after the
    * last update that the game did.
    */
  val didEnemyDie: Boolean
}

/** Companion object of the [[GameState]] trait, containing its factory method. */
object GameState {

  /* Default implementation of the GameState trait. */
  private case class GameStateImpl(
    levelInitialState: Level[PlayableCell],
    levelCurrentState: Level[BaseCell],
    currentLevelIndex: Int,
    hasNextLevel: Boolean,
    didEnemyDie: Boolean,
    isCurrentLevelCompleted: Boolean
  ) extends GameState

  /** Returns a new instance of the [[GameState]] trait given the initial and current state of the
    * [[it.unibo.pps.caw.common.model.Level]] currently being played for the game updates, the index of the
    * [[it.unibo.pps.caw.common.model.Level]] currently being played, if there is another [[it.unibo.pps.caw.common.model.Level]]
    * to be played after the current one, if an [[it.unibo.pps.caw.common.model.cell.EnemyCell]] has died after the last update
    * and if the current [[it.unibo.pps.caw.common.model.Level]] has been completed or not.
    *
    * @param levelInitialState
    *   the initial state of the [[it.unibo.pps.caw.common.model.Level]] currently being played with respect to the game updates
    * @param levelCurrentState
    *   the current state of the [[it.unibo.pps.caw.common.model.Level]] currently being played after all the game updates already
    *   happened
    * @param currentLevelIndex
    *   the index of the [[it.unibo.pps.caw.common.model.Level]] currently being played
    * @param hasNextLevel
    *   whether or not there is another [[it.unibo.pps.caw.common.model.Level]] to be played after the current one
    * @param didEnemyDie
    *   whether or not an [[it.unibo.pps.caw.common.model.cell.EnemyCell]] has died after the last update
    * @param isCurrentLevelCompleted
    *   whether or not the current [[it.unibo.pps.caw.common.model.Level]] has been completed
    * @return
    *   a new [[GameState]] instance
    */
  def apply(
    levelInitialState: Level[PlayableCell],
    levelCurrentState: Level[BaseCell],
    currentLevelIndex: Int,
    hasNextLevel: Boolean,
    didEnemyDie: Boolean,
    isCurrentLevelCompleted: Boolean
  ): GameState =
    GameStateImpl(levelInitialState, levelCurrentState, currentLevelIndex, hasNextLevel, didEnemyDie, isCurrentLevelCompleted)

  /** Contains all the extensions methods to the [[GameState]] trait. */
  extension (state: GameState) {

    /** Copy constructor of the [[GameState]] trait which creates a new instance of [[GameState]] copying the already created one
      * and modifying its properties with the given values for the initial and current state of the
      * [[it.unibo.pps.caw.common.model.Level]] currently being played for the game updates, the index of the
      * [[it.unibo.pps.caw.common.model.Level]] currently being played, if there is another
      * [[it.unibo.pps.caw.common.model.Level]] to be played after the current one, if an
      * [[it.unibo.pps.caw.common.model.cell.EnemyCell]] has died after the last update and if the current
      * [[it.unibo.pps.caw.common.model.Level]] has been completed or not.
      *
      * @param levelInitialState
      *   the initial state of the [[it.unibo.pps.caw.common.model.Level]] currently being played with respect to the game updates
      * @param levelCurrentState
      *   the current state of the [[it.unibo.pps.caw.common.model.Level]] currently being played after all the game updates
      *   already happened
      * @param currentLevelIndex
      *   the index of the [[it.unibo.pps.caw.common.model.Level]] currently being played
      * @param hasNextLevel
      *   whether or not there is another [[it.unibo.pps.caw.common.model.Level]] to be played after the current one
      * @param didEnemyDie
      *   whether or not an [[it.unibo.pps.caw.common.model.cell.EnemyCell]] has died after the last update
      * @param isCurrentLevelCompleted
      *   whether or not the current [[it.unibo.pps.caw.common.model.Level]] has been completed
      * @return
      *   a new [[GameState]] instance copied from the given one
      */
    def copy(
      levelInitialState: Level[PlayableCell] = state.levelInitialState,
      levelCurrentState: Level[BaseCell] = state.levelCurrentState,
      currentLevelIndex: Int = state.currentLevelIndex,
      hasNextLevel: Boolean = state.hasNextLevel,
      didEnemyDie: Boolean = state.didEnemyDie,
      isCurrentLevelCompleted: Boolean = state.isCurrentLevelCompleted
    ): GameState =
      GameStateImpl(levelInitialState, levelCurrentState, currentLevelIndex, hasNextLevel, didEnemyDie, isCurrentLevelCompleted)
  }
}
