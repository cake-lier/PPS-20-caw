package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.common.model.cell.{Orientation, Push, Rotation}

/* Contains all words for specifying properties of Cell.
 *
 * These properties are the Orientation, the Rotation and the Push, which must be preceded by the words that can set
 * those properties to the cells.
 */
private trait PropertiesWord {

  /** A word for specifying the [[it.unibo.pps.caw.common.model.cell.Orientation]] of a
    * [[it.unibo.pps.caw.common.model.cell.MoverCell]] or a [[it.unibo.pps.caw.common.model.cell.GeneratorCell]].
    *
    * Each word is associated to the corresponding [[it.unibo.pps.caw.common.model.cell.Orientation]] to which this word is
    * related.
    */
  sealed trait OrientationWord(val orientation: Orientation)

  /** The "left" word which specifies the [[it.unibo.pps.caw.common.model.cell.Orientation.Left]]. */
  case object left extends OrientationWord(Orientation.Left)

  /** The "right" word which specifies the [[it.unibo.pps.caw.common.model.cell.Orientation.Right]]. */
  case object right extends OrientationWord(Orientation.Right)

  /** The "top" word which specifies the [[it.unibo.pps.caw.common.model.cell.Orientation.Top]]. */
  case object top extends OrientationWord(Orientation.Top)

  /** The "down" word which specifies the [[it.unibo.pps.caw.common.model.cell.Orientation.Down]]. */
  case object down extends OrientationWord(Orientation.Down)

  /** A word for specifying the [[it.unibo.pps.caw.common.model.cell.Rotation]] of a
    * [[it.unibo.pps.caw.common.model.cell.RotatorCell]].
    *
    * Each word is associated to the corresponding [[it.unibo.pps.caw.common.model.cell.Rotation]] to which this word is related.
    */
  sealed trait RotationWord(val rotation: Rotation)

  /** The "clockwise" word which specifies the [[it.unibo.pps.caw.common.model.cell.Rotation.Clockwise]]. */
  case object clockwise extends RotationWord(Rotation.Clockwise)

  /** The "counterclockwise" word which specifies the [[it.unibo.pps.caw.common.model.cell.Rotation.Counterclockwise]]. */
  case object counterclockwise extends RotationWord(Rotation.Counterclockwise)

  /** A word for specifying the [[it.unibo.pps.caw.common.model.cell.Push]] of a [[it.unibo.pps.caw.common.model.cell.BlockCell]].
    *
    * Each word is associated to the corresponding [[it.unibo.pps.caw.common.model.cell.Push]] to which this word is related.
    */
  sealed trait PushWord(val push: Push)

  /** The "vertically" word which specifies the [[it.unibo.pps.caw.common.model.cell.Push.Vertical]]. */
  case object vertically extends PushWord(Push.Vertical)

  /** The "horizontally" word which specifies the [[it.unibo.pps.caw.common.model.cell.Push.Horizontal]]. */
  case object horizontally extends PushWord(Push.Horizontal)

  /** The "inBothDirection" word which specifies the [[it.unibo.pps.caw.common.model.cell.Push.Both]]. */
  case object inBothDirections extends PushWord(Push.Both)
}
