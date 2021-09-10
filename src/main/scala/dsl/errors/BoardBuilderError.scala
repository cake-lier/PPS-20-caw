package it.unibo.pps.caw.dsl.errors

enum BoardBuilderError(val message: String) {
  case NegativeDimensions extends BoardBuilderError("The dimensions given to an entity were negativr values")
  case NegativePosition extends BoardBuilderError("The position given to an entity used negative coordinates")
  case PlayableAreaNotInBounds extends BoardBuilderError("The playable area exceeds the level bounds")
  case SamePositionForDifferentCells extends BoardBuilderError("Two or more cells have the same position")
  case CellOutsideBounds extends BoardBuilderError("A cell was place outside the level bounds")
  case DimensionsUnset extends BoardBuilderError("The dimensions were not set")
  case PlayableAreaUnset extends BoardBuilderError("The playable area was not set")
}
