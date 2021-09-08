package it.unibo.pps.caw.dsl

trait Board {
  val dimensions: Option[Dimensions]

  val playableArea: Option[PlayableArea]

  val moverCells: Set[OrientedCell]

  val generatorCells: Set[OrientedCell]

  val rotatorCells: Set[DirectedCell]

  val blockCells: Set[MovableCell]

  val enemyCells: Set[Cell]

  val wallCells: Set[Cell]
}

object Board {
  private case class BoardImpl(
      dimensions: Option[Dimensions],
      playableArea: Option[PlayableArea],
      moverCells: Set[OrientedCell],
      generatorCells: Set[OrientedCell],
      rotatorCells: Set[DirectedCell],
      blockCells: Set[MovableCell],
      enemyCells: Set[Cell],
      wallCells: Set[Cell]
  ) extends Board

  def apply(
      dimensions: Option[Dimensions] = None,
      playableArea: Option[PlayableArea] = None,
      moverCells: Set[OrientedCell] = Set.empty,
      generatorCells: Set[OrientedCell] = Set.empty,
      rotatorCells: Set[DirectedCell] = Set.empty,
      blockCells: Set[MovableCell] = Set.empty,
      enemyCells: Set[Cell] = Set.empty,
      wallCells: Set[Cell] = Set.empty
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

  extension(board: Board) {
    def copy(
        dimensions: Option[Dimensions] = board.dimensions,
        playableArea: Option[PlayableArea] = board.playableArea,
        moverCells: Set[OrientedCell] = board.moverCells,
        generatorCells: Set[OrientedCell] = board.generatorCells,
        rotatorCells: Set[DirectedCell] = board.rotatorCells,
        blockCells: Set[MovableCell] = board.blockCells,
        enemyCells: Set[Cell] = board.enemyCells,
        wallCells: Set[Cell] = board.wallCells
    ): Board = apply(
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
