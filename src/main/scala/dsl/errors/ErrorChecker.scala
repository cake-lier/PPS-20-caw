package it.unibo.pps.caw.dsl.errors

import it.unibo.pps.caw.dsl.entities.*

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
    def checkNonNegativeDimensions(dimensions: Dimensions): Either[BoardBuilderError, Unit] =
      Either.cond(dimensions.width >= 0 && dimensions.height >= 0, (), BoardBuilderError.NegativeDimensions)

    /* Checks if a Position instance contains only positive values. */
    def checkNonNegativePosition(position: Position): Either[BoardBuilderError, Unit] =
      Either.cond(position.x >= 0 && position.y >= 0, (), BoardBuilderError.NegativePosition)

    /* Checks whether the given PlayableArea is fully contained into the given Dimensions. */
    def checkAreaInBounds(bounds: Dimensions, area: PlayableArea): Either[BoardBuilderError, Unit] =
      Either.cond(
        area.position.x + area.dimensions.width <= bounds.width && area.position.y + area.dimensions.height <= bounds.height,
        (),
        BoardBuilderError.PlayableAreaNotInBounds
      )

    /* Checks if there are no duplicate Position instances between the given Positions. */
    def checkNonDuplicatePositions(positions: Seq[Position]): Either[BoardBuilderError, Unit] = {
      Either.cond(positions.toSet.size == positions.size, (), BoardBuilderError.SamePositionForDifferentCells)
    }

    /* Checks if the given Position is contained into the given Dimensions bounds. */
    def checkPositionInBounds(position: Position, bounds: Dimensions): Either[BoardBuilderError, Unit] =
      Either.cond(position.x <= bounds.width && position.y <= bounds.height, (), BoardBuilderError.CellOutsideBounds)

    /* Checks whether the given Dimensions are set or not. */
    def checkSetDimensions(dimensions: Option[Dimensions]): Either[BoardBuilderError, Dimensions] =
      dimensions.toRight(BoardBuilderError.DimensionsUnset)

    /* Checks whether the given PlayableArea is set or not. */
    def checkSetPlayableArea(area: Option[PlayableArea]): Either[BoardBuilderError, PlayableArea] =
      area.toRight(BoardBuilderError.PlayableAreaUnset)

    /* Utility method for composing a collection of Eithers with Unit as a Right type. The Eithers are composed in a way such
     * that, if a Left is found while traversing, is then returned. If only Rights are found, a Unit inside a Right is returned.
     */
    def composeEithers[A](check: A => Either[BoardBuilderError, Unit], items: Iterable[A]): Either[BoardBuilderError, Unit] =
      items
        .map(check)
        .foldLeft[Either[BoardBuilderError, Unit]](Right(())) { (a, e) =>
          for {
            _ <- a
            error <- e.left
          } yield error
        }
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
  def checkBuilderData(builder: BoardBuilder): Either[BoardBuilderError, Board] = {
    val positions: Seq[Position] =
      builder.moverCells.map(_.position).toSeq ++
        builder.generatorCells.map(_.position) ++
        builder.rotatorCells.map(_.position) ++
        builder.blockCells.map(_.position) ++
        builder.enemyCells.map(_.position) ++
        builder.wallCells.map(_.position)
    for {
      d <- checkSetDimensions(builder.dimensions)
      _ <- checkNonNegativeDimensions(d)
      a <- checkSetPlayableArea(builder.playableArea)
      _ <- checkNonNegativeDimensions(a.dimensions)
      _ <- checkNonNegativePosition(a.position)
      _ <- checkAreaInBounds(d, a)
      _ <- checkNonDuplicatePositions(positions)
      _ <- composeEithers(checkNonNegativePosition(_), positions)
      _ <- composeEithers(checkPositionInBounds(_, d), positions)
    } yield Board(
      d,
      a,
      builder.moverCells,
      builder.generatorCells,
      builder.rotatorCells,
      builder.blockCells,
      builder.enemyCells,
      builder.wallCells
    )
  }
}
