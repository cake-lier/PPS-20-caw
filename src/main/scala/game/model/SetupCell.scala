package it.unibo.pps.caw.game.model

/** Represent a [[Cell]] during the setup phase, a [[Cell]] could be playable */
sealed trait SetupCell extends Cell {

  /** if the [[Cell]] is movable by the player (is in the [[PlayableArea]] */
  val playable: Boolean
}

sealed trait SetupRotatorCell extends RotatorCell with SetupCell

object SetupRotatorCell {
  private case class SetupRotatorCellImpl(position: Position, rotation: Rotation, playable: Boolean) extends SetupRotatorCell

  def apply(position: Position, rotation: Rotation, playable: Boolean = false): SetupRotatorCell =
    SetupRotatorCellImpl(position, rotation, playable)

  def unapply(cell: SetupRotatorCell): (Position, Rotation, Boolean) = (cell.position, cell.rotation, cell.playable)
}

sealed trait SetupGeneratorCell extends GeneratorCell with SetupCell

object SetupGeneratorCell {
  private case class SetupGeneratorCellImpl(position: Position, orientation: Orientation, playable: Boolean)
    extends SetupGeneratorCell

  def apply(position: Position, orientation: Orientation, playable: Boolean = false): SetupGeneratorCell =
    SetupGeneratorCellImpl(position, orientation, playable)

  def unapply(cell: SetupGeneratorCell): (Position, Orientation, Boolean) = (cell.position, cell.orientation, cell.playable)
}

sealed trait SetupEnemyCell extends EnemyCell with SetupCell

object SetupEnemyCell {
  private case class SetupEnemyCellImpl(position: Position, playable: Boolean) extends SetupEnemyCell

  def apply(position: Position, playable: Boolean = false): SetupEnemyCell = SetupEnemyCellImpl(position, playable)

  def unapply(cell: SetupEnemyCell): (Position, Boolean) = (cell.position, cell.playable)
}

sealed trait SetupMoverCell extends MoverCell with SetupCell

object SetupMoverCell {
  private case class SetupMoverCellImpl(position: Position, orientation: Orientation, playable: Boolean) extends SetupMoverCell

  def apply(position: Position, orientation: Orientation, playable: Boolean = false): SetupMoverCell =
    SetupMoverCellImpl(position, orientation, playable)

  def unapply(cell: SetupMoverCell): (Position, Orientation, Boolean) = (cell.position, cell.orientation, cell.playable)
}

sealed trait SetupBlockCell extends BlockCell with SetupCell

object SetupBlockCell {
  private case class SetupBlockCellImpl(position: Position, push: Push, playable: Boolean) extends SetupBlockCell

  def apply(position: Position, push: Push, playable: Boolean = false): SetupBlockCell =
    SetupBlockCellImpl(position, push, playable)

  def unapply(cell: SetupBlockCell): (Position, Push, Boolean) = (cell.position, cell.push, cell.playable)
}

sealed trait SetupWallCell extends WallCell with SetupCell

object SetupWallCell {
  private case class SetupWallCellImpl(position: Position, playable: Boolean) extends SetupWallCell

  def apply(position: Position, playable: Boolean = false): SetupWallCell = SetupWallCellImpl(position, playable)

  def unapply(cell: SetupWallCell): (Position, Boolean) = (cell.position, cell.playable)
}
