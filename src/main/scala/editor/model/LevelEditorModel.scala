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

    override val builtLevel: Option[Level[BaseCell]] = {
      currentLevel
        .playableArea
        .map(a =>
          Level(
            Dimensions(currentLevel.width - 2, currentLevel.height - 2),
            currentLevel
              .board
              .cells
              .filter(_.playable)
              .map(_.changePositionProperty(p => (p.x - 1, p.y - 1)))
              .map(_.toBaseCell),
            PlayableArea(a.dimensions)((a.position.x - 1, a.position.y - 1))
          )
        )
    }

    override def resetLevel: LevelEditorModel = LevelEditorModel(currentLevel.width, currentLevel.height)

    override def updateCellPosition(oldPosition: Position, newPosition: Position): LevelEditorModel = {
      val updatedCell: PlayableCell = currentLevel
        .board
        .cells
        .find(_.position == oldPosition)
        .map(_.changePositionProperty(_ => newPosition))
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

  private def apply(levelBuilder: LevelBuilder): LevelEditorModel =
    val walls: Set[PlayableCell] = Set(
      (0 to levelBuilder.width + 1).map(i => PlayableWallCell((i, 0))(false)),
      (0 to levelBuilder.width + 1).map(i => PlayableWallCell((i, levelBuilder.height + 1))(false)),
      (1 to levelBuilder.height).map(i => PlayableWallCell((0, i))(false)),
      (1 to levelBuilder.height).map(i => PlayableWallCell((levelBuilder.width + 1, i))(false))
    ).flatten
    LevelEditorModelImpl(
      levelBuilder
        .playableArea
        .map(p =>
          LevelBuilder(
            levelBuilder.width + 2,
            levelBuilder.height + 2,
            levelBuilder.board ++ walls,
            PlayableArea(p.dimensions)((p.position.x + 1, p.position.y + 1))
          )
        )
        .getOrElse(LevelBuilder(levelBuilder.width + 2, levelBuilder.height + 2, levelBuilder.board ++ walls))
    )

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
        level.board.map(_.toPlayableCell(_ => true)).map(_.changePositionProperty(p => (p.x + 1, p.y + 1))),
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
    LevelEditorModel(LevelBuilder(width, height, Board.empty[PlayableCell]))
}
