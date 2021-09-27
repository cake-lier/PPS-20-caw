package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.common.Position

/** Represent a [[Cell]] during the update phase */
sealed trait UpdateCell extends Cell {

  /** cell identifier for one update cycle */
  val id: Int

  /** whether the cell has been updated or not */
  val updated: Boolean
}

sealed trait UpdateRotatorCell extends RotatorCell with UpdateCell

object UpdateRotatorCell {
  private case class UpdateRotatorCellImpl(position: Position, rotation: Rotation, id: Int, updated: Boolean)
    extends UpdateRotatorCell

  def apply(position: Position, rotation: Rotation, id: Int, updated: Boolean): UpdateRotatorCell =
    UpdateRotatorCellImpl(position, rotation, id, updated)

  def unapply(cell: UpdateRotatorCell): (Position, Rotation, Int, Boolean) = (cell.position, cell.rotation, cell.id, cell.updated)

  extension (cell: UpdateRotatorCell) {
    def copy(
      position: Position = cell.position,
      rotation: Rotation = cell.rotation,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateRotatorCell = UpdateRotatorCellImpl(position, rotation, id, updated)
  }
}

sealed trait UpdateGeneratorCell extends GeneratorCell with UpdateCell

object UpdateGeneratorCell {
  final case class UpdateGeneratorCellImpl(position: Position, orientation: Orientation, id: Int, updated: Boolean)
    extends UpdateGeneratorCell

  def apply(position: Position, orientation: Orientation, id: Int, updated: Boolean): UpdateGeneratorCell =
    UpdateGeneratorCellImpl(position, orientation, id, updated)

  def unapply(cell: UpdateGeneratorCell): (Position, Orientation, Int, Boolean) =
    (cell.position, cell.orientation, cell.id, cell.updated)

  extension (cell: UpdateGeneratorCell) {
    def copy(
      position: Position = cell.position,
      orientation: Orientation = cell.orientation,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateGeneratorCell = UpdateGeneratorCellImpl(position, orientation, id, updated)
  }
}

sealed trait UpdateEnemyCell extends EnemyCell with UpdateCell

object UpdateEnemyCell {
  private case class UpdateEnemyCellImpl(position: Position, id: Int, updated: Boolean) extends UpdateEnemyCell

  def apply(position: Position, id: Int, updated: Boolean): UpdateEnemyCell =
    UpdateEnemyCellImpl(position, id, updated)

  def unapply(cell: UpdateEnemyCell): (Position, Int, Boolean) = (cell.position, cell.id, cell.updated)

  extension (cell: UpdateEnemyCell) {
    def copy(
      position: Position = cell.position,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateEnemyCell = UpdateEnemyCellImpl(position, id, updated)
  }
}

sealed trait UpdateMoverCell extends MoverCell with UpdateCell

object UpdateMoverCell {
  private case class UpdateMoverCellImpl(position: Position, orientation: Orientation, id: Int, updated: Boolean)
    extends UpdateMoverCell

  def apply(position: Position, orientation: Orientation, id: Int, updated: Boolean): UpdateMoverCell =
    UpdateMoverCellImpl(position, orientation, id, updated)

  def unapply(cell: UpdateMoverCell): (Position, Orientation, Int, Boolean) =
    (cell.position, cell.orientation, cell.id, cell.updated)

  extension (cell: UpdateMoverCell) {
    def copy(
      position: Position = cell.position,
      orientation: Orientation = cell.orientation,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateMoverCell = UpdateMoverCellImpl(position, orientation, id, updated)
  }
}

sealed trait UpdateBlockCell extends BlockCell with UpdateCell

object UpdateBlockCell {
  private case class UpdateBlockCellImpl(position: Position, push: Push, id: Int, updated: Boolean) extends UpdateBlockCell

  def apply(position: Position, push: Push, id: Int, updated: Boolean): UpdateBlockCell =
    UpdateBlockCellImpl(position, push, id, updated)

  def unapply(cell: UpdateBlockCell): (Position, Push, Int, Boolean) = (cell.position, cell.push, cell.id, cell.updated)

  extension (cell: UpdateBlockCell) {
    def copy(
      position: Position = cell.position,
      push: Push = cell.push,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateBlockCell = UpdateBlockCellImpl(position, push, id, updated)
  }
}

sealed trait UpdateWallCell extends WallCell with UpdateCell

object UpdateWallCell {
  private case class UpdateWallCellImpl(position: Position, id: Int, updated: Boolean) extends UpdateWallCell

  def apply(position: Position, id: Int, updated: Boolean): UpdateWallCell = UpdateWallCellImpl(position, id, updated)

  def unapply(cell: UpdateWallCell): (Position, Int, Boolean) = (cell.position, cell.id, cell.updated)

  extension (cell: UpdateWallCell) {
    def copy(
      position: Position = cell.position,
      id: Int = cell.id,
      updated: Boolean = cell.updated
    ): UpdateWallCell = UpdateWallCellImpl(position, id, updated)
  }
}
