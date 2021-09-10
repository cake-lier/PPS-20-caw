package it.unibo.pps.caw.dsl.entities

trait BoardBuilder {
  val dimensions: Option[Dimensions]

  val playableArea: Option[PlayableArea]

  val moverCells: Set[OrientableCell]

  val generatorCells: Set[OrientableCell]

  val rotatorCells: Set[RotatableCell]

  val blockCells: Set[PushableCell]

  val enemyCells: Set[Cell]

  val wallCells: Set[Cell]
}

object BoardBuilder {
  private case class BoardBuilderImpl(
      dimensions: Option[Dimensions],
      playableArea: Option[PlayableArea],
      moverCells: Set[OrientableCell],
      generatorCells: Set[OrientableCell],
      rotatorCells: Set[RotatableCell],
      blockCells: Set[PushableCell],
      enemyCells: Set[Cell],
      wallCells: Set[Cell]
  ) extends BoardBuilder

  def apply(
      dimensions: Option[Dimensions] = None,
      playableArea: Option[PlayableArea] = None,
      moverCells: Set[OrientableCell] = Set.empty,
      generatorCells: Set[OrientableCell] = Set.empty,
      rotatorCells: Set[RotatableCell] = Set.empty,
      blockCells: Set[PushableCell] = Set.empty,
      enemyCells: Set[Cell] = Set.empty,
      wallCells: Set[Cell] = Set.empty
  ): BoardBuilder = BoardBuilderImpl(
    dimensions,
    playableArea,
    moverCells,
    generatorCells,
    rotatorCells,
    blockCells,
    enemyCells,
    wallCells
  )

  extension (board: BoardBuilder) {
    def copy(
        dimensions: Option[Dimensions] = board.dimensions,
        playableArea: Option[PlayableArea] = board.playableArea,
        moverCells: Set[OrientableCell] = board.moverCells,
        generatorCells: Set[OrientableCell] = board.generatorCells,
        rotatorCells: Set[RotatableCell] = board.rotatorCells,
        blockCells: Set[PushableCell] = board.blockCells,
        enemyCells: Set[Cell] = board.enemyCells,
        wallCells: Set[Cell] = board.wallCells
    ): BoardBuilder = apply(
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
}
