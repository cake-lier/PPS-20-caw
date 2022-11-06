package it.unibo.pps.caw
package common.model.cell

/** A direction in which a [[Cell]] can be pushed.
  *
  * This enumeration is used for describing the different types of direction in which a [[Cell]] can be pushed when subjected to a
  * force exerted by a [[MoverCell]] or a [[GeneratorCell]] directly or indirectly through a column of other cells that are being
  * pushed. More specifically, the [[Push]] direction can be given only to a [[BlockCell]]. To each type is associated a specific
  * name, suitable for identifying it.
  */
enum Push(val name: String) {

  /** The value used for cells that can be pushed horizontally. */
  case Horizontal extends Push("horizontal")

  /** The value used for cells that can be pushed vertically. */
  case Vertical extends Push("vertical")

  /** The value used for cells that can be pushed in both directions, vertical and horizontal. */
  case Both extends Push("both")
}

/** Companion object to the [[Push]] enum, containing utility methods. */
object Push {

  /** Returns a [[Some]] containing the [[Push]] value associated to the given name, if it exists. Otherwise, a [[None]] is
    * returned.
    *
    * @param name
    *   the name of the [[Push]] value to get
    * @return
    *   the [[Push]] value associated to the given name
    */
  def fromName(name: String): Option[Push] = Push.values.find(_.name == name)
}
