package it.unibo.pps.caw
package game.model.engine

import common.model.Position
import common.model.cell.*
import game.model.*

/* A Cell which is used during an update of the it.unibo.pps.caw.common.model.Level currently being played.
 *
 * While the GameModel does an update during the second phase of the game, the information that it needs for correctly
 * performing it about a Cell are not enough. The update is made in steps, one for each cell that needs to be updated. Hence,
 * the GameModel needs to remember if a cell has already been updated, but also it needs to trace a unique correlation
 * between a cell before and after updating it, so as to keep intact its fields and remember not to repeat the operation if
 * it has already been done. An UpdateCell does exactly this job, storing a unique id and the fact if this cell has already
 * been updated or not.
 */
sealed private trait UpdateCell extends Cell {

  /* Returns the unique id associated to this UpdateCell. */
  val id: Int

  /* Returns whether or not this UpdateCell has already been updated. */
  val updated: Boolean
}

/* Companion object of the UpdateCell trait, containing utility methods. */
private object UpdateCell {

  /* Contains extensions methods for the BaseCell trait when used with the UpdateCell trait. */
  extension (cell: BaseCell) {

    /* Converts a BaseCell to an UpdateCell given the values of the id and the updated fields. */
    def toUpdateCell(id: Int, updated: Boolean): UpdateCell = cell match {
      case BaseMoverCell(o, p)     => UpdateMoverCell(p)(o)(id)(false)
      case BaseGeneratorCell(o, p) => UpdateGeneratorCell(p)(o)(id)(updated)
      case BaseRotatorCell(r, p)   => UpdateRotatorCell(p)(r)(id)(updated)
      case BaseBlockCell(d, p)     => UpdateBlockCell(p)(d)(id)(updated)
      case BaseEnemyCell(p)        => UpdateEnemyCell(p)(id)(updated)
      case BaseWallCell(p)         => UpdateWallCell(p)(id)(updated)
      case BaseDeleterCell(p)      => UpdateDeleterCell(p)(id)(updated)
    }
  }

  /* Contains extension methods to the UpdateCell trait. */
  extension (cell: UpdateCell) {

    /* Changes the updated property of this UpdateCell to the given value. */
    def changeUpdatedProperty(updated: Boolean): UpdateCell = cell match {
      case UpdateRotatorCell(p, r, i, _)   => UpdateRotatorCell(p)(r)(i)(updated)
      case UpdateGeneratorCell(p, o, i, _) => UpdateGeneratorCell(p)(o)(i)(updated)
      case UpdateEnemyCell(p, i, _)        => UpdateEnemyCell(p)(i)(updated)
      case UpdateMoverCell(p, o, i, _)     => UpdateMoverCell(p)(o)(i)(updated)
      case UpdateBlockCell(p, d, i, _)     => UpdateBlockCell(p)(d)(i)(updated)
      case UpdateWallCell(p, i, _)         => UpdateWallCell(p)(i)(updated)
      case UpdateDeleterCell(p, i, _)      => UpdateDeleterCell(p)(i)(updated)
    }

    /* Returns this UpdateCell converted to its equivalent BaseCell. */
    def toBaseCell: BaseCell = cell match {
      case UpdateRotatorCell(p, r, _, _)   => BaseRotatorCell(r)(p)
      case UpdateGeneratorCell(p, o, _, _) => BaseGeneratorCell(o)(p)
      case UpdateEnemyCell(p, _, _)        => BaseEnemyCell(p)
      case UpdateMoverCell(p, o, _, _)     => BaseMoverCell(o)(p)
      case UpdateBlockCell(p, d, _, _)     => BaseBlockCell(d)(p)
      case UpdateWallCell(p, _, _)         => BaseWallCell(p)
      case UpdateDeleterCell(p, _, _)      => BaseDeleterCell(p)
    }
  }
}

/* A RotatorCell which is also an UpdateCell. */
sealed private trait UpdateRotatorCell extends RotatorCell with UpdateCell

/* Companion object of the UpdateRotatorCell trait, containing its utility methods. */
private object UpdateRotatorCell {

  /* Default implementation of the UpdateRotatorCell trait. */
  private case class UpdateRotatorCellImpl(position: Position, rotation: Rotation, id: Int, updated: Boolean)
    extends UpdateRotatorCell

  /* Returns a new instance of the UpdateRotatorCell trait given its Position, its Rotation, its unique id and
   * whether or not it has already been updated.
   */
  def apply(position: Position)(rotation: Rotation)(id: Int)(updated: Boolean): UpdateRotatorCell =
    UpdateRotatorCellImpl(position, rotation, id, updated)

  /* Extracts the Position, Rotation, "id" and "updated" fields from the given instance of UpdateRotatorCell. */
  def unapply(cell: UpdateRotatorCell): (Position, Rotation, Int, Boolean) = (cell.position, cell.rotation, cell.id, cell.updated)

  /* Contains the extension methods of the UpdateRotatorCell trait. */
  extension (cell: UpdateRotatorCell) {

    /* Copy constructor of the UpdateRotatorCell trait which creates a new instance of UpdateRotatorCell copying the already
     * created one and modifying its fields with the given values for its Position, its Rotation, its unique id and for whether
     * or not it has already been updated.
     */
    def copy(
      position: Position = cell.position,
      rotation: Rotation = cell.rotation,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateRotatorCell = UpdateRotatorCellImpl(position, rotation, id, updated)
  }
}

/* A GeneratorCell which is also an UpdateCell. */
sealed private trait UpdateGeneratorCell extends GeneratorCell with UpdateCell

/* Companion object to the UpdateGeneratorCell trait, containing its utility methods. */
private object UpdateGeneratorCell {

  /* Default implementation of the UpdateGeneratorCell trait. */
  final case class UpdateGeneratorCellImpl(position: Position, orientation: Orientation, id: Int, updated: Boolean)
    extends UpdateGeneratorCell

  /* Returns a new instance of the UpdateGeneratorCell trait given its Position, its Orientation, its unique id and whether or
   * not it has already been updated.
   */
  def apply(position: Position)(orientation: Orientation)(id: Int)(updated: Boolean): UpdateGeneratorCell =
    UpdateGeneratorCellImpl(position, orientation, id, updated)

  /* Extracts the Position, Orientation, "id" and "updated" fields from the given instance of UpdateGeneratorCell. */
  def unapply(cell: UpdateGeneratorCell): (Position, Orientation, Int, Boolean) =
    (cell.position, cell.orientation, cell.id, cell.updated)

  /* Contains the extension methods of the UpdateGeneratorCell trait. */
  extension (cell: UpdateGeneratorCell) {

    /* Copy constructor of the UpdateGeneratorCell trait which creates a new instance of UpdateGeneratorCell copying the already
     * created one and modifying its fields with the given values for its Position, its Orientation, its unique id and for
     * whether or not it has already been updated.
     */
    def copy(
      position: Position = cell.position,
      orientation: Orientation = cell.orientation,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateGeneratorCell = UpdateGeneratorCellImpl(position, orientation, id, updated)
  }
}

/* An EnemyCell which is also an UpdateCell. */
sealed private trait UpdateEnemyCell extends EnemyCell with UpdateCell

/* Companion object to the UpdateEnemyCell trait, containing its utility methods. */
private object UpdateEnemyCell {

  /* Default implementation of the UpdateEnemyCell trait. */
  private case class UpdateEnemyCellImpl(position: Position, id: Int, updated: Boolean) extends UpdateEnemyCell

  /* Returns a new instance of the UpdateEnemyCell trait given its Position, its unique id and whether or not it has already been
   * updated.
   */
  def apply(position: Position)(id: Int)(updated: Boolean): UpdateEnemyCell =
    UpdateEnemyCellImpl(position, id, updated)

  /* Extracts the Position, "id" and "updated" fields from the given instance of UpdateEnemyCell. */
  def unapply(cell: UpdateEnemyCell): (Position, Int, Boolean) = (cell.position, cell.id, cell.updated)

  /* Contains the extension methods of the UpdateEnemyCell trait. */
  extension (cell: UpdateEnemyCell) {

    /* Copy constructor of the UpdateEnemyCell trait which creates a new instance of UpdateEnemyCell copying the already created
     * one and modifying its fields with the given values for its Position, its unique id and for whether or not it has already
     * been updated.
     */
    def copy(
      position: Position = cell.position,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateEnemyCell = UpdateEnemyCellImpl(position, id, updated)
  }
}

/* A MoverCell which is also an UpdateCell. */
sealed private trait UpdateMoverCell extends MoverCell with UpdateCell

/* Companion object to the UpdateMoverCell trait, containing its utility methods. */
private object UpdateMoverCell {

  /* Default implementation of the UpdateMoverCell trait. */
  private case class UpdateMoverCellImpl(position: Position, orientation: Orientation, id: Int, updated: Boolean)
    extends UpdateMoverCell

  /* Returns a new instance of the UpdateMoverCell trait given its Position, its Orientation, its unique id and whether or not it
   * has already been updated.
   */
  def apply(position: Position)(orientation: Orientation)(id: Int)(updated: Boolean): UpdateMoverCell =
    UpdateMoverCellImpl(position, orientation, id, updated)

  /* Extracts the Position, Orientation, "id" and "updated" fields from the given instance of UpdateMoverCell. */
  def unapply(cell: UpdateMoverCell): (Position, Orientation, Int, Boolean) =
    (cell.position, cell.orientation, cell.id, cell.updated)

  /* Contains the extension methods of the UpdateMoverCell trait. */
  extension (cell: UpdateMoverCell) {

    /* Copy constructor of the UpdateMoverCell trait which creates a new instance of UpdateMoverCell copying the already created
     * one and modifying its fields with the given values for its Position, its Orientation, its unique id and for whether or not
     * it has already been updated.
     */
    def copy(
      position: Position = cell.position,
      orientation: Orientation = cell.orientation,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateMoverCell = UpdateMoverCellImpl(position, orientation, id, updated)
  }
}

/* A BlockCell which is also an UpdateCell. */
sealed private trait UpdateBlockCell extends BlockCell with UpdateCell

/* Companion object to the UpdateBlockCell trait, containing its utility methods. */
private object UpdateBlockCell {

  /* Default implementation of the UpdateBlockCell trait. */
  private case class UpdateBlockCellImpl(position: Position, push: Push, id: Int, updated: Boolean) extends UpdateBlockCell

  /* Returns a new instance of the UpdateBlockCell trait given its Position, its Push direction, its unique id and whether or not
   * it has already been updated.
   */
  def apply(position: Position)(push: Push)(id: Int)(updated: Boolean): UpdateBlockCell =
    UpdateBlockCellImpl(position, push, id, updated)

  /* Extracts the Position, Push direction, "id" and "updated" fields from the given instance of UpdateBlockCell. */
  def unapply(cell: UpdateBlockCell): (Position, Push, Int, Boolean) = (cell.position, cell.push, cell.id, cell.updated)

  /* Contains the extension methods of the UpdateBlockCell trait. */
  extension (cell: UpdateBlockCell) {

    /* Copy constructor of the UpdateBlockCell trait which creates a new instance of UpdateBlockCell copying the already created
     * one and modifying its fields with the given values for its Position, its Push direction, its unique id and for whether or
     * not it has already been updated.
     */
    def copy(
      position: Position = cell.position,
      push: Push = cell.push,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateBlockCell = UpdateBlockCellImpl(position, push, id, updated)
  }
}

/* A WallCell which is also an UpdateCell. */
sealed private trait UpdateWallCell extends WallCell with UpdateCell

/* Companion object to the UpdateWallCell trait, containing its utility methods. */
private object UpdateWallCell {

  /* Default implementation of the UpdateWallCell trait. */
  private case class UpdateWallCellImpl(position: Position, id: Int, updated: Boolean) extends UpdateWallCell

  /* Returns a new instance of the UpdateWallCell trait given its Position, its unique id and whether or not it has already been
   * updated.
   */
  def apply(position: Position)(id: Int)(updated: Boolean): UpdateWallCell = UpdateWallCellImpl(position, id, updated)

  /* Extracts the Position, "id" and "updated" fields from the given instance of UpdateWallCell. */
  def unapply(cell: UpdateWallCell): (Position, Int, Boolean) = (cell.position, cell.id, cell.updated)

  /* Contains the extension methods of the UpdateWallCell trait. */
  extension (cell: UpdateWallCell) {

    /* Copy constructor of the UpdateWallCell trait which creates a new instance of UpdateWallCell copying the already created
     * one and modifying its fields with the given values for its Position, its unique id and for whether or not it has already
     * been updated.
     */
    def copy(
      position: Position = cell.position,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateWallCell = UpdateWallCellImpl(position, id, updated)
  }
}

/* A DeleterCell which is also an UpdateCell. */
sealed private trait UpdateDeleterCell extends WallCell with UpdateCell

/* Companion object to the UpdateDeleterCell trait, containing its utility methods. */
private object UpdateDeleterCell {

  /* Default implementation of the UpdateWallCell trait. */
  private case class UpdateDeleterCellImpl(position: Position, id: Int, updated: Boolean) extends UpdateDeleterCell

  /* Returns a new instance of the UpdateDeleterCell trait given its Position, its unique id and whether or not it has already
   * been updated.
   */
  def apply(position: Position)(id: Int)(updated: Boolean): UpdateDeleterCell = UpdateDeleterCellImpl(position, id, updated)

  /* Extracts the Position, "id" and "updated" fields from the given instance of UpdateDeleterCell. */
  def unapply(cell: UpdateDeleterCell): (Position, Int, Boolean) = (cell.position, cell.id, cell.updated)

  /* Contains the extension methods of the UpdateDeleterCell trait. */
  extension (cell: UpdateDeleterCell) {

    /* Copy constructor of the UpdateDeleterCell trait which creates a new instance of UpdateDeleterCell copying the already
     * created one and modifying its fields with the given values for its Position, its unique id and for whether or not it has
     * already been updated.
     */
    def copy(
      position: Position = cell.position,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateDeleterCell = UpdateDeleterCellImpl(position, id, updated)
  }
}
