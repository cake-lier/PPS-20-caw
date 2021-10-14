package it.unibo.pps.caw.common.model.cell

/** An orientation given to a [[Cell]].
  *
  * This enumeration is used for describing the different orientations that can be given to a [[Cell]]. More specifically, an
  * orientation can be given to a [[MoverCell]] and a [[GeneratorCell]]. In the first case, it indicates where the [[Cell]] is
  * headed to, and in the second case, the orientation indicates the direction in which the new cells are spawned. To each type is
  * associated a specific name, suitable for identifying it.
  */
enum Orientation(val name: String) {

  /** The value used for cells with a right orientation. */
  case Right extends Orientation("right")

  /** The value used for cells with a left orientation. */
  case Left extends Orientation("left")

  /** The value used for cells with a down orientation. */
  case Down extends Orientation("down")

  /** The value used for cells with a top orientation. */
  case Top extends Orientation("top")
}

/** Companion object of the [[Orientation]] enum, containing utility methods. */
object Orientation {

  /** Returns a [[Some]] containing the [[Orientation]] associated to the given name, if it exists. Otherwise, a [[None]] is
    * returned.
    *
    * @param name
    *   the name of the [[Orientation]] to get
    * @return
    *   the [[Orientation]] associated to the given name
    */
  def fromName(name: String): Option[Orientation] = Orientation.values.find(_.name == name)
}
