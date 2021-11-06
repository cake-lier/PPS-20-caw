package it.unibo.pps.caw.dsl.validation

/* An error that can arise while validating of the data inserted in a LevelBuilderState while
 * building a Level.
 *
 * Every error is associated with a human-friendly message that can be displayed to the user.
 */
private enum ValidationError(val message: String) {

  /* The error which arises when the Level has Dimensions which are too big or too small. */
  case LevelDimensionsNotInRange
    extends ValidationError(
      "The chosen dimensions for the level are either too big or to small, so not in range between 2 and 30 included"
    )

  /* The error which arises when the PlayableArea has Dimensions which are too big or too small. */
  case PlayableAreaDimensionsNotInRange
    extends ValidationError(
      "The chosen dimensions for the playable area are either too big or to small, so not in range between 1 and 30 included"
    )

  /* The error which arises when an entity has Position coordinates which are not in a valid range. */
  case PositionNotInRange
    extends ValidationError(
      "The position given to an entity has coordinates which are not in a valid range, so between 0 and 29 included"
    )

  /* The error which arises when the PlayableArea is not entirely contained inside the Level. */
  case PlayableAreaNotInBounds extends ValidationError("The playable area exceeds the level bounds")

  /* The error which arises when two or more Cell state have the same Position. */
  case SamePositionForDifferentCells extends ValidationError("Two or more cells have the same position")

  /* The error which arises when it has been given to a Cell a Position outside the Level bounds. */
  case CellOutsideBounds extends ValidationError("A cell was placed outside the level bounds")

  /* The error which arises when the Dimensions of the Level have been left unset. */
  case DimensionsUnset extends ValidationError("The dimensions were not set")

  /* The error which arises when the PlayableArea of the Level has been left unset. */
  case PlayableAreaUnset extends ValidationError("The playable area was not set")
}
