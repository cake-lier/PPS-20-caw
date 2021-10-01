package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.model.Position
import it.unibo.pps.caw.common.model.cell.*

/** A [[Cell]] which is used during an update of the [[it.unibo.pps.caw.common.model.Level]] currently being played.
  *
  * While the [[GameModel]] does an update during the second phase of the game, the information that it needs for correctly
  * performing it about a [[Cell]] are not enough. The update is made in steps, one for each cell that needs to be updated. Hence,
  * the [[GameModel]] needs to remember if a cell has already been updated, but also it needs to trace a unique correlation
  * between a cell before and after updating it, so as to keep intact its properties and remember not to repeat the operation if
  * it has already been done. An [[UpdateCell]] does exactly this job, storing a unique id and the fact if this cell has already
  * been updated or not.
  */
sealed trait UpdateCell extends Cell {

  /** Returns the unique id associated to this [[UpdateCell]]. */
  val id: Int

  /** Returns whether or not this [[UpdateCell]] has already been updated. */
  val updated: Boolean
}

/** A [[RotatorCell]] which is also an [[UpdateCell]]. */
sealed trait UpdateRotatorCell extends RotatorCell with UpdateCell

/** Companion object of the [[UpdateRotatorCell]] trait, containing its utility methods. */
object UpdateRotatorCell {

  /* Default implementation of the UpdateRotatorCell trait. */
  private case class UpdateRotatorCellImpl(position: Position, rotation: Rotation, id: Int, updated: Boolean)
    extends UpdateRotatorCell

  /** Returns a new instance of the [[UpdateRotatorCell]] trait given its [[Position]], its [[Rotation]], its unique id and
    * whether or not it has already been updated.
    *
    * @param position
    *   the [[Position]] of the [[UpdateRotatorCell]] to create
    * @param rotation
    *   the [[Rotation]] of the [[UpdateRotatorCell]] to create
    * @param id
    *   the unique id of the [[UpdateRotatorCell]] to create
    * @param updated
    *   whether or not the [[UpdateRotatorCell]] to create has already been updated
    * @return
    *   a new instance of [[UpdateRotatorCell]]
    */
  def apply(position: Position, rotation: Rotation, id: Int, updated: Boolean): UpdateRotatorCell =
    UpdateRotatorCellImpl(position, rotation, id, updated)

  /** Extracts the [[Position]], [[Rotation]], "id" and "updated" properties from the given instance of [[UpdateRotatorCell]].
    *
    * @param cell
    *   the [[UpdateRotatorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], [[Rotation]], "id" and "updated" properties
    */
  def unapply(cell: UpdateRotatorCell): (Position, Rotation, Int, Boolean) = (cell.position, cell.rotation, cell.id, cell.updated)

  /** Contains the extension methods of the [[UpdateRotatorCell]] trait. */
  extension (cell: UpdateRotatorCell) {

    /** Copy constructor of the [[UpdateRotatorCell]] trait which creates a new instance of [[UpdateRotatorCell]] copying the
      * already created one and modifying its properties with the given values for its [[Position]], its [[Rotation]], its unique
      * id and for whether or not it has already been updated.
      *
      * @param position
      *   the [[Position]] of the [[UpdateRotatorCell]] to create
      * @param rotation
      *   the [[Rotation]] of the [[UpdateRotatorCell]] to create
      * @param id
      *   the unique id of the [[UpdateRotatorCell]] to create
      * @param updated
      *   whether or not the [[UpdateRotatorCell]] to create has already been updated
      * @return
      *   a new [[UpdateRotatorCell]] instance copied from the given one
      */
    def copy(
      position: Position = cell.position,
      rotation: Rotation = cell.rotation,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateRotatorCell = UpdateRotatorCellImpl(position, rotation, id, updated)
  }
}

/** A [[GeneratorCell]] which is also an [[UpdateCell]]. */
sealed trait UpdateGeneratorCell extends GeneratorCell with UpdateCell

/** Companion object to the [[UpdateGeneratorCell]] trait, containing its utility methods. */
object UpdateGeneratorCell {

  /* Default implementation of the UpdateGeneratorCell trait. */
  final case class UpdateGeneratorCellImpl(position: Position, orientation: Orientation, id: Int, updated: Boolean)
    extends UpdateGeneratorCell

  /** Returns a new instance of the [[UpdateGeneratorCell]] trait given its [[Position]], its [[Orientation]], its unique id and
    * whether or not it has already been updated.
    *
    * @param position
    *   the [[Position]] of the [[UpdateGeneratorCell]] to create
    * @param orientation
    *   the [[Rotation]] of the [[UpdateGeneratorCell]] to create
    * @param id
    *   the unique id of the [[UpdateGeneratorCell]] to create
    * @param updated
    *   whether or not the [[UpdateGeneratorCell]] to create has already been updated
    * @return
    *   a new instance of [[UpdateGeneratorCell]]
    */
  def apply(position: Position, orientation: Orientation, id: Int, updated: Boolean): UpdateGeneratorCell =
    UpdateGeneratorCellImpl(position, orientation, id, updated)

  /** Extracts the [[Position]], [[Orientation]], "id" and "updated" properties from the given instance of
    * [[UpdateGeneratorCell]].
    *
    * @param cell
    *   the [[UpdateGeneratorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], [[Orientation]], "id" and "updated" properties
    */
  def unapply(cell: UpdateGeneratorCell): (Position, Orientation, Int, Boolean) =
    (cell.position, cell.orientation, cell.id, cell.updated)

  /** Contains the extension methods of the [[UpdateGeneratorCell]] trait. */
  extension (cell: UpdateGeneratorCell) {

    /** Copy constructor of the [[UpdateGeneratorCell]] trait which creates a new instance of [[UpdateGeneratorCell]] copying the
      * already created one and modifying its properties with the given values for its [[Position]], its [[Orientation]], its
      * unique id and for whether or not it has already been updated.
      *
      * @param position
      *   the [[Position]] of the [[UpdateGeneratorCell]] to create
      * @param orientation
      *   the [[Orientation]] of the [[UpdateGeneratorCell]] to create
      * @param id
      *   the unique id of the [[UpdateGeneratorCell]] to create
      * @param updated
      *   whether or not the [[UpdateGeneratorCell]] to create has already been updated
      * @return
      *   a new [[UpdateGeneratorCell]] instance copied from the given one
      */
    def copy(
      position: Position = cell.position,
      orientation: Orientation = cell.orientation,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateGeneratorCell = UpdateGeneratorCellImpl(position, orientation, id, updated)
  }
}

/** An [[EnemyCell]] which is also an [[UpdateCell]]. */
sealed trait UpdateEnemyCell extends EnemyCell with UpdateCell

/** Companion object to the [[UpdateEnemyCell]] trait, containing its utility methods. */
object UpdateEnemyCell {

  /* Default implementation of the UpdateEnemyCell trait. */
  private case class UpdateEnemyCellImpl(position: Position, id: Int, updated: Boolean) extends UpdateEnemyCell

  /** Returns a new instance of the [[UpdateEnemyCell]] trait given its [[Position]], its unique id and whether or not it has
    * already been updated.
    *
    * @param position
    *   the [[Position]] of the [[UpdateEnemyCell]] to create
    * @param id
    *   the unique id of the [[UpdateEnemyCell]] to create
    * @param updated
    *   whether or not the [[UpdateEnemyCell]] to create has already been updated
    * @return
    *   a new instance of [[UpdateEnemyCell]]
    */
  def apply(position: Position, id: Int, updated: Boolean): UpdateEnemyCell =
    UpdateEnemyCellImpl(position, id, updated)

  /** Extracts the [[Position]], "id" and "updated" properties from the given instance of [[UpdateEnemyCell]].
    *
    * @param cell
    *   the [[UpdateEnemyCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], "id" and "updated" properties
    */
  def unapply(cell: UpdateEnemyCell): (Position, Int, Boolean) = (cell.position, cell.id, cell.updated)

  /** Contains the extension methods of the [[UpdateEnemyCell]] trait. */
  extension (cell: UpdateEnemyCell) {

    /** Copy constructor of the [[UpdateEnemyCell]] trait which creates a new instance of [[UpdateEnemyCell]] copying the already
      * created one and modifying its properties with the given values for its [[Position]], its unique id and for whether or not
      * it has already been updated.
      *
      * @param position
      *   the [[Position]] of the [[UpdateEnemyCell]] to create
      * @param id
      *   the unique id of the [[UpdateEnemyCell]] to create
      * @param updated
      *   whether or not the [[UpdateEnemyCell]] to create has already been updated
      * @return
      *   a new [[UpdateEnemyCell]] instance copied from the given one
      */
    def copy(
      position: Position = cell.position,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateEnemyCell = UpdateEnemyCellImpl(position, id, updated)
  }
}

/** A [[MoverCell]] which is also an [[UpdateCell]]. */
sealed trait UpdateMoverCell extends MoverCell with UpdateCell

/** Companion object to the [[UpdateMoverCell]] trait, containing its utility methods. */
object UpdateMoverCell {

  /* Default implementation of the UpdateMoverCell trait. */
  private case class UpdateMoverCellImpl(position: Position, orientation: Orientation, id: Int, updated: Boolean)
    extends UpdateMoverCell

  /** Returns a new instance of the [[UpdateMoverCell]] trait given its [[Position]], its [[Orientation]], its unique id and
    * whether or not it has already been updated.
    *
    * @param position
    *   the [[Position]] of the [[UpdateMoverCell]] to create
    * @param orientation
    *   the [[Rotation]] of the [[UpdateMoverCell]] to create
    * @param id
    *   the unique id of the [[UpdateMoverCell]] to create
    * @param updated
    *   whether or not the [[UpdateMoverCell]] to create has already been updated
    * @return
    *   a new instance of [[UpdateMoverCell]]
    */
  def apply(position: Position, orientation: Orientation, id: Int, updated: Boolean): UpdateMoverCell =
    UpdateMoverCellImpl(position, orientation, id, updated)

  /** Extracts the [[Position]], [[Orientation]], "id" and "updated" properties from the given instance of [[UpdateMoverCell]].
    *
    * @param cell
    *   the [[UpdateMoverCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], [[Orientation]], "id" and "updated" properties
    */
  def unapply(cell: UpdateMoverCell): (Position, Orientation, Int, Boolean) =
    (cell.position, cell.orientation, cell.id, cell.updated)

  /** Contains the extension methods of the [[UpdateMoverCell]] trait. */
  extension (cell: UpdateMoverCell) {

    /** Copy constructor of the [[UpdateMoverCell]] trait which creates a new instance of [[UpdateMoverCell]] copying the already
      * created one and modifying its properties with the given values for its [[Position]], its [[Orientation]], its unique id
      * and for whether or not it has already been updated.
      *
      * @param position
      *   the [[Position]] of the [[UpdateMoverCell]] to create
      * @param orientation
      *   the [[Orientation]] of the [[UpdateMoverCell]] to create
      * @param id
      *   the unique id of the [[UpdateMoverCell]] to create
      * @param updated
      *   whether or not the [[UpdateMoverCell]] to create has already been updated
      * @return
      *   a new [[UpdateMoverCell]] instance copied from the given one
      */
    def copy(
      position: Position = cell.position,
      orientation: Orientation = cell.orientation,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateMoverCell = UpdateMoverCellImpl(position, orientation, id, updated)
  }
}

/** A [[BlockCell]] which is also an [[UpdateCell]]. */
sealed trait UpdateBlockCell extends BlockCell with UpdateCell

/** Companion object to the [[UpdateBlockCell]] trait, containing its utility methods. */
object UpdateBlockCell {

  /* Default implementation of the UpdateBlockCell trait. */
  private case class UpdateBlockCellImpl(position: Position, push: Push, id: Int, updated: Boolean) extends UpdateBlockCell

  /** Returns a new instance of the [[UpdateBlockCell]] trait given its [[Position]], its [[Push]] direction, its unique id and
    * whether or not it has already been updated.
    *
    * @param position
    *   the [[Position]] of the [[UpdateBlockCell]] to create
    * @param push
    *   the [[Push]] direction of the [[UpdateBlockCell]] to create
    * @param id
    *   the unique id of the [[UpdateBlockCell]] to create
    * @param updated
    *   whether or not the [[UpdateBlockCell]] to create has already been updated
    * @return
    *   a new instance of [[UpdateBlockCell]]
    */
  def apply(position: Position, push: Push, id: Int, updated: Boolean): UpdateBlockCell =
    UpdateBlockCellImpl(position, push, id, updated)

  /** Extracts the [[Position]], [[Push]] direction, "id" and "updated" properties from the given instance of [[UpdateBlockCell]].
    *
    * @param cell
    *   the [[UpdateBlockCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], [[Push]] direction, "id" and "updated" properties
    */
  def unapply(cell: UpdateBlockCell): (Position, Push, Int, Boolean) = (cell.position, cell.push, cell.id, cell.updated)

  /** Contains the extension methods of the [[UpdateBlockCell]] trait. */
  extension (cell: UpdateBlockCell) {

    /** Copy constructor of the [[UpdateBlockCell]] trait which creates a new instance of [[UpdateBlockCell]] copying the already
      * created one and modifying its properties with the given values for its [[Position]], its [[Push]] direction, its unique id
      * and for whether or not it has already been updated.
      *
      * @param position
      *   the [[Position]] of the [[UpdateBlockCell]] to create
      * @param push
      *   the [[Push]] direction of the [[UpdateBlockCell]] to create
      * @param id
      *   the unique id of the [[UpdateBlockCell]] to create
      * @param updated
      *   whether or not the [[UpdateBlockCell]] to create has already been updated
      * @return
      *   a new [[UpdateBlockCell]] instance copied from the given one
      */
    def copy(
      position: Position = cell.position,
      push: Push = cell.push,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateBlockCell = UpdateBlockCellImpl(position, push, id, updated)
  }
}

/** A [[WallCell]] which is also an [[UpdateCell]]. */
sealed trait UpdateWallCell extends WallCell with UpdateCell

/** Companion object to the [[UpdateWallCell]] trait, containing its utility methods. */
object UpdateWallCell {

  /* Default implementation of the UpdateWallCell trait. */
  private case class UpdateWallCellImpl(position: Position, id: Int, updated: Boolean) extends UpdateWallCell

  /** Returns a new instance of the [[UpdateWallCell]] trait given its [[Position]], its unique id and whether or not it has
    * already been updated.
    *
    * @param position
    *   the [[Position]] of the [[UpdateWallCell]] to create
    * @param id
    *   the unique id of the [[UpdateWallCell]] to create
    * @param updated
    *   whether or not the [[UpdateWallCell]] to create has already been updated
    * @return
    *   a new instance of [[UpdateWallCell]]
    */
  def apply(position: Position, id: Int, updated: Boolean): UpdateWallCell = UpdateWallCellImpl(position, id, updated)

  /** Extracts the [[Position]], "id" and "updated" properties from the given instance of [[UpdateWallCell]].
    *
    * @param cell
    *   the [[UpdateWallCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], "id" and "updated" properties
    */
  def unapply(cell: UpdateWallCell): (Position, Int, Boolean) = (cell.position, cell.id, cell.updated)

  /** Contains the extension methods of the [[UpdateWallCell]] trait. */
  extension (cell: UpdateWallCell) {

    /** Copy constructor of the [[UpdateWallCell]] trait which creates a new instance of [[UpdateWallCell]] copying the already
      * created one and modifying its properties with the given values for its [[Position]], its unique id and for whether or not
      * it has already been updated.
      *
      * @param position
      *   the [[Position]] of the [[UpdateWallCell]] to create
      * @param id
      *   the unique id of the [[UpdateWallCell]] to create
      * @param updated
      *   whether or not the [[UpdateWallCell]] to create has already been updated
      * @return
      *   a new [[UpdateWallCell]] instance copied from the given one
      */
    def copy(
      position: Position = cell.position,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateWallCell = UpdateWallCellImpl(position, id, updated)
  }
}
