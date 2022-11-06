package it.unibo.pps.caw
package dsl.validation

import common.model.*
import common.model.cell.BaseCell
import dsl.entities.LevelBuilderState

import cats.data.ValidatedNel
import cats.implicits.given
import cats.syntax.apply

/** Contains the method for validating the correctness of a [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] entity and extract
  * the pieces of information for building a [[it.unibo.pps.caw.common.model.Level]].
  *
  * This object is a module for containing methods which can check the correctness of a
  * [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] entity, so it can be then built into a
  * [[it.unibo.pps.caw.common.model.Level]] entity.
  */
object LevelBuilderStateValidator {

  /* Contains the auxiliary methods for the check operation, the single checks that need to pass for the whole operation to
   * succeed.
   */
  private object Checkers {

    /* The type of the result of a validation method, which returns a ValidationError if an error occurs. */
    private type ValidationResult[A] = ValidatedNel[ValidationError, A]

    /* Checks if the given values are in the given range, using the given error if not. */
    private def checkValuesAreInRange(values: Int*)(range: Range)(error: ValidationError): ValidationResult[Unit] =
      if (values.forall(range.contains)) ().validNel else error.invalidNel

    /* Checks if the given Dimensions are in range for the Level Dimensions values, so between 2 and 30 included. */
    private def checkLevelDimensionsAreInRange(dimensions: Dimensions): ValidationResult[Unit] =
      checkValuesAreInRange(dimensions.width, dimensions.height)(2 to 30)(ValidationError.LevelDimensionsNotInRange)

    /* Checks if the given Dimensions are in range for the PlayableArea Dimensions values, so between 1 and 30 included. */
    private def checkPlayableAreaDimensionsAreInRange(dimensions: Dimensions): ValidationResult[Unit] =
      checkValuesAreInRange(dimensions.width, dimensions.height)(1 to 30)(ValidationError.PlayableAreaDimensionsNotInRange)

    /* Checks if the given Position instance contains coordinates which are in range for the maximum Level Dimensions. */
    private def checkPositionCoordinatesAreInRange(position: Position): ValidationResult[Unit] =
      checkValuesAreInRange(position.x, position.y)(0 until 30)(ValidationError.PositionNotInRange)

    /* Checks whether the given PlayableArea is fully contained into the given Dimensions. */
    private def checkAreaInBounds(bounds: Dimensions, area: PlayableArea): ValidationResult[Unit] =
      if (area.position.x + area.dimensions.width <= bounds.width && area.position.y + area.dimensions.height <= bounds.height)
        ().validNel
      else
        ValidationError.PlayableAreaNotInBounds.invalidNel

    /* Checks if there are no duplicate Position instances between the given Positions. */
    private def checkNonDuplicatePositions(positions: Seq[Position]): ValidationResult[Unit] =
      if (positions.toSet.size == positions.size) ().validNel else ValidationError.SamePositionForDifferentCells.invalidNel

    /* Checks if the given Position is contained into the given Dimensions bounds. */
    private def checkPositionInBounds(position: Position, bounds: Dimensions): ValidationResult[Unit] =
      if (position.x <= bounds.width && position.y <= bounds.height)
        ().validNel
      else
        ValidationError.CellOutsideBounds.invalidNel

    /* Checks whether the given Dimensions are set or not. */
    private def checkSetDimensions(dimensions: Option[Dimensions]): ValidationResult[Dimensions] =
      dimensions.toRight(ValidationError.DimensionsUnset).toValidatedNel

    /* Checks whether the given PlayableArea is set or not. */
    private def checkSetPlayableArea(area: Option[PlayableArea]): ValidationResult[PlayableArea] =
      area.toRight(ValidationError.PlayableAreaUnset).toValidatedNel

    /* Checks whether or not the given board Dimensions are valid. */
    def checkBoardDimensions(dimensions: Option[Dimensions]): ValidationResult[Dimensions] =
      checkSetDimensions(dimensions).andThen(d => checkLevelDimensionsAreInRange(d).map(_ => d))

    /* Checks whether or not the given PlayableArea is valid. */
    def checkPlayableArea(playableArea: Option[PlayableArea])(boardDimensions: Dimensions): ValidationResult[PlayableArea] =
      checkSetPlayableArea(playableArea).andThen(a =>
        checkPlayableAreaDimensionsAreInRange(a.dimensions)
          .product(checkPositionCoordinatesAreInRange(a.position))
          .andThen(_ => checkAreaInBounds(boardDimensions, a))
          .map(_ => a)
      )

    /* Checks whether or not the given cells Positions are valid. */
    def checkCellPositions(positions: Seq[Position])(boardDimensions: Dimensions): ValidationResult[Unit] =
      checkNonDuplicatePositions(positions)
        .product(positions.map(checkPositionCoordinatesAreInRange).sequence_)
        .andThen(_ => positions.map(checkPositionInBounds(_, boardDimensions)).sequence_)
  }

  import Checkers.*

  /** Checks if the data which was stored into the given [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] is valid or not and,
    * if it is, builds a new [[it.unibo.pps.caw.common.model.Level]] using that data. An [[scala.util.Either]] is returned at the
    * end of the check operation: if the operation succeeded, the built level will be contained inside the [[scala.util.Either]].
    * If the check fails, all the [[ValidationError]] encountered while checking will be contained inside the
    * [[scala.util.Either]].
    *
    * @param state
    *   the [[it.unibo.pps.caw.dsl.entities.LevelBuilderState]] to be checked and to be used for building
    * @return
    *   an [[scala.util.Either]] with the built [[it.unibo.pps.caw.common.model.Level]] if the check succeeds or with all the
    *   encountered [[ValidationError]] if the check fails
    */
  def validateBuilderState(state: LevelBuilderState): Either[Seq[ValidationError], Level[BaseCell]] =
    checkBoardDimensions(state.dimensions)
      .andThen(d =>
        (checkPlayableArea(state.playableArea)(d), checkCellPositions(state.cells.map(_.position))(d)).mapN((a, _) => (d, a))
      )
      .map(t => Level(t._1, state.cells.toSet, t._2))
      .leftMap(_.toList)
      .toEither
}
