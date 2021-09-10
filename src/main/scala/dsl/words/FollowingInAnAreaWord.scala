package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.dsl.entities.{Orientation, Position, Push, Rotation}
import it.unibo.pps.caw.dsl.CellsAtWorkDSL.{OrientationWord, PushWord, RotationWord}

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
  def facing(orientationWord: OrientationWord): AtWord
}

object FacingWord {
  private class FacingWordImpl(fun: Orientation => AtWord) extends FacingWord {
    def facing(orientationWord: OrientationWord): AtWord = fun(orientationWord.orientation)
  }

  def apply(fun: Orientation => AtWord): FacingWord = FacingWordImpl(fun)
}

sealed trait RotatingWord extends FollowingInAnAreaWord {
  def rotating(rotationWord: RotationWord): AtWord
}

object RotatingWord {
  private class RotatingWordImpl(fun: Rotation => AtWord) extends RotatingWord {
    def rotating(rotationWord: RotationWord): AtWord = fun(rotationWord.rotation)
  }

  def apply(fun: Rotation => AtWord): RotatingWord = RotatingWordImpl(fun)
}

trait PushableWord extends FollowingInAnAreaWord {
  def pushable(pushWord: PushWord): AtWord
}

object PushableWord {
  private class PushableWordImpl(fun: Push => AtWord) extends PushableWord {
    def pushable(pushWord: PushWord): AtWord = fun(pushWord.push)
  }

  def apply(fun: Push => AtWord): PushableWord = PushableWordImpl(fun)
}
