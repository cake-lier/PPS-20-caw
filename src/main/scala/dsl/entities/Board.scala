package it.unibo.pps.caw.dsl.entities

trait Board {
  val dimensions: Dimensions

  val playableArea: PlayableArea

  val moverCells: Set[OrientableCell]

  val generatorCells: Set[OrientableCell]

  val rotatorCells: Set[RotatableCell]

  val blockCells: Set[PushableCell]

  val enemyCells: Set[Cell]

  val wallCells: Set[Cell]
}

object Board {
  private case class BoardImpl(
      dimensions: Dimensions,
      playableArea: PlayableArea,
      moverCells: Set[OrientableCell],
      generatorCells: Set[OrientableCell],
      rotatorCells: Set[RotatableCell],
      blockCells: Set[PushableCell],
      enemyCells: Set[Cell],
      wallCells: Set[Cell]
  ) extends Board

  def apply(
      dimensions: Dimensions,
      playableArea: PlayableArea,
      moverCells: Set[OrientableCell],
      generatorCells: Set[OrientableCell],
      rotatorCells: Set[RotatableCell],
      blockCells: Set[PushableCell],
      enemyCells: Set[Cell],
      wallCells: Set[Cell]
  ): Board = BoardImpl(
    dimensions,
    playableArea,
    moverCells,
    generatorCells,
    rotatorCells,
    blockCells,
    enemyCells,
    wallCells
  )
}
