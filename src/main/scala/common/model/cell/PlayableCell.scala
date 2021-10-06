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

/** Companion object of the [[PlayableCell]] trait, containing utility methods. */
object PlayableCell {

  /** Contains extension methods for the [[BaseCell]] trait when used with [[PlayableCell]]. */
  extension (cell: BaseCell) {

    /** Converts a [[BaseCell]] to a [[PlayableCell]] given the function for getting the value of the [[PlayableCell.playable]]
      * property given the [[BaseCell]] currently being converted to a [[PlayableCell]].
      *
      * @param isPlayable
      *   the function for getting the value of the [[PlayableCell.playable]] property to assign to the [[BaseCell]] currently
      *   being converted to a [[PlayableCell]]
      * @return
      *   a new [[PlayableCell]] with the same properties of this [[BaseCell]] and the given values for the added properties
      */
    def toPlayableCell(isPlayable: BaseCell => Boolean): PlayableCell = cell match {
      case BaseRotatorCell(r, p)   => PlayableRotatorCell(r)(p)(isPlayable(cell))
      case BaseGeneratorCell(o, p) => PlayableGeneratorCell(o)(p)(isPlayable(cell))
      case BaseEnemyCell(p)        => PlayableEnemyCell(p)(isPlayable(cell))
      case BaseMoverCell(o, p)     => PlayableMoverCell(o)(p)(isPlayable(cell))
      case BaseBlockCell(d, p)     => PlayableBlockCell(d)(p)(isPlayable(cell))
      case BaseWallCell(p)         => PlayableWallCell(p)(isPlayable(cell))
    }
  }

  /** Contains all extension methods for the [[PlayableCell]] trait. */
  extension (cell: PlayableCell) {

    /** Changes the [[PlayableCell.playable]] property of this [[PlayableCell]] to the given value.
      *
      * @param playable
      *   the value to which the [[PlayableCell.playable]] property is to be set
      * @return
      *   this [[PlayableCell]] with its [[PlayableCell.playable]] property set to the given value
      */
    def changePlayableProperty(playable: Boolean): PlayableCell = cell match {
      case PlayableRotatorCell(r, p, _)   => PlayableRotatorCell(r)(p)(playable)
      case PlayableGeneratorCell(o, p, _) => PlayableGeneratorCell(o)(p)(playable)
      case PlayableEnemyCell(p, _)        => PlayableEnemyCell(p)(playable)
      case PlayableMoverCell(o, p, _)     => PlayableMoverCell(o)(p)(playable)
      case PlayableBlockCell(d, p, _)     => PlayableBlockCell(d)(p)(playable)
      case PlayableWallCell(p, _)         => PlayableWallCell(p)(playable)
    }

    /** Changes the [[PlayableCell.position]] property of this [[PlayableCell]] with the result of the given function. The
      * function may depend on the [[PlayableCell]] on which the property is going to be changed.
      *
      * @param getPosition
      *   the function used for getting the [[Position]] to set to this [[PlayableCell]]
      * @return
      *   this [[PlayableCell]] with its [[Position]] changed according to the result of the given function
      */
    def changePositionProperty(getPosition: Position => Position): PlayableCell = cell match {
      case PlayableRotatorCell(r, p, i)   => PlayableRotatorCell(r)(getPosition(p))(i)
      case PlayableGeneratorCell(o, p, i) => PlayableGeneratorCell(o)(getPosition(p))(i)
      case PlayableEnemyCell(p, i)        => PlayableEnemyCell(getPosition(p))(i)
      case PlayableMoverCell(o, p, i)     => PlayableMoverCell(o)(getPosition(p))(i)
      case PlayableBlockCell(d, p, i)     => PlayableBlockCell(d)(getPosition(p))(i)
      case PlayableWallCell(p, i)         => PlayableWallCell(getPosition(p))(i)
    }

    /** Returns this [[PlayableCell]] converted to its equivalent [[BaseCell]]. */
    def toBaseCell: BaseCell = cell match {
      case PlayableRotatorCell(r, p, _)   => BaseRotatorCell(r)(p)
      case PlayableGeneratorCell(o, p, _) => BaseGeneratorCell(o)(p)
      case PlayableEnemyCell(p, _)        => BaseEnemyCell(p)
      case PlayableMoverCell(o, p, _)     => BaseMoverCell(o)(p)
      case PlayableBlockCell(d, p, _)     => BaseBlockCell(d)(p)
      case PlayableWallCell(p, _)         => BaseWallCell(p)
    }
  }
}

/** A [[RotatorCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableRotatorCell extends RotatorCell with PlayableCell

/** Companion object of the [[PlayableRotatorCell]] trait, containing its utility methods. */
object PlayableRotatorCell {

  /* Default implementation of the PlayableRotatorCell trait. */
  private case class PlayableRotatorCellImpl(rotation: Rotation, position: Position, playable: Boolean)
    extends PlayableRotatorCell

  /** Returns a new instance of the [[PlayableRotatorCell]] trait given its [[Rotation]], its [[Position]] and if it is playable
    * or not.
    *
    * @param rotation
    *   the [[Rotation]] of the [[PlayableRotatorCell]] to create
    * @param position
    *   the [[Position]] of the [[PlayableRotatorCell]] to create
    * @param playable
    *   if the [[PlayableRotatorCell]] is playable or not
    * @return
    *   a new [[PlayableRotatorCell]] instance
    */
  def apply(rotation: Rotation)(position: Position)(playable: Boolean): PlayableRotatorCell =
    PlayableRotatorCellImpl(rotation, position, playable)

  /** Extracts the [[Rotation]], [[Position]] and "playable" properties from the given instance of [[PlayableRotatorCell]].
    *
    * @param cell
    *   the [[PlayableRotatorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Rotation]], [[Position]] and "playable" properties
    */
  def unapply(cell: PlayableRotatorCell): (Rotation, Position, Boolean) = (cell.rotation, cell.position, cell.playable)
}

/** A [[GeneratorCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableGeneratorCell extends GeneratorCell with PlayableCell

/** Companion object of the [[PlayableGeneratorCell]] trait, containing its utility methods. */
object PlayableGeneratorCell {

  /* Default implementation of the PlayableGeneratorCell trait. */
  private case class PlayableGeneratorCellImpl(orientation: Orientation, position: Position, playable: Boolean)
    extends PlayableGeneratorCell

  /** Returns a new instance of the [[PlayableGeneratorCell]] trait given its [[Orientation]], its [[Position]] and if it is
    * playable or not.
    *
    * @param orientation
    *   the [[Orientation]] of the [[PlayableGeneratorCell]] to create
    * @param position
    *   the [[Position]] of the [[PlayableGeneratorCell]] to create
    * @param playable
    *   if the [[PlayableGeneratorCell]] is playable or not
    * @return
    *   a new [[PlayableGeneratorCell]] instance
    */
  def apply(orientation: Orientation)(position: Position)(playable: Boolean): PlayableGeneratorCell =
    PlayableGeneratorCellImpl(orientation, position, playable)

  /** Extracts the [[Orientation]], [[Position]] and "playable" properties from the given instance of [[PlayableGeneratorCell]].
    *
    * @param cell
    *   the [[PlayableGeneratorCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Orientation]], [[Position]] and "playable" properties
    */
  def unapply(cell: PlayableGeneratorCell): (Orientation, Position, Boolean) = (cell.orientation, cell.position, cell.playable)
}

/** An [[EnemyCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableEnemyCell extends EnemyCell with PlayableCell

/** Companion object of the [[PlayableEnemyCell]] trait, containing its utility methods. */
object PlayableEnemyCell {

  /* Default implementation of the PlayableEnemyCell trait. */
  private case class PlayableEnemyCellImpl(position: Position, playable: Boolean) extends PlayableEnemyCell

  /** Returns a new instance of the [[PlayableEnemyCell]] trait given its [[Position]] and if it is playable or not.
    *
    * @param position
    *   the [[Position]] of the [[PlayableEnemyCell]] to create
    * @param playable
    *   if the [[PlayableEnemyCell]] is playable or not
    * @return
    *   a new [[PlayableEnemyCell]] instance
    */
  def apply(position: Position)(playable: Boolean): PlayableEnemyCell = PlayableEnemyCellImpl(position, playable)

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
  private case class PlayableMoverCellImpl(orientation: Orientation, position: Position, playable: Boolean)
    extends PlayableMoverCell

  /** Returns a new instance of the [[PlayableMoverCell]] trait given its [[Orientation]], its [[Position]] and if it is playable
    * or not.
    *
    * @param orientation
    *   the [[Orientation]] of the [[PlayableMoverCell]] to create
    * @param position
    *   the [[Position]] of the [[PlayableMoverCell]] to create
    * @param playable
    *   if the [[PlayableMoverCell]] is playable or not
    * @return
    *   a new [[PlayableMoverCell]] instance
    */
  def apply(orientation: Orientation)(position: Position)(playable: Boolean): PlayableMoverCell =
    PlayableMoverCellImpl(orientation, position, playable)

  /** Extracts the [[Orientation]], [[Position]] and "playable" properties from the given instance of [[PlayableMoverCell]].
    *
    * @param cell
    *   the [[PlayableMoverCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Orientation]], [[Position]] and "playable" properties
    */
  def unapply(cell: PlayableMoverCell): (Orientation, Position, Boolean) = (cell.orientation, cell.position, cell.playable)
}

/** A [[BlockCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableBlockCell extends BlockCell with PlayableCell

/** Companion object of the [[PlayableBlockCell]] trait, containing its utility methods. */
object PlayableBlockCell {

  /* Default implementation of the PlayableBlockCell trait. */
  private case class PlayableBlockCellImpl(push: Push, position: Position, playable: Boolean) extends PlayableBlockCell

  /** Returns a new instance of the [[PlayableBlockCell]] trait given its [[Push]] direction, its [[Position]] and if it is
    * playable or not.
    *
    * @param push
    *   the [[Push]] direction of the [[PlayableBlockCell]] to create
    * @param position
    *   the [[Position]] of the [[PlayableBlockCell]] to create
    * @param playable
    *   if the [[PlayableBlockCell]] is playable or not
    * @return
    *   a new [[PlayableBlockCell]] instance
    */
  def apply(push: Push)(position: Position)(playable: Boolean): PlayableBlockCell =
    PlayableBlockCellImpl(push, position, playable)

  /** Extracts the [[Push]] direction, [[Position]] and "playable" properties from the given instance of [[PlayableBlockCell]].
    *
    * @param cell
    *   the [[PlayableBlockCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Push]] direction, [[Position]] and "playable" properties
    */
  def unapply(cell: PlayableBlockCell): (Push, Position, Boolean) = (cell.push, cell.position, cell.playable)
}

/** A [[WallCell]] which is also a [[PlayableCell]]. */
sealed trait PlayableWallCell extends WallCell with PlayableCell

/** Companion object of the [[PlayableWallCell]] trait, containing its utility methods. */
object PlayableWallCell {

  /* Default implementation of the PlayableWallCell trait. */
  private case class PlayableWallCellImpl(position: Position, playable: Boolean) extends PlayableWallCell

  /** Returns a new instance of the [[PlayableWallCell]] trait given its [[Position]] and if it is playable or not.
    *
    * @param position
    *   the [[Position]] of the [[PlayableWallCell]] to create
    * @param playable
    *   if the [[PlayableWallCell]] is playable or not
    * @return
    *   a new [[PlayableWallCell]] instance
    */
  def apply(position: Position)(playable: Boolean): PlayableWallCell = PlayableWallCellImpl(position, playable)

  /** Extracts the [[Position]] and "playable" properties from the given instance of [[PlayableWallCell]].
    *
    * @param cell
    *   the [[PlayableWallCell]] from which extracting the properties
    * @return
    *   a tuple containing the [[Position]] and "playable" properties
    */
  def unapply(cell: PlayableWallCell): (Position, Boolean) = (cell.position, cell.playable)
}
