package it.unibo.pps.caw.dsl.entities

import it.unibo.pps.caw.common.model.{Dimensions, PlayableArea}
import it.unibo.pps.caw.common.model.cell.*

/** A data structure to be used for collecting data useful for the creation of a new [[it.unibo.pps.caw.common.model.Level]]
  * instance.
  *
  * This trait does not represent the final product of the "level creation" process, but rather an accumulator of data useful to
  * the process itself. The final product will be an instance of [[it.unibo.pps.caw.common.model.Level]] itself. This means that
  * it should be used by entities that are responsible for building new [[it.unibo.pps.caw.common.model.Level]] instances. It must
  * be constructed through its companion object.
  */
trait LevelBuilderState {

  /** Returns an [[scala.Option]] with the dimensions of the [[it.unibo.pps.caw.common.model.Level]] to be built, if they have
    * been set.
    */
  val dimensions: Option[Dimensions]

  /** Returns an [[scala.Option]] with the [[PlayableArea]] of the [[it.unibo.pps.caw.common.model.Level]] to be built, if it has
    * been set.
    */
  val playableArea: Option[PlayableArea]

  /** Returns the mover cells that have to be placed on the [[it.unibo.pps.caw.common.model.Level]] to be built. */
  val moverCells: Set[BaseMoverCell]

  /** Returns the generator cells that have to be placed on the [[it.unibo.pps.caw.common.model.Level]] to be built. */
  val generatorCells: Set[BaseGeneratorCell]

  /** Returns the rotator cells that have to be placed on the [[it.unibo.pps.caw.common.model.Level]] to be built. */
  val rotatorCells: Set[BaseRotatorCell]

  /** Returns the block cells that have to be placed on the [[it.unibo.pps.caw.common.model.Level]] to be built. */
  val blockCells: Set[BaseBlockCell]

  /** Returns the enemy cells that have to be placed on the [[it.unibo.pps.caw.common.model.Level]] to be built. */
  val enemyCells: Set[BaseEnemyCell]

  /** Returns the wall cells that have to be placed on the [[it.unibo.pps.caw.common.model.Level]] to be built. */
  val wallCells: Set[BaseWallCell]

  /** Returns the deleter cells that have to be placed on the [[it.unibo.pps.caw.common.model.Level]] to be built. */
  val deleterCells: Set[BaseDeleterCell]
}

/** Companion object of the [[LevelBuilderState]] trait, containing its factory methods. */
object LevelBuilderState {

  /* Default implementation of the LevelBuilderState trait. */
  private case class BoardBuilderImpl(
    dimensions: Option[Dimensions],
    playableArea: Option[PlayableArea],
    moverCells: Set[BaseMoverCell],
    generatorCells: Set[BaseGeneratorCell],
    rotatorCells: Set[BaseRotatorCell],
    blockCells: Set[BaseBlockCell],
    enemyCells: Set[BaseEnemyCell],
    wallCells: Set[BaseWallCell],
    deleterCells: Set[BaseDeleterCell]
  ) extends LevelBuilderState

  /** Returns a new instance of the [[LevelBuilderState]] trait.
    *
    * @param dimensions
    *   the [[Dimensions]] of the [[LevelBuilderState]] to create, which are set to [[scala.None]] by default, which means unset
    * @param playableArea
    *   the [[PlayableArea]] of the [[LevelBuilderState]] to create, which is set to [[scala.None]] by default, which means unset
    * @param moverCells
    *   the mover cells stored by this [[LevelBuilderState]]
    * @param generatorCells
    *   the generator cells stored by this [[LevelBuilderState]]
    * @param rotatorCells
    *   the rotator cells stored by this [[LevelBuilderState]]
    * @param blockCells
    *   the block cells stored by this [[LevelBuilderState]]
    * @param enemyCells
    *   the enemy cells stored by this [[LevelBuilderState]]
    * @param wallCells
    *   the wall cells stored by this [[LevelBuilderState]]
    * @param deleterCells
    *   the deleter cells stored by this [[LevelBuilderState]]
    * @return
    *   a new instance of the [[LevelBuilderState]] trait
    */
  def apply(
    dimensions: Option[Dimensions] = None,
    playableArea: Option[PlayableArea] = None,
    moverCells: Set[BaseMoverCell] = Set.empty,
    generatorCells: Set[BaseGeneratorCell] = Set.empty,
    rotatorCells: Set[BaseRotatorCell] = Set.empty,
    blockCells: Set[BaseBlockCell] = Set.empty,
    enemyCells: Set[BaseEnemyCell] = Set.empty,
    wallCells: Set[BaseWallCell] = Set.empty,
    deleterCells: Set[BaseDeleterCell] = Set.empty
  ): LevelBuilderState = BoardBuilderImpl(
    dimensions,
    playableArea,
    moverCells,
    generatorCells,
    rotatorCells,
    blockCells,
    enemyCells,
    wallCells,
    deleterCells
  )

  /** Extensions methods for the [[LevelBuilderState]] trait. */
  extension (builder: LevelBuilderState) {

    /** Returns a new instance of the [[LevelBuilderState]] trait using a copy constructor, hence "cloning" an already created
      * instance unless some parameters are modified.
      *
      * @param dimensions
      *   the [[Dimensions]] of the [[LevelBuilderState]] to create, which are set to the original instance [[Dimensions]] by
      *   default
      * @param playableArea
      *   the [[PlayableArea]] of the [[LevelBuilderState]] to create, which is set to the [[PlayableArea]] of the original
      *   instance by default
      * @param moverCells
      *   the [[BaseMoverCell]] stored by this [[LevelBuilderState]], which are the mover cells stored in the original instance by
      *   default
      * @param generatorCells
      *   the [[BaseGeneratorCell]] stored by this [[LevelBuilderState]], which are the generator cells stored in the original
      *   instance by default
      * @param rotatorCells
      *   the [[BaseRotatorCell]] stored by this [[LevelBuilderState]], which are the rotator cells stored in the original
      *   instance by default
      * @param blockCells
      *   the [[BaseBlockCell]] stored by this [[LevelBuilderState]], which are the block cells stored in the original instance by
      *   default
      * @param enemyCells
      *   the [[BaseEnemyCell]] stored by this [[LevelBuilderState]], which are the enemy cells stored in the original instance by
      *   default
      * @param wallCells
      *   the [[BaseWallCell]] stored by this [[LevelBuilderState]], which are the wall cells stored in the original instance by
      *   default
      * @param deleterCells
      *   the [[BaseDeleterCell]] stored by this [[LevelBuilderState]], which are the deleter cells stored in the original
      *   instance by default
      * @return
      *   a new instance of the [[LevelBuilderState]] trait
      */
    def copy(
      dimensions: Option[Dimensions] = builder.dimensions,
      playableArea: Option[PlayableArea] = builder.playableArea,
      moverCells: Set[BaseMoverCell] = builder.moverCells,
      generatorCells: Set[BaseGeneratorCell] = builder.generatorCells,
      rotatorCells: Set[BaseRotatorCell] = builder.rotatorCells,
      blockCells: Set[BaseBlockCell] = builder.blockCells,
      enemyCells: Set[BaseEnemyCell] = builder.enemyCells,
      wallCells: Set[BaseWallCell] = builder.wallCells,
      deleterCells: Set[BaseDeleterCell] = builder.deleterCells
    ): LevelBuilderState = apply(
      dimensions,
      playableArea,
      moverCells,
      generatorCells,
      rotatorCells,
      blockCells,
      enemyCells,
      wallCells,
      deleterCells
    )
  }
}
