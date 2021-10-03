package it.unibo.pps.caw.dsl.entities

import it.unibo.pps.caw.common.model.{Dimensions, PlayableArea}
import it.unibo.pps.caw.common.model.cell.*

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
  val moverCells: Set[BaseMoverCell]

  /** Returns the generator cells that have to be placed on the new [[Board]]. */
  val generatorCells: Set[BaseGeneratorCell]

  /** Returns the rotator cells that have to be placed on the new [[Board]]. */
  val rotatorCells: Set[BaseRotatorCell]

  /** Returns the block cells that have to be placed on the new [[Board]]. */
  val blockCells: Set[BaseBlockCell]

  /** Returns the enemy cells that have to be placed on the new [[Board]]. */
  val enemyCells: Set[BaseEnemyCell]

  /** Returns the wall cells that have to be placed on the new [[Board]]. */
  val wallCells: Set[BaseWallCell]
}

/** Companion object of the [[BoardBuilder]] trait, containing its factory methods. */
object BoardBuilder {

  /* Default implementation of the BoardBuilder trait. */
  private case class BoardBuilderImpl(
    dimensions: Option[Dimensions],
    playableArea: Option[PlayableArea],
    moverCells: Set[BaseMoverCell],
    generatorCells: Set[BaseGeneratorCell],
    rotatorCells: Set[BaseRotatorCell],
    blockCells: Set[BaseBlockCell],
    enemyCells: Set[BaseEnemyCell],
    wallCells: Set[BaseWallCell]
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
    moverCells: Set[BaseMoverCell] = Set.empty,
    generatorCells: Set[BaseGeneratorCell] = Set.empty,
    rotatorCells: Set[BaseRotatorCell] = Set.empty,
    blockCells: Set[BaseBlockCell] = Set.empty,
    enemyCells: Set[BaseEnemyCell] = Set.empty,
    wallCells: Set[BaseWallCell] = Set.empty
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
      moverCells: Set[BaseMoverCell] = builder.moverCells,
      generatorCells: Set[BaseGeneratorCell] = builder.generatorCells,
      rotatorCells: Set[BaseRotatorCell] = builder.rotatorCells,
      blockCells: Set[BaseBlockCell] = builder.blockCells,
      enemyCells: Set[BaseEnemyCell] = builder.enemyCells,
      wallCells: Set[BaseWallCell] = builder.wallCells
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
