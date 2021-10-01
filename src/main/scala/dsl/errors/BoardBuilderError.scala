package it.unibo.pps.caw.dsl.errors

/** An error that can arise while checking for the correctness of the data inserted in a
  * [[it.unibo.pps.caw.dsl.entities.BoardBuilder]] while building a [[it.unibo.pps.caw.dsl.entities.Board]].
  *
  * Every error is associated with a human-friendly message that can be displayed to the user.
  */
enum BoardBuilderError(val message: String) {

  /** The error which arises when an entity has negative [[it.unibo.pps.caw.dsl.entities.Dimensions]]. */
  case NegativeDimensions extends BoardBuilderError("The dimensions given to an entity were negative")

  /** The error which arises when an entity has negative [[it.unibo.pps.caw.dsl.entities.Position]] coordinates. */
  case NegativePosition extends BoardBuilderError("The position given to an entity has negative coordinates")

  /** The error which arises when the [[it.unibo.pps.caw.dsl.entities.PlayableArea]] is not entirely contained inside the
    * [[it .unibo.pps.caw.dsl.entities.Board]].
    */
  case PlayableAreaNotInBounds extends BoardBuilderError("The playable area exceeds the level bounds")

  /** The error which arises when two or more [[it.unibo.pps.caw.dsl.entities.Cell]] state have the same
    * [[it.unibo.pps.caw.dsl .entities.Position]].
    */
  case SamePositionForDifferentCells extends BoardBuilderError("Two or more cells have the same position")

  /** The error which arises when it has been given to a [[it.unibo.pps.caw.dsl.entities.Cell]] a
    * [[it.unibo.pps.caw.dsl.entities.Position]] outside the [[it.unibo.pps.caw.dsl.entities.Board]] bounds.
    */
  case CellOutsideBounds extends BoardBuilderError("A cell was placed outside the level bounds")

  /** The error which arises when the [[it.unibo.pps.caw.dsl.entities.Dimensions]] of the [[it.unibo.pps.caw.dsl.entities.Board]]
    * have been left unset.
    */
  case DimensionsUnset extends BoardBuilderError("The dimensions were not set")

  /** The error which arises when the [[it.unibo.pps.caw.dsl.entities.PlayableArea]] of the
    * [[it.unibo.pps.caw.dsl.entities.Board]] has been left unset.
    */
  case PlayableAreaUnset extends BoardBuilderError("The playable area was not set")
}
