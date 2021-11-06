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

  /** Returns an [[scala.Option]] with the [[it.unibo.pps.caw.common.model.PlayableArea]] of the
    * [[it.unibo.pps.caw.common.model.Level]] to be built, if it has been set.
    */
  val playableArea: Option[PlayableArea]

  /** Returns the cells that have to be placed on the [[it.unibo.pps.caw.common.model.Level]] to be built. */
  val cells: Seq[BaseCell]
}

/** Companion object of the [[LevelBuilderState]] trait, containing its factory methods. */
object LevelBuilderState {

  /* Default implementation of the LevelBuilderState trait. */
  private case class BoardBuilderImpl(
    dimensions: Option[Dimensions],
    playableArea: Option[PlayableArea],
    cells: Seq[BaseCell]
  ) extends LevelBuilderState

  /** Returns a new instance of the [[LevelBuilderState]] trait.
    *
    * @param dimensions
    *   the [[it.unibo.pps.caw.common.model.Dimensions]] of the [[LevelBuilderState]] to create, which are set to [[scala.None]]
    *   by default, which means unset
    * @param playableArea
    *   the [[it.unibo.pps.caw.common.model.PlayableArea]] of the [[LevelBuilderState]] to create, which is set to [[scala.None]]
    *   by default, which means unset
    * @param cells
    *   the cells stored by this [[LevelBuilderState]]
    * @return
    *   a new instance of the [[LevelBuilderState]] trait
    */
  def apply(
    dimensions: Option[Dimensions] = None,
    playableArea: Option[PlayableArea] = None,
    cells: Seq[BaseCell] = Seq.empty
  ): LevelBuilderState = BoardBuilderImpl(dimensions, playableArea, cells)

  /** Extensions methods for the [[LevelBuilderState]] trait. */
  extension (builder: LevelBuilderState) {

    /** Returns a new instance of the [[LevelBuilderState]] trait using a copy constructor, hence "cloning" an already created
      * instance unless some parameters are modified.
      *
      * @param dimensions
      *   the [[it.unibo.pps.caw.common.model.Dimensions]] of the [[LevelBuilderState]] to create, which are set to the original
      *   instance dimensions by default
      * @param playableArea
      *   the [[it.unibo.pps.caw.common.model.PlayableArea]] of the [[LevelBuilderState]] to create, which is set to the playable
      *   area of the original instance by default
      * @param cells
      *   the [[it.unibo.pps.caw.common.model.cell.BaseCell]] stored by this [[LevelBuilderState]], which are the cells stored in
      *   the original instance by default
      * @return
      *   a new instance of the [[LevelBuilderState]] trait
      */
    def copy(
      dimensions: Option[Dimensions] = builder.dimensions,
      playableArea: Option[PlayableArea] = builder.playableArea,
      cells: Seq[BaseCell] = builder.cells
    ): LevelBuilderState = apply(dimensions, playableArea, cells)
  }
}
