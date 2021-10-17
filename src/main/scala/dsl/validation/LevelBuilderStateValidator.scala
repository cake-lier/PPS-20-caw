package it.unibo.pps.caw.dsl.validation

import it.unibo.pps.caw.dsl.entities.*
import cats.data.ValidatedNel
import cats.implicits.given
import cats.syntax.apply
import it.unibo.pps.caw.common.model.{Dimensions, Level, PlayableArea, Position}
import it.unibo.pps.caw.common.model.cell.BaseCell

/** Contains the method for validating the correctness of a [[LevelBuilderState]] entity and extract the informations for building
  * a [[Level]].
  *
  * This object is a module for containing methods which can check the correctness of a [[LevelBuilderState]] entity, so it can be
  * then built into a [[Level]] entity.
  */
object LevelBuilderStateValidator {

  /* Contains the auxiliary methods for the check operation, the single checks that need to pass for the whole operation to
   * succeed.
   */
  private object Checkers {

    /* Checks if a Dimensions instance contains only positive values. */
    private def checkNonNegativeDimensions(dimensions: Dimensions): ValidatedNel[ValidationError, Unit] =
      if (dimensions.width >= 0 && dimensions.height >= 0) ().validNel else ValidationError.NegativeDimensions.invalidNel

    /* Checks if a Position instance contains only positive values. */
    private def checkNonNegativePosition(position: Position): ValidatedNel[ValidationError, Unit] =
      if (position.x >= 0 && position.y >= 0) ().validNel else ValidationError.NegativePosition.invalidNel

    /* Checks whether the given PlayableArea is fully contained into the given Dimensions. */
    private def checkAreaInBounds(bounds: Dimensions, area: PlayableArea): ValidatedNel[ValidationError, Unit] =
      if (area.position.x + area.dimensions.width <= bounds.width && area.position.y + area.dimensions.height <= bounds.height)
        ().validNel
      else
        ValidationError.PlayableAreaNotInBounds.invalidNel

    /* Checks if there are no duplicate Position instances between the given Positions. */
    private def checkNonDuplicatePositions(positions: Seq[Position]): ValidatedNel[ValidationError, Unit] = {
      if (positions.toSet.size == positions.size) ().validNel else ValidationError.SamePositionForDifferentCells.invalidNel
    }

    /* Checks if the given Position is contained into the given Dimensions bounds. */
    private def checkPositionInBounds(position: Position, bounds: Dimensions): ValidatedNel[ValidationError, Unit] =
      if (position.x <= bounds.width && position.y <= bounds.height)
        ().validNel
      else
        ValidationError.CellOutsideBounds.invalidNel

    /* Checks whether the given Dimensions are set or not. */
    private def checkSetDimensions(dimensions: Option[Dimensions]): ValidatedNel[ValidationError, Dimensions] =
      dimensions.toRight(ValidationError.DimensionsUnset).toValidatedNel

    /* Checks whether the given PlayableArea is set or not. */
    private def checkSetPlayableArea(area: Option[PlayableArea]): ValidatedNel[ValidationError, PlayableArea] =
      area.toRight(ValidationError.PlayableAreaUnset).toValidatedNel

    /* Checks whether or not the given board Dimensions are valid. */
    def checkBoardDimensions(dimensions: Option[Dimensions]): ValidatedNel[ValidationError, Dimensions] =
      checkSetDimensions(dimensions).andThen(d => checkNonNegativeDimensions(d).map(_ => d))

    /* Checks whether or not the given PlayableArea is valid. */
    def checkPlayableArea(
      playableArea: Option[PlayableArea]
    )(
      boardDimensions: Dimensions
    ): ValidatedNel[ValidationError, PlayableArea] =
      checkSetPlayableArea(playableArea).andThen(a =>
        checkNonNegativeDimensions(a.dimensions)
          .product(checkNonNegativePosition(a.position))
          .andThen(_ => checkAreaInBounds(boardDimensions, a))
          .map(_ => a)
      )

    /* Checks whether or not the given cells Positions are valid. */
    def checkCellPositions(positions: Seq[Position])(boardDimensions: Dimensions): ValidatedNel[ValidationError, Unit] =
      checkNonDuplicatePositions(positions)
        .product(positions.map(checkNonNegativePosition(_)).sequence_)
        .andThen(_ => positions.map(checkPositionInBounds(_, boardDimensions)).sequence_)

  }

  import Checkers.*

  /** Checks if the data which was stored into the given [[LevelBuilderState]] is valid or not and, if it is, builds a new
    * [[Level]] using that data. An [[scala.util.Either]] is returned at the end of the check operation: if the operation
    * succeeded, the built [[Level]] will be contained inside the [[scala.util.Either]]. If the check fails, all the
    * [[ValidationError]] encountered while checking will be contained inside the [[scala.util.Either]].
    *
    * @param state
    *   the [[LevelBuilderState]] to be checked and to be used for building
    * @return
    *   an [[scala.util.Either]] with the built [[Level]] if the check succeedes or with all the encountered [[ValidationError]]
    *   if the check fails
    */
  def validateBuilderState(state: LevelBuilderState): Either[Seq[ValidationError], Level[BaseCell]] =
    checkBoardDimensions(state.dimensions)
      .andThen(d =>
        (
          checkPlayableArea(state.playableArea)(d),
          checkCellPositions(
            Seq(
              state.moverCells.map(_.position),
              state.generatorCells.map(_.position),
              state.rotatorCells.map(_.position),
              state.blockCells.map(_.position),
              state.enemyCells.map(_.position),
              state.wallCells.map(_.position),
              state.deleterCells.map(_.position)
            ).flatten
          )(
            d
          )
        ).mapN((a, _) => (d, a))
      )
      .map(t =>
        Level(
          t._1,
          Set(
            state.moverCells,
            state.generatorCells,
            state.rotatorCells,
            state.blockCells,
            state.enemyCells,
            state.wallCells,
            state.deleterCells
          ).flatten,
          t._2
        )
      )
      .leftMap(_.toList)
      .toEither
}
