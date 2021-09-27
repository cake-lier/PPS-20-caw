package it.unibo.pps.caw.game.model

trait GameState {

  val initialStateLevel: Level[SetupCell]

  val currentStateLevel: Level[SetupCell]

  val isCurrentLevelCompleted: Boolean

  val nextLevelIndex: Option[Int]

  val didEnemyDie: Boolean
}

object GameState {
  private case class GameStateImpl(
    initialStateLevel: Level[SetupCell],
    currentStateLevel: Level[SetupCell],
    nextLevelIndex: Option[Int],
    didEnemyDie: Boolean,
    isCurrentLevelCompleted: Boolean
  ) extends GameState

  def apply(
    initialStateLevel: Level[SetupCell],
    currentStateLevel: Level[SetupCell],
    nextLevelIndex: Option[Int],
    didEnemyDie: Boolean,
    isCurrentLevelCompleted: Boolean
  ): GameState = GameStateImpl(initialStateLevel, currentStateLevel, nextLevelIndex, didEnemyDie, isCurrentLevelCompleted)

  extension (s: GameState) {
    def copy(
      initialStateLevel: Level[SetupCell] = s.initialStateLevel,
      currentStateLevel: Level[SetupCell] = s.currentStateLevel,
      nextLevelIndex: Option[Int] = s.nextLevelIndex,
      didEnemyDie: Boolean = s.didEnemyDie,
      isCurrentLevelCompleted: Boolean = s.isCurrentLevelCompleted
    ): GameState = GameStateImpl(initialStateLevel, currentStateLevel, nextLevelIndex, didEnemyDie, isCurrentLevelCompleted)
  }
}
