package it.unibo.pps.caw
package game.model

trait Model(val level: Level) {
  def update(): Model
  def reset(): Model
  def nextLevelIndex(currentIndex: Int): Option[Int]
}

object Model {

  private class ModelImpl(level: Level) extends Model(level) {
    def update(): Model = ???

    def reset(): Model = ???

    def nextLevelIndex(currentIndex: Int): Option[Int] = Some(currentIndex + 1).filter(_ < 30)
  }

  def apply(level: Level): Model = ModelImpl(level)
}
