package it.unibo.pps.caw.dsl.entities

/** A game board, containing all cells that the user has inserted in it.
  *
  * An instance of this class is to be constructed after the build operation made by the [[BoardBuilder]], when the data inserted
  * by the user has been validated and it was found indeed valid. If the data is still being collected, use [[BoardBuilder]] to
  * store it instead. It must be constructed through its companion object.
  */
trait Board {

  /** Returns the [[Dimensions]] of this board. */
  val dimensions: Dimensions

  /** Returns the [[PlayableArea]] of this board. */
  val playableArea: PlayableArea

  /** Returns the mover cells that have been placed on this board. */
  val moverCells: Set[OrientableCell]

  /** Returns the generator cells that have been placed on this board. */
  val generatorCells: Set[OrientableCell]

  /** Returns the rotator cells that have been placed on this board. */
  val rotatorCells: Set[RotatableCell]

  /** Returns the block cells that have been placed on this board. */
  val blockCells: Set[PushableCell]

  /** Returns the enemy cells that have been placed on this board. */
  val enemyCells: Set[Cell]

  /** Returns the wall cells that have been placed on this board. */
  val wallCells: Set[Cell]
}

/** Companion object of the [[Board]] trait, contains its factory method. */
object Board {
  
  /* Default implementation of the Board trait. */
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

  /** Returns a new instance of the [[Board]] trait.
    *
    * @param dimensions
    *   the [[Dimensions]] of the board to create
    * @param playableArea
    *   the [[PlayableArea]] of the board to create
    * @param moverCells
    *   the mover cells that have been placed on the board to create
    * @param generatorCells
    *   the generator cells that have been placed on the board to create
    * @param rotatorCells
    *   the rotator cells that have been placed on the board to create
    * @param blockCells
    *   the block cells that have been placed on the board to create
    * @param enemyCells
    *   the enemy cells that have been placed on the board to create
    * @param wallCells
    *   the wall cells that have been placed on the board to create
    * @return
    *   a new instance of the [[Board]] trait
    */
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
