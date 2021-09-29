package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.PlayableCell

trait GameState {

  val initialStateLevel: Level[PlayableCell]

  val currentStateLevel: Level[PlayableCell]

  val isCurrentLevelCompleted: Boolean

  val nextLevelIndex: Option[Int]

  val didEnemyDie: Boolean
}

object GameState {
  private case class GameStateImpl(
                                    initialStateLevel: Level[PlayableCell],
                                    currentStateLevel: Level[PlayableCell],
                                    nextLevelIndex: Option[Int],
                                    didEnemyDie: Boolean,
                                    isCurrentLevelCompleted: Boolean
  ) extends GameState

  def apply(
             initialStateLevel: Level[PlayableCell],
             currentStateLevel: Level[PlayableCell],
             nextLevelIndex: Option[Int],
             didEnemyDie: Boolean,
             isCurrentLevelCompleted: Boolean
  ): GameState = GameStateImpl(initialStateLevel, currentStateLevel, nextLevelIndex, didEnemyDie, isCurrentLevelCompleted)

  extension (s: GameState) {
    def copy(
              initialStateLevel: Level[PlayableCell] = s.initialStateLevel,
              currentStateLevel: Level[PlayableCell] = s.currentStateLevel,
              nextLevelIndex: Option[Int] = s.nextLevelIndex,
              didEnemyDie: Boolean = s.didEnemyDie,
              isCurrentLevelCompleted: Boolean = s.isCurrentLevelCompleted
    ): GameState = GameStateImpl(initialStateLevel, currentStateLevel, nextLevelIndex, didEnemyDie, isCurrentLevelCompleted)
  }
}
