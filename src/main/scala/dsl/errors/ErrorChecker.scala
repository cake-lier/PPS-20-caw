package it.unibo.pps.caw.dsl.errors

import it.unibo.pps.caw.dsl.entities.*

object ErrorChecker {
  private object Checkers {
    def checkNonNegativeDimensions(dimensions: Dimensions): Either[BoardBuilderError, Unit] =
      Either.cond(dimensions.width >= 0 && dimensions.height >= 0, (), BoardBuilderError.NegativeDimensions)

    def checkNonNegativePosition(position: Position): Either[BoardBuilderError, Unit] =
      Either.cond(position.x >= 0 && position.y >= 0, (), BoardBuilderError.NegativePosition)

    def checkAreaIsInBound(bounds: Dimensions, area: PlayableArea): Either[BoardBuilderError, Unit] =
      Either.cond(
        area.position.x + area.dimensions.width <= bounds.width && area.position.y + area.dimensions.height <= bounds.height,
        (),
        BoardBuilderError.PlayableAreaNotInBounds
      )

    def checkNonDuplicatePositions(positions: Seq[Position]): Either[BoardBuilderError, Unit] = {
      Either.cond(positions.toSet.size == positions.size, (), BoardBuilderError.SamePositionForDifferentCells)
    }

    def checkPositionInBoundary(position: Position, bounds: Dimensions): Either[BoardBuilderError, Unit] =
      Either.cond(position.x <= bounds.width && position.y <= bounds.height, (), BoardBuilderError.CellOutsideBounds)

    def checkIsSetDimensions(dimensions: Option[Dimensions]): Either[BoardBuilderError, Dimensions] = dimensions match {
      case Some(d) => Right(d)
      case _       => Left(BoardBuilderError.DimensionsUnset)
    }

    def checkIsSetPlayableArea(area: Option[PlayableArea]): Either[BoardBuilderError, PlayableArea] = area match {
      case Some(a) => Right(a)
      case _       => Left(BoardBuilderError.PlayableAreaUnset)
    }

    def composeEithers(
        check: Position => Either[BoardBuilderError, Unit],
        positions: Iterable[Position]
    ): Either[BoardBuilderError, Unit] =
      positions
        .map(check)
        .foldLeft[Either[BoardBuilderError, Unit]](Right(()))((a, e) =>
          for {
            _ <- a
            error <- e.left
          } yield error
        )
  }

  import Checkers.*

  def checkBoard(board: BoardBuilder): Either[BoardBuilderError, Board] = {
    val positions: Seq[Position] = board.moverCells.map(_.position).toSeq ++
      board.generatorCells.map(_.position) ++
      board.rotatorCells.map(_.position) ++
      board.blockCells.map(_.position) ++
      board.enemyCells.map(_.position) ++
      board.wallCells.map(_.position)
    for {
      d <- checkIsSetDimensions(board.dimensions)
      _ <- checkNonNegativeDimensions(d)
      a <- checkIsSetPlayableArea(board.playableArea)
      _ <- checkNonNegativeDimensions(a.dimensions)
      _ <- checkNonNegativePosition(a.position)
      _ <- checkAreaIsInBound(d, a)
      _ <- checkNonDuplicatePositions(positions)
      _ <- composeEithers(checkNonNegativePosition(_), positions)
      _ <- composeEithers(checkPositionInBoundary(_, d), positions)
    } yield Board(
      d,
      a,
      board.moverCells,
      board.generatorCells,
      board.rotatorCells,
      board.blockCells,
      board.enemyCells,
      board.wallCells
    )
  }
}
