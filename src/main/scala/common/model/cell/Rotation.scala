package it.unibo.pps.caw.common.model.cell

/** A direction of rotation for a given [[Cell]].
  *
  * This enum is used for describing the different directions in which a [[Cell]] can rotate. More specifically, a rotation can
  * only be given to a [[RotatorCell]]. To each type is associated a specific name, suitable for identifying it.
  */
enum Rotation(val name: String) {

  /** The value used for cells that have a clockwise rotation. */
  case Clockwise extends Rotation("clockwise")

  /** The value used for cells that have a counterclockwise rotation. */
  case Counterclockwise extends Rotation("counterclockwise")
}

/** Companion object to the [[Rotation]] trait, containing utility methods. */
object Rotation {

  /** Returns a [[Some]] containing the [[Rotation]] associated to the given name, if it exists. Otherwise, a [[None]] is
    * returned.
    *
    * @param name
    *   the name of the [[Rotation]] to get
    * @return
    *   the [[Rotation]] associated to the given name
    */
  def fromName(name: String): Option[Rotation] = Rotation.values.find(_.name == name)
}
