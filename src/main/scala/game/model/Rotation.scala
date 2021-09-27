package it.unibo.pps.caw.game.model

/** Enum that represent the direction of rotation some cells */
enum Rotation(val name: String) {
  case Clockwise extends Rotation("clockwise")
  case Counterclockwise extends Rotation("counterclockwise")
}

object Rotation {
  
  /** get the [[Option]] of given rotation to [[Rotation]]
    *
    * @param name
    *   the value of [[Rotation]] as string
    */
  def fromName(name: String): Option[Rotation] = Rotation.values.find(_.name == name)
}
