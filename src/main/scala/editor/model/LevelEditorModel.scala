package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.model.cell.PlayableCell.toPlayableCell

/** The model of the editor, containing all its business logic.
  *
  * The model contains the logic of the editor, providing the necessary functionalities to modify the current edited level given
  * the player inputs. A level is made of a playable area and cells: the player is able to select and deselect the playable area;
  * they can add a game cell in whichever position they want, move it from a position to another or remove it. It must be
  * constructed through its companion object.
  */
sealed trait LevelEditorModel {

  /** The [[LevelBuilderState]] of the editor. */
  val currentState: LevelBuilderState

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

/** The companion object of the trait [[LevelEditorModel]], containing its factory methods. */
object LevelEditorModel {

  /* Implementation of LevelEditorModel. */
  private case class LevelEditorModelImpl(currentState: LevelBuilderState) extends LevelEditorModel {

    override val builtLevel: Option[Level[BaseCell]] =
      currentState
        .playableArea
        .map(a =>
          Level(
            Dimensions(currentState.dimensions.width - 2, currentState.dimensions.height - 2),
            currentState.board.filter(_.playable).map(_.changePositionProperty(p => (p.x - 1, p.y - 1))).map(_.toBaseCell),
            PlayableArea(a.dimensions)((a.position.x - 1, a.position.y - 1))
          )
        )

    override def resetLevel: LevelEditorModel =
      //-2 because addCornerWalls adds 2 in each dimension
      LevelEditorModel(currentState.dimensions.width - 2, currentState.dimensions.height - 2)

    override def updateCellPosition(oldPosition: Position, newPosition: Position): LevelEditorModel =
      (currentState.board.find(_.position == newPosition), currentState.board.find(_.position == oldPosition)) match {
        case (None, Some(c)) =>
          LevelEditorModelImpl(
            currentState.copy(
              board = currentState.board.filter(_.position != oldPosition) + c.changePositionProperty(_ => newPosition)
            )
          )
        case _ => this
      }

    override def setCell(cell: BaseCell): LevelEditorModel =
      LevelEditorModelImpl(currentState.copy(board = currentState.board + cell.toPlayableCell(_ => true)))

    override def unsetCell(position: Position): LevelEditorModel =
      LevelEditorModelImpl(currentState.copy(board = currentState.board.filter(_.position != position)))

    override def setPlayableArea(position: Position, dimensions: Dimensions): LevelEditorModel =
      LevelEditorModelImpl(currentState.copy(playableArea = Some(PlayableArea(dimensions)(position))))

    override def unsetPlayableArea: LevelEditorModel = LevelEditorModelImpl(currentState.copy(playableArea = None))
  }

  private def addCornerWalls(levelBuilder: LevelBuilderState): LevelBuilderState = {
    val walls: Set[PlayableCell] = Set(
      (0 to levelBuilder.dimensions.width + 1).map(i => PlayableWallCell((i, 0))(playable = false)),
      (0 to levelBuilder.dimensions.width + 1).map(i =>
        PlayableWallCell((i, levelBuilder.dimensions.height + 1))(playable = false)
      ),
      (1 to levelBuilder.dimensions.height).map(i => PlayableWallCell((0, i))(playable = false)),
      (1 to levelBuilder.dimensions.height).map(i => PlayableWallCell((levelBuilder.dimensions.width + 1, i))(playable = false))
    ).flatten
    levelBuilder
      .playableArea
      .map(p => LevelBuilderState(PlayableArea(p.dimensions)((p.position.x + 1, p.position.y + 1))))
      .getOrElse(LevelBuilderState.apply: Dimensions => Board[PlayableCell] => LevelBuilderState)(
        (levelBuilder.dimensions.width + 2, levelBuilder.dimensions.height + 2)
      )(
        levelBuilder.board ++ walls
      )
  }

  /** Returns a new instance of [[LevelEditorModel]] when editing an already existing level.
    * @param level
    *   the existing level to be edited
    * @return
    *   a new instance of [[LevelEditorModel]]
    */
  def apply(level: Level[BaseCell]): LevelEditorModel =
    LevelEditorModelImpl(
      addCornerWalls(
        LevelBuilderState(
          level.playableArea
        )(
          level.dimensions
        )(
          level.board.map(_.toPlayableCell(_ => true)).map(_.changePositionProperty(p => (p.x + 1, p.y + 1)))
        )
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
    LevelEditorModelImpl(addCornerWalls(LevelBuilder((width, height))(Board.empty[PlayableCell])))
}
