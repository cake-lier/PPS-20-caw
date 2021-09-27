package it.unibo.pps.caw.game.model

/** Enum that represent all [[Cell]] types */
enum CellType(val name: String) {
  case Enemy extends CellType("enemy")
  case Rotator extends CellType("rotator")
  case Mover extends CellType("mover")
  case Block extends CellType("block")
  case Empty extends CellType("empty")
  case Wall extends CellType("wall")
  case Generator extends CellType("generator")
}

object CellType {
  
  /** get the [[Option]] of given type to [[CellType]]
    *
    * @param name
    * the value of [[CellType]] as string
    */
  def fromName(name: String): Option[CellType] = CellType.values.find(_.name == name)
}
