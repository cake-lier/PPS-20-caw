package it.unibo.pps.caw
package game.model

trait Model(val level: Level) {
  def update(): Model
  def reset(): Model
  def nextLevelIndex(currentIndex: Int): Option[Int]
  val isLevelCompleted: Boolean
}

object Model {

  private class ModelImpl(level: Level) extends Model(level) {
    override def update(): Model = ???

    override def reset(): Model = ???

    override def nextLevelIndex(currentIndex: Int): Option[Int] = Some(currentIndex + 1).filter(_ < 30)

    override val isLevelCompleted: Boolean = false
  }

  def apply(level: Level): Model = ModelImpl(level)
}
