package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.dsl.entities.{Orientation, Position, Push, Rotation}

sealed trait FollowingInAnAreaWord

sealed trait AtWord extends FollowingInAnAreaWord {
  def at(x: Int, y: Int): Unit
}

object AtWord {
  private class AtWordImpl(fun: Position => Unit) extends AtWord {
    def at(x: Int, y: Int): Unit = fun(Position(x, y))
  }

  def apply(fun: Position => Unit): AtWord = AtWordImpl(fun)
}

sealed trait FacingWord extends FollowingInAnAreaWord {
  def facing(orientation: Orientation): AtWord
}

object FacingWord {
  private class FacingWordImpl(fun: Orientation => AtWord) extends FacingWord {
    def facing(orientation: Orientation): AtWord = fun(orientation)
  }

  def apply(fun: Orientation => AtWord): FacingWord = FacingWordImpl(fun)
}

sealed trait RotatingWord extends FollowingInAnAreaWord {
  def rotating(rotation: Rotation): AtWord
}

object RotatingWord {
  private class RotatingWordImpl(fun: Rotation => AtWord) extends RotatingWord {
    def rotating(rotation: Rotation): AtWord = fun(rotation)
  }

  def apply(fun: Rotation => AtWord): RotatingWord = RotatingWordImpl(fun)
}

trait PushableWord extends FollowingInAnAreaWord {
  def pushable(push: Push): AtWord
}

object PushableWord {
  private class PushableWordImpl(fun: Push => AtWord) extends PushableWord {
    def pushable(push: Push): AtWord = fun(push)
  }

  def apply(fun: Push => AtWord): PushableWord = PushableWordImpl(fun)
}