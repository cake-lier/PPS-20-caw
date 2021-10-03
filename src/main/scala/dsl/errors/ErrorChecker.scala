package it.unibo.pps.caw.dsl.errors

import it.unibo.pps.caw.dsl.entities.*
import cats.data.ValidatedNel
import cats.implicits.given
import cats.syntax.apply
import it.unibo.pps.caw.common.model.{Dimensions, Level, PlayableArea, Position}
import it.unibo.pps.caw.common.model.cell.BaseCell

/** Contains the method for checking the correctness of a [[BoardBuilder]] entity.
  *
  * This object is a module for containing methods which can check the correctness of a [[BoardBuilder]] entity, so it can be then
  * built into a [[Board]] entity. The only method exposed by this module is the [[ErrorChecker.checkBuilderData]] one, which can
  * perform the whole checking operation.
  */
object ErrorChecker {

  /* Contains the auxiliary methods for the check operation, the single checks that need to pass for the whole operation to
   * succeed.
   */
  private object Checkers {

    /* Checks if a Dimensions instance contains only positive values. */
    private def checkNonNegativeDimensions(dimensions: Dimensions): ValidatedNel[BoardBuilderError, Unit] =
      if (dimensions.width >= 0 && dimensions.height >= 0) ().validNel else BoardBuilderError.NegativeDimensions.invalidNel

    /* Checks if a Position instance contains only positive values. */
    private def checkNonNegativePosition(position: Position): ValidatedNel[BoardBuilderError, Unit] =
      if (position.x >= 0 && position.y >= 0) ().validNel else BoardBuilderError.NegativePosition.invalidNel

    /* Checks whether the given PlayableArea is fully contained into the given Dimensions. */
    private def checkAreaInBounds(bounds: Dimensions, area: PlayableArea): ValidatedNel[BoardBuilderError, Unit] =
      if (area.position.x + area.dimensions.width <= bounds.width && area.position.y + area.dimensions.height <= bounds.height)
        ().validNel
      else
        BoardBuilderError.PlayableAreaNotInBounds.invalidNel

    /* Checks if there are no duplicate Position instances between the given Positions. */
    private def checkNonDuplicatePositions(positions: Seq[Position]): ValidatedNel[BoardBuilderError, Unit] = {
      if (positions.toSet.size == positions.size) ().validNel else BoardBuilderError.SamePositionForDifferentCells.invalidNel
    }

    /* Checks if the given Position is contained into the given Dimensions bounds. */
    private def checkPositionInBounds(position: Position, bounds: Dimensions): ValidatedNel[BoardBuilderError, Unit] =
      if (position.x <= bounds.width && position.y <= bounds.height)
        ().validNel
      else
        BoardBuilderError.CellOutsideBounds.invalidNel

    /* Checks whether the given Dimensions are set or not. */
    private def checkSetDimensions(dimensions: Option[Dimensions]): ValidatedNel[BoardBuilderError, Dimensions] =
      dimensions.toRight(BoardBuilderError.DimensionsUnset).toValidatedNel

    /* Checks whether the given PlayableArea is set or not. */
    private def checkSetPlayableArea(area: Option[PlayableArea]): ValidatedNel[BoardBuilderError, PlayableArea] =
      area.toRight(BoardBuilderError.PlayableAreaUnset).toValidatedNel

    /* Checks whether or not the given board Dimensions are valid. */
    def checkBoardDimensions(dimensions: Option[Dimensions]): ValidatedNel[BoardBuilderError, Dimensions] =
      checkSetDimensions(dimensions).andThen(d => checkNonNegativeDimensions(d).map(_ => d))

    /* Checks whether or not the given PlayableArea is valid. */
    def checkPlayableArea(
      playableArea: Option[PlayableArea],
      boardDimensions: Dimensions
    ): ValidatedNel[BoardBuilderError, PlayableArea] =
      checkSetPlayableArea(playableArea).andThen(a =>
        checkNonNegativeDimensions(a.dimensions)
          .product(checkNonNegativePosition(a.position))
          .andThen(_ => checkAreaInBounds(boardDimensions, a))
          .map(_ => a)
      )

    /* Checks whether or not the given cells Positions are valid. */
    def checkCellPositions(positions: Seq[Position], boardDimensions: Dimensions): ValidatedNel[BoardBuilderError, Unit] =
      checkNonDuplicatePositions(positions)
        .product(positions.map(checkNonNegativePosition(_)).sequence_)
        .andThen(_ => positions.map(checkPositionInBounds(_, boardDimensions)).sequence_)

  }

  import Checkers.*

  /** Checks if the data which was stored into the [[BoardBuilder]] is valid or not.
    *
    * An [[scala.util.Either]] is return at the end of the check operation: if the operation succeeded, a [[Board]] will be built
    * from the [[BoardBuilder]] and will be contained inside the [[scala.util.Either]]. If the check fails, the first
    * [[BoardBuilderError]] encountered while checking will be contained inside the [[scala.util.Either]].
    * @param builder
    *   the [[BoardBuilder]] to check
    * @return
    *   an [[scala.util.Either]] with the built [[Board]] if the check succeedes or with the first encountered
    *   [[BoardBuilderError]] if the check fails
    */
  def checkBuilderData(builder: BoardBuilder): Either[Seq[BoardBuilderError], Level[BaseCell]] = {
    checkBoardDimensions(builder.dimensions)
      .andThen(d =>
        (
          checkPlayableArea(builder.playableArea, d),
          checkCellPositions(
            builder.moverCells.map(_.position).toSeq ++
              builder.generatorCells.map(_.position) ++
              builder.rotatorCells.map(_.position) ++
              builder.blockCells.map(_.position) ++
              builder.enemyCells.map(_.position) ++
              builder.wallCells.map(_.position),
            d
          )
        ).mapN((a, _) => (d, a))
      )
      .map(t =>
        Level(
          t._1,
          builder.moverCells ++
            builder.generatorCells ++
            builder.rotatorCells ++
            builder.blockCells ++
            builder.enemyCells ++
            builder.wallCells,
          t._2
        )
      )
      .leftMap(_.toList)
      .toEither
  }
}
