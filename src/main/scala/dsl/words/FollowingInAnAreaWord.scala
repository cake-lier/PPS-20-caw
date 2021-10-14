package it.unibo.pps.caw.dsl.words

import it.unibo.pps.caw.common.model.Position
import it.unibo.pps.caw.common.model.cell.{Orientation, Push, Rotation}
import it.unibo.pps.caw.dsl.CellsAtWorkDSL.{OrientationWord, PushWord, RotationWord}

/** Used for creating a union type between all words that can follow the [[InAnAreaWord]] in a phrase. */
sealed trait FollowingInAnAreaWord

/** The "at" word, used for specifying the [[Position]] of an entity.
  *
  * This word appears last in a sentence. It must be constructed through its companion object.
  */
sealed trait AtWord extends FollowingInAnAreaWord {

  /** Allows to use the "at" word in a sentence written in the DSL, specifying the position of an entity.
    *
    * @param x
    *   the x coordinate of the entity position
    * @param y
    *   the y coordinate of the entity position
    */
  def at(x: Int, y: Int): Unit
}

/** Companion object of the [[AtWord]] trait, containing its factory method. */
object AtWord {

  /* Default implementation of the AtWord trait. */
  private class AtWordImpl(fun: Position => Unit) extends AtWord {

    /* Calls the given function with the Position constructed through the coordinates given by the user. */
    def at(x: Int, y: Int): Unit = fun((x, y))
  }

  /** Returns a new instance of the [[AtWord]] trait. It needs a function which can consume the [[Position]] that the user
    * specifies through the use of this "at" word.
    *
    * @param fun
    *   the function for consuming the [[Position]] the user specifies through this "at" word
    * @return
    *   a new instance of the [[AtWord]] trait
    */
  def apply(fun: Position => Unit): AtWord = AtWordImpl(fun)
}

/** The "facing" word, used for specifying the [[Orientation]] of an entity.
  *
  * This word must appear before an [[AtWord]]. It must be constructed through its companion object.
  */
sealed trait FacingWord extends FollowingInAnAreaWord {

  /** Allows to use the "facing" word in a sentence written in the DSL, specifying the [[Orientation]] of an entity. It accepts an
    * [[OrientationWord]] instead of an [[Orientation]] so as to provide a more english-like structure to the phrase. It returns
    * the [[AtWord]] to use next for completing the sentence.
    *
    * @param orientationWord
    *   the word specifing the [[Orientation]] of an entity
    * @return
    *   the [[AtWord]] that the user must then use for completing the sentence
    */
  def facing(orientationWord: OrientationWord): AtWord
}

/** Companion object of the [[FacingWord]] trait, containing its factory method. */
object FacingWord {

  /* Default implementation of the FacingWord trait. */
  private class FacingWordImpl(fun: Orientation => AtWord) extends FacingWord {

    /* Calls the given function with the Orientation obtained from the OrientationWord used by the user. */
    def facing(orientationWord: OrientationWord): AtWord = fun(orientationWord.orientation)
  }

  /** Returns a new instance of the [[FacingWord]] trait. It needs a function which can consume the [[Orientation]] that the user
    * specifies through the use of this "facing" word and which can return an [[AtWord]] that the user must then use for
    * completing the sentence.
    *
    * @param fun
    *   the function for consuming the [[Orientation]] the user specifies through this "facing" word and which returns the
    *   [[AtWord]] to use next in the sentence
    * @return
    *   a new instance of the [[FacingWord]] trait
    */
  def apply(fun: Orientation => AtWord): FacingWord = FacingWordImpl(fun)
}

/** The "rotating" word, used for specifying the direction of [[Rotation]] of an entity.
  *
  * This word must appear before an [[AtWord]]. It must be constructed through its companion object.
  */
sealed trait RotatingWord extends FollowingInAnAreaWord {

  /** Allows to use the "rotating" word in a sentence written in the DSL, specifying the direction of [[Rotation]] of an entity.
    * It accepts a [[RotationWord]] instead of a [[Rotation]] so as to provide a more english-like structure to the phrase. It
    * returns the [[AtWord]] to use next for completing the sentence.
    *
    * @param rotationWord
    *   the word specifing the direction of [[Rotation]] of an entity
    * @return
    *   the [[AtWord]] that the user must then use for completing the sentence
    */
  def rotating(rotationWord: RotationWord): AtWord
}

/** Companion object of the [[RotatingWord]] trait, containing its factory method. */
object RotatingWord {

  /* Default implementation of the RotatingWord trait. */
  private class RotatingWordImpl(fun: Rotation => AtWord) extends RotatingWord {

    /* Calls the given function with the Rotation obtained from the RotationWord used by the user. */
    def rotating(rotationWord: RotationWord): AtWord = fun(rotationWord.rotation)
  }

  /** Returns a new instance of the [[RotatingWord]] trait. It needs a function which can consume the [[Rotation]] that the user
    * specifies through the use of this "rotating" word and which can return an [[AtWord]] that the user must then use for
    * completing the sentence.
    *
    * @param fun
    *   the function for consuming the [[Rotation]] the user specifies through this "rotating" word and which returns the
    *   [[AtWord]] to use next in the sentence
    * @return
    *   a new instance of the [[RotatingWord]] trait
    */
  def apply(fun: Rotation => AtWord): RotatingWord = RotatingWordImpl(fun)
}

/** The "pushable" word, used for specifying the [[Push]] direction of an entity.
  *
  * This word must appear before an [[AtWord]]. It must be constructed through its companion object.
  */
sealed trait PushableWord extends FollowingInAnAreaWord {

  /** Allows to use the "pushable" word in a sentence written in the DSL, specifying the [[Push]] direction of an entity. It
    * accepts a [[PushWord]] instead of a [[Push]] so as to provide a more english-like structure to the phrase. It returns the
    * [[AtWord]] to use next for completing the sentence.
    *
    * @param pushWord
    *   the word specifing the [[Push]] direction of an entity
    * @return
    *   the [[AtWord]] that the user must then use for completing the sentence
    */
  def pushable(pushWord: PushWord): AtWord
}

/** Companion object of the [[PushableWord]] trait, containing its factory method. */
object PushableWord {

  /* Default implementation of the PushableWord trait. */
  private class PushableWordImpl(fun: Push => AtWord) extends PushableWord {

    /* Calls the given function with the Push obtained from the PushWord used by the user. */
    def pushable(pushWord: PushWord): AtWord = fun(pushWord.push)
  }

  /** Returns a new instance of the [[PushableWord]] trait. It needs a function which can consume the [[Push]] that the user
    * specifies through the use of this "pushable" word and which can return an [[AtWord]] that the user must then use for
    * completing the sentence.
    *
    * @param fun
    *   the function for consuming the [[Push]] the user specifies through this "pushable" word and which returns the [[AtWord]]
    *   to use next in the sentence
    * @return
    *   a new instance of the [[PushableWord]] trait
    */
  def apply(fun: Push => AtWord): PushableWord = PushableWordImpl(fun)
}
