package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.model.cell.PlayableCell.toPlayableCell

/** The model of the editor, containing all its business logic.
  *
  * The model contains the logic of the editor, providing the necessary functionalities to modify the current edited level given
  * the player inputs. A level is made of a playable area and cells: the player is able to select and deselect the playable area;
  * they can add a game cell in whichever position they want, move it from a position to another or remove it.
  */
sealed trait LevelEditorModel {

  /** The [[LevelBuilder]] of the editor. */
  val currentLevel: LevelBuilder

  /** The [[Level]] built by the player. */
  val builtLevel: Option[Level[BaseCell]]

  /** Resets the level, removing the playable area and all the cells.
    * @return
    *   a new instance of empty [[LevelEditorModel]]
    */
  def resetLevel: LevelEditorModel

  /** Adds the given cell to the model.
    * @param cell
    *   the [[BaseCell]] to be added
    * @return
    *   a new instance of [[LevelEditorModel]] with the added cell
    */
  def setCell(cell: BaseCell): LevelEditorModel

  /** Moves a cell given its previous [[Position]] and the new [[Position]] in which was moved.
    * @param oldPosition
    *   the [[Position]] of the [[Cell]] that was moved
    * @param newPosition
    *   the new [[Position]] of the [[Cell]]
    * @return
    *   a new instance of [[LevelEditorModel]] with the [[Cell]] moved to its new [[Position]]
    */
  def updateCellPosition(oldPosition: Position, newPosition: Position): LevelEditorModel

  /** Removes the cell in the given [[Position]].
    * @param position
    *   the [[Position]] of the [[Cell]] that has to be removed
    * @return
    *   a new instance of [[LevelEditorModel]] with the [[Cell]] removed
    */
  def unsetCell(position: Position): LevelEditorModel

  /** Places a [[PlayableArea]] in the given [[Position]] with the given [[Dimensions]].
    * @param position
    *   the upper left corner [[Position]] of the [[PlayableArea]]
    * @param dimensions
    *   the [[Dimensions]] of the [[PlayableArea]]
    * @return
    *   a new instance of [[LevelEditorModel]] containing a new [[PlayableArea]]
    */
  def setPlayableArea(position: Position, dimensions: Dimensions): LevelEditorModel

  /** Removes the [[PlayableArea]] from the level.
    * @return
    *   a new instance of [[LevelEditorModel]] with the [[PlayableArea]] removed
    */
  def unsetPlayableArea: LevelEditorModel
}

/** The companion object of the trait [[LevelEditorModel]]. */
object LevelEditorModel {

  /* Implementation of LevelEditorModel. */
  private case class LevelEditorModelImpl(currentLevel: LevelBuilder) extends LevelEditorModel {

    override val builtLevel: Option[Level[BaseCell]] =
      currentLevel
        .playableArea
        .map(
          Level(
            Dimensions(currentLevel.width, currentLevel.height),
            currentLevel
              .board
              .map(_.toBaseCell),
            _
          )
        )

    override def resetLevel: LevelEditorModel = LevelEditorModel(currentLevel.width, currentLevel.height)

    override def updateCellPosition(oldPosition: Position, newPosition: Position): LevelEditorModel = {
      val updatedCell: PlayableCell = currentLevel
        .board
        .cells
        .find(_.position == oldPosition)
        .map {
          case PlayableWallCell(_, playable)  => PlayableWallCell(newPosition)(playable)
          case PlayableEnemyCell(_, playable) => PlayableEnemyCell(newPosition)(playable)
          case PlayableRotatorCell(rotationDirection, _, playable) =>
            PlayableRotatorCell(rotationDirection)(newPosition)(playable)
          case PlayableGeneratorCell(orientation, _, playable) => PlayableGeneratorCell(orientation)(newPosition)(playable)
          case PlayableMoverCell(orientation, _, playable)     => PlayableMoverCell(orientation)(newPosition)(playable)
          case PlayableBlockCell(push, _, playable)            => PlayableBlockCell(push)(newPosition)(playable)
        }
        .get
      LevelEditorModelImpl(currentLevel.copy(board = currentLevel.board.filter(_.position != oldPosition) + updatedCell))
    }

    override def setCell(cell: BaseCell): LevelEditorModel =
      LevelEditorModelImpl(currentLevel.copy(board = currentLevel.board + cell.toPlayableCell(_ => true)))

    override def unsetCell(position: Position): LevelEditorModel =
      LevelEditorModelImpl(currentLevel.copy(board = currentLevel.board.filter(_.position != position)))

    override def setPlayableArea(position: Position, dimensions: Dimensions): LevelEditorModel =
      LevelEditorModelImpl(currentLevel.copy(playableArea = Some(PlayableArea(dimensions)(position))))

    override def unsetPlayableArea: LevelEditorModel = LevelEditorModelImpl(currentLevel.copy(playableArea = None))
  }

  /** Returns a new instance of [[LevelEditorModel]] when editing an already existing level.
    * @param level
    *   the existing level to be edited
    * @return
    *   a new instance of [[LevelEditorModel]]
    */
  def apply(level: Level[BaseCell]): LevelEditorModel =
    LevelEditorModelImpl(
      LevelBuilder(
        level.dimensions.width,
        level.dimensions.height,
        level.board.map(_.toPlayableCell(_ => true)),
        level.playableArea
      )
    )

  /** Returns a new instance of [[LevelEditorModel]] when creating a new level.
    * @param width
    *   the width of the level
    * @param height
    *   the height of the level
    * @return
    *   a new instance of [[LevelEditorModel]]
    */
  def apply(width: Int, height: Int): LevelEditorModel =
    LevelEditorModelImpl(LevelBuilder(width, height, Board.empty[PlayableCell]))
}
