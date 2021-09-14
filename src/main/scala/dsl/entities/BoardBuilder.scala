package it.unibo.pps.caw.dsl.entities

/** A data structure to be used for collecting data useful for the creation of a new [[Board]] instance.
  *
  * This trait does not represent the final product of the "board creation" process, but rather an accumulator of data useful to
  * the process itself. The final product will be an instance of [[Board]] itself. It must be constructed through its companion
  * object.
  */
trait BoardBuilder {

  /** Returns an [[scala.Option]] with the dimensions of the new [[Board]], if they have been set. */
  val dimensions: Option[Dimensions]

  /** Returns an [[scala.Option]] with the [[PlayableArea]] of the new [[Board]], if it has been set. */
  val playableArea: Option[PlayableArea]

  /** Returns the mover cells that have to be placed on the new [[Board]]. */
  val moverCells: Set[OrientableCell]

  /** Returns the generator cells that have to be placed on the new [[Board]]. */
  val generatorCells: Set[OrientableCell]

  /** Returns the rotator cells that have to be placed on the new [[Board]]. */
  val rotatorCells: Set[RotatableCell]

  /** Returns the block cells that have to be placed on the new [[Board]]. */
  val blockCells: Set[PushableCell]

  /** Returns the enemy cells that have to be placed on the new [[Board]]. */
  val enemyCells: Set[Cell]

  /** Returns the wall cells that have to be placed on the new [[Board]]. */
  val wallCells: Set[Cell]
}

/** Companion object of the [[BoardBuilder]] trait, containing its factory methods. */
object BoardBuilder {

  /* Default implementation of the BoardBuilder trait. */
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

  /** Returns a new instance of the [[BoardBuilder]] trait.
    *
    * @param dimensions
    *   the [[Dimensions]] of the [[BoardBuilder]] to create, which are set to [[scala.None]] by default, which means unset
    * @param playableArea
    *   the [[PlayableArea]] of the [[BoardBuilder]] to create, which is set to [[scala.None]] by default, which means unset
    * @param moverCells
    *   the mover cells stored by this [[BoardBuilder]]
    * @param generatorCells
    *   the generator cells stored by this [[BoardBuilder]]
    * @param rotatorCells
    *   the rotator cells stored by this [[BoardBuilder]]
    * @param blockCells
    *   the block cells stored by this [[BoardBuilder]]
    * @param enemyCells
    *   the enemy cells stored by this [[BoardBuilder]]
    * @param wallCells
    *   the wall cells stored by this [[BoardBuilder]]
    * @return
    *   a new instance of the [[BoardBuilder]] trait
    */
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

  /** Extensions methods for the [[BoardBuilder]] trait. */
  extension (builder: BoardBuilder) {

    /** Returns a new instance of the [[BoardBuilder]] trait using a copy constructor, hence "cloning" an already created instance
      * unless some parameters are modified.
      *
      * @param dimensions
      *   the [[Dimensions]] of the [[BoardBuilder]] to create, which are set to the original instance [[Dimensions]] by default
      * @param playableArea
      *   the [[PlayableArea]] of the [[BoardBuilder]] to create, which is set to the [[PlayableArea]] of the original instance by
      *   default
      * @param moverCells
      *   the mover cells stored by this [[BoardBuilder]], which are the mover cells stored in the original instance by default
      * @param generatorCells
      *   the generator cells stored by this [[BoardBuilder]], which are the generator cells stored in the original instance by
      *   default
      * @param rotatorCells
      *   the rotator cells stored by this [[BoardBuilder]], which are the rotator cells stored in the original instance by
      *   default
      * @param blockCells
      *   the block cells stored by this [[BoardBuilder]], which are the block cells stored in the original instance by default
      * @param enemyCells
      *   the enemy cells stored by this [[BoardBuilder]], which are the enemy cells stored in the original instance by default
      * @param wallCells
      *   the wall cells stored by this [[BoardBuilder]], which are the wall cells stored in the original instance by default
      * @return
      *   a new instance of the [[BoardBuilder]] trait
      */
    def copy(
        dimensions: Option[Dimensions] = builder.dimensions,
        playableArea: Option[PlayableArea] = builder.playableArea,
        moverCells: Set[OrientableCell] = builder.moverCells,
        generatorCells: Set[OrientableCell] = builder.generatorCells,
        rotatorCells: Set[RotatableCell] = builder.rotatorCells,
        blockCells: Set[PushableCell] = builder.blockCells,
        enemyCells: Set[Cell] = builder.enemyCells,
        wallCells: Set[Cell] = builder.wallCells
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
