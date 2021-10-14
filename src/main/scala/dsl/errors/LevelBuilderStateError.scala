package it.unibo.pps.caw.dsl.errors

/** An error that can arise while validating of the data inserted in a [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] while
  * building a [[it.unibo.pps.caw.common.model.Level]].
  *
  * Every error is associated with a human-friendly message that can be displayed to the user.
  */
enum LevelBuilderStateError(val message: String) {

  /** The error which arises when an entity has negative [[it.unibo.pps.common.model.Dimensions]]. */
  case NegativeDimensions extends LevelBuilderStateError("The dimensions given to an entity were negative")

  /** The error which arises when an entity has negative [[it.unibo.pps.caw.dsl.entities.Position]] coordinates. */
  case NegativePosition extends LevelBuilderStateError("The position given to an entity has negative coordinates")

  /** The error which arises when the [[it.unibo.pps.caw.dsl.entities.PlayableArea]] is not entirely contained inside the
    * [[it.unibo.pps.caw.common.model.Level]].
    */
  case PlayableAreaNotInBounds extends LevelBuilderStateError("The playable area exceeds the level bounds")

  /** The error which arises when two or more [[it.unibo.pps.caw.dsl.entities.Cell]] state have the same
    * [[it.unibo.pps.caw.dsl.entities.Position]].
    */
  case SamePositionForDifferentCells extends LevelBuilderStateError("Two or more cells have the same position")

  /** The error which arises when it has been given to a [[it.unibo.pps.caw.dsl.entities.Cell]] a
    * [[it.unibo.pps.caw.dsl.entities.Position]] outside the [[it.unibo.pps.caw.common.model.Level]] bounds.
    */
  case CellOutsideBounds extends LevelBuilderStateError("A cell was placed outside the level bounds")

  /** The error which arises when the [[it.unibo.pps.common.model.Dimensions]] of the [[it.unibo.pps.caw.common.model.Level]] have
    * been left unset.
    */
  case DimensionsUnset extends LevelBuilderStateError("The dimensions were not set")

  /** The error which arises when the [[it.unibo.pps.caw.dsl.entities.PlayableArea]] of the
    * [[it.unibo.pps.caw.common.model.Level]] has been left unset.
    */
  case PlayableAreaUnset extends LevelBuilderStateError("The playable area was not set")
}
