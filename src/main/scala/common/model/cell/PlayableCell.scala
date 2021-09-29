package it.unibo.pps.caw.common.model.cell

import it.unibo.pps.caw.common.model.Position

/** A [[Cell]] which has the capability to tell whether or not it is playable and hence movable by the player.
  *
  * A simple [[Cell]] has only properties which are intrinsic to the cell itself or in relation to the entity that contains it,
  * the [[Board]]. No information is given for knowing if the cell can be moved by the player or not, which translates to whether
  * or not it is contained into the [[PlayableArea]] or not. A [[PlayableCell]] extends the properties of a [[Cell]] adding the
  * information for whether or not the cell is playable and hence movable by the player when arranging the [[Level]] before the
  * game starts.
  */
sealed trait PlayableCell extends Cell {

  /** Returns if this [[PlayableCell]] is playable or, equivalently, if it is movable by the player. */
  val playable: Boolean
}

/** A [[RotatorCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableRotatorCell extends RotatorCell with PlayableCell

/** Companion object of the [[PlayableRotatorCell]] trait, containing its utility methods. */
object PlayableRotatorCell {

  /* Default implementation of the PlayableRotatorCell trait. */
  private case class PlayableRotatorCellImpl(position: Position, rotation: Rotation, playable: Boolean)
    extends PlayableRotatorCell

  /** Returns a new instance of the [[PlayableRotatorCell]] trait given its [[Position]], its [[Rotation]] and if it is playable
    * or not. By default, it is not playable unless explicitly stated otherwise.
    *
    * @param position
    *   the [[Position]] of the [[PlayableRotatorCell]] to create
    * @param rotation
    *   the [[Rotation]] of the [[PlayableRotatorCell]] to create
    * @param playable
    *   if the [[PlayableRotatorCell]] is playable or not
    * @return
    *   a new [[PlayableRotatorCell]] instance
    */
  def apply(position: Position, rotation: Rotation, playable: Boolean = false): PlayableRotatorCell =
    PlayableRotatorCellImpl(position, rotation, playable)

  /** Extracts the [[Position]], [[Rotation]] and "playable" properties from the given instance of [[PlayableRotatorCell]].
    *
    * @param cell
    *   the [[PlayableRotatorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], [[Rotation]] and "playable" properties
    */
  def unapply(cell: PlayableRotatorCell): (Position, Rotation, Boolean) = (cell.position, cell.rotation, cell.playable)
}

/** A [[GeneratorCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableGeneratorCell extends GeneratorCell with PlayableCell

/** Companion object of the [[PlayableGeneratorCell]] trait, containing its utility methods. */
object PlayableGeneratorCell {

  /* Default implementation of the PlayableGeneratorCell trait. */
  private case class PlayableGeneratorCellImpl(position: Position, orientation: Orientation, playable: Boolean)
    extends PlayableGeneratorCell

  /** Returns a new instance of the [[PlayableGeneratorCell]] trait given its [[Position]], its [[Orientation]] and if it is
    * playable or not. By default, it is not playable unless explicitly stated otherwise.
    *
    * @param position
    *   the [[Position]] of the [[PlayableGeneratorCell]] to create
    * @param orientation
    *   the [[Orientation]] of the [[PlayableGeneratorCell]] to create
    * @param playable
    *   if the [[PlayableGeneratorCell]] is playable or not
    * @return
    *   a new [[PlayableGeneratorCell]] instance
    */
  def apply(position: Position, orientation: Orientation, playable: Boolean = false): PlayableGeneratorCell =
    PlayableGeneratorCellImpl(position, orientation, playable)

  /** Extracts the [[Position]], [[Orientation]] and "playable" properties from the given instance of [[PlayableGeneratorCell]].
    *
    * @param cell
    *   the [[PlayableGeneratorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], [[Orientation]] and "playable" properties
    */
  def unapply(cell: PlayableGeneratorCell): (Position, Orientation, Boolean) = (cell.position, cell.orientation, cell.playable)
}

/** An [[EnemyCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableEnemyCell extends EnemyCell with PlayableCell

/** Companion object of the [[PlayableEnemyCell]] trait, containing its utility methods. */
object PlayableEnemyCell {

  /* Default implementation of the PlayableEnemyCell trait. */
  private case class PlayableEnemyCellImpl(position: Position, playable: Boolean) extends PlayableEnemyCell

  /** Returns a new instance of the [[PlayableEnemyCell]] trait given its [[Position]] and if it is playable or not. By default,
    * it is not playable unless explicitly stated otherwise.
    *
    * @param position
    *   the [[Position]] of the [[PlayableEnemyCell]] to create
    * @param playable
    *   if the [[PlayableEnemyCell]] is playable or not
    * @return
    *   a new [[PlayableEnemyCell]] instance
    */
  def apply(position: Position, playable: Boolean = false): PlayableEnemyCell = PlayableEnemyCellImpl(position, playable)

  /** Extracts the [[Position]] and "playable" properties from the given instance of [[PlayableEnemyCell]].
    *
    * @param cell
    *   the [[PlayableEnemyCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]] and "playable" properties
    */
  def unapply(cell: PlayableEnemyCell): (Position, Boolean) = (cell.position, cell.playable)
}

/** A [[MoverCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableMoverCell extends MoverCell with PlayableCell

/** Companion object of the [[PlayableMoverCell]] trait, containing its utility methods. */
object PlayableMoverCell {

  /* Default implementation of the PlayableMoverCell trait. */
  private case class PlayableMoverCellImpl(position: Position, orientation: Orientation, playable: Boolean)
    extends PlayableMoverCell

  /** Returns a new instance of the [[PlayableMoverCell]] trait given its [[Position]], its [[Orientation]] and if it is playable
    * or not. By default, it is not playable unless explicitly stated otherwise.
    *
    * @param position
    *   the [[Position]] of the [[PlayableMoverCell]] to create
    * @param orientation
    *   the [[Orientation]] of the [[PlayableMoverCell]] to create
    * @param playable
    *   if the [[PlayableMoverCell]] is playable or not
    * @return
    *   a new [[PlayableMoverCell]] instance
    */
  def apply(position: Position, orientation: Orientation, playable: Boolean = false): PlayableMoverCell =
    PlayableMoverCellImpl(position, orientation, playable)

  /** Extracts the [[Position]], [[Orientation]] and "playable" properties from the given instance of [[PlayableMoverCell]].
    *
    * @param cell
    *   the [[PlayableMoverCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], [[Orientation]] and "playable" properties
    */
  def unapply(cell: PlayableMoverCell): (Position, Orientation, Boolean) = (cell.position, cell.orientation, cell.playable)
}

/** A [[BlockCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableBlockCell extends BlockCell with PlayableCell

/** Companion object of the [[PlayableBlockCell]] trait, containing its utility methods. */
object PlayableBlockCell {

  /* Default implementation of the PlayableBlockCell trait. */
  private case class PlayableBlockCellImpl(position: Position, push: Push, playable: Boolean) extends PlayableBlockCell

  /** Returns a new instance of the [[PlayableBlockCell]] trait given its [[Position]], its [[Push]] direction and if it is
    * playable or not. By default, it is not playable unless explicitly stated otherwise.
    *
    * @param position
    *   the [[Position]] of the [[PlayableBlockCell]] to create
    * @param push
    *   the [[Push]] direction of the [[PlayableBlockCell]] to create
    * @param playable
    *   if the [[PlayableBlockCell]] is playable or not
    * @return
    *   a new [[PlayableBlockCell]] instance
    */
  def apply(position: Position, push: Push, playable: Boolean = false): PlayableBlockCell =
    PlayableBlockCellImpl(position, push, playable)

  /** Extracts the [[Position]], [[Push]] direction and "playable" properties from the given instance of [[PlayableBlockCell]].
    *
    * @param cell
    *   the [[PlayableBlockCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]], [[Push]] direction and "playable" properties
    */
  def unapply(cell: PlayableBlockCell): (Position, Push, Boolean) = (cell.position, cell.push, cell.playable)
}

/** A [[WallCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableWallCell extends WallCell with PlayableCell

/** Companion object of the [[PlayableWallCell]] trait, containing its utility methods. */
object PlayableWallCell {

  /* Default implementation of the PlayableWallCell trait. */
  private case class PlayableWallCellImpl(position: Position, playable: Boolean) extends PlayableWallCell

  /** Returns a new instance of the [[PlayableWallCell]] trait given its [[Position]] and if it is playable or not. By default, it
    * is not playable unless explicitly stated otherwise.
    *
    * @param position
    *   the [[Position]] of the [[PlayableWallCell]] to create
    * @param playable
    *   if the [[PlayableWallCell]] is playable or not
    * @return
    *   a new [[PlayableWallCell]] instance
    */
  def apply(position: Position, playable: Boolean = false): PlayableWallCell = PlayableWallCellImpl(position, playable)

  /** Extracts the [[Position]] and "playable" properties from the given instance of [[PlayableWallCell]].
    *
    * @param cell
    *   the [[PlayableWallCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]] and "playable" properties
    */
  def unapply(cell: PlayableWallCell): (Position, Boolean) = (cell.position, cell.playable)
}
