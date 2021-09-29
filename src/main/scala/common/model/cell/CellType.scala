package it.unibo.pps.caw.common.model.cell

/** A type of a [[Cell]] in the game world.
  *
  * This enumeration is used for describing all the different types of cells that exist in the game world. To each type is
  * associated a specific name, suitable for identifying it.
  */
enum CellType(val name: String) {

  /** The enemy cell type, the one associated to the [[EnemyCell]]. */
  case Enemy extends CellType("enemy")

  /** The rotator cell type, the one associated to the [[RotatorCell]]. */
  case Rotator extends CellType("rotator")

  /** The mover cell type, the one associated to the [[MoverCell]]. */
  case Mover extends CellType("mover")

  /** The block cell type, the one associated to the [[BlockCell]]. */
  case Block extends CellType("block")

  /** The wall cell type, the one associated to the [[WallCell]]. */
  case Wall extends CellType("wall")

  /** The generator cell type, the one associated to the [[GeneratorCell]]. */
  case Generator extends CellType("generator")
}

/** Companion object to the [[CellType]] enum, containing utility methods. */
object CellType {

  /** Returns a [[Some]] containing the [[CellType]] associated to the given name, if it exists. Otherwise, a [[None]] is
    * returned.
    *
    * @param name
    *   the name of the [[CellType]] to get
    * @return
    *   the [[CellType]] associated to the given name
    */
  def fromName(name: String): Option[CellType] = CellType.values.find(_.name == name)
}
