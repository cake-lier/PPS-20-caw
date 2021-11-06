package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.model.cell.PlayableCell.toPlayableCell

/** The model of the editor, containing all its business logic.
  *
  * The model contains the logic of the editor, providing the necessary functionalities to modify the current edited level given
  * the player inputs. A level is made of a [[it.unibo.pps.caw.common.model.PlayableArea]] and a
  * [[it.unibo.pps.caw.common.model.Board]] with [[it.unibo.pps.caw.common.model.cell.Cell]]: the player is able to select and
  * deselect the playable area; they can add a cell in whichever position they want, move it from a position to another or remove
  * it. It must be constructed through its companion object.
  */
trait EditorModel {

  /** The [[EditorModelState]] of the editor. */
  val state: EditorModelState

  /** The [[it.unibo.pps.caw.common.model.Level]] built by the player. */
  val builtLevel: Option[Level[BaseCell]]

  /** Resets the level, removing the playable area and all the cells.
    *
    * @return
    *   a new instance of empty [[EditorModel]]
    */
  def resetLevel: EditorModel

  /** Adds the given cell to the model.
    *
    * @param cell
    *   the [[it.unibo.pps.caw.common.model.cell.BaseCell]] to be added
    * @return
    *   a new instance of [[EditorModel]] with the added cell
    */
  def addCell(cell: BaseCell): EditorModel

  /** Moves a cell given its current [[it.unibo.pps.caw.common.model.Position]] and the next
    * [[it.unibo.pps.caw.common.model.Position]] in which is moved.
    *
    * @param currentPosition
    *   the [[it.unibo.pps.caw.common.model.Position]] of the [[it.unibo.pps.caw.common.model.cell.Cell]] that is going to be
    *   moved
    * @param nextPosition
    *   the next [[it.unibo.pps.caw.common.model.Position]] of the [[it.unibo.pps.caw.common.model.cell.Cell]]
    * @return
    *   a new instance of [[EditorModel]] with the [[it.unibo.pps.caw.common.model.cell.Cell]] moved to its next
    *   [[it.unibo.pps.caw.common.model.Position]]
    */
  def updateCellPosition(currentPosition: Position, nextPosition: Position): EditorModel

  /** Removes the cell in the given [[it.unibo.pps.caw.common.model.Position]].
    *
    * @param position
    *   the [[it.unibo.pps.caw.common.model.Position]] of the [[it.unibo.pps.caw.common.model.cell.Cell]] that has to be removed
    * @return
    *   a new instance of [[EditorModel]] with the [[it.unibo.pps.caw.common.model.cell.Cell]] removed
    */
  def removeCell(position: Position): EditorModel

  /** Places a [[it.unibo.pps.caw.common.model.PlayableArea]] in the given [[it.unibo.pps.caw.common.model.Position]] with the
    * given [[it.unibo.pps.caw.common.model.Dimensions]].
    *
    * @param position
    *   the upper left corner [[it.unibo.pps.caw.common.model.Position]] of the [[it.unibo.pps.caw.common.model.PlayableArea]]
    * @param dimensions
    *   the [[it.unibo.pps.caw.common.model.Dimensions]] of the [[it.unibo.pps.caw.common.model.PlayableArea]]
    * @return
    *   a new instance of [[EditorModel]] containing a new [[it.unibo.pps.caw.common.model.PlayableArea]]
    */
  def addPlayableArea(position: Position, dimensions: Dimensions): EditorModel

  /** Removes the [[it.unibo.pps.caw.common.model.PlayableArea]] from the level.
    *
    * @return
    *   a new instance of [[EditorModel]] with the [[it.unibo.pps.caw.common.model.PlayableArea]] removed
    */
  def removePlayableArea: EditorModel
}

/** The companion object of the trait [[EditorModel]], containing its factory methods. */
object EditorModel {

  /* Implementation of EditorModel. */
  private case class LevelEditorModelImpl(state: EditorModelState) extends EditorModel {

    override val builtLevel: Option[Level[BaseCell]] =
      state
        .playableArea
        .map(a =>
          Level(
            Dimensions(state.dimensions.width - 2, state.dimensions.height - 2),
            state.board.filter(_.playable).map(_.changePositionProperty(p => (p.x - 1, p.y - 1))).map(_.toBaseCell),
            PlayableArea(a.dimensions)((a.position.x - 1, a.position.y - 1))
          )
        )

    override def resetLevel: EditorModel =
      //-2 because addCornerWalls adds 2 in each dimension
      EditorModel(state.dimensions.width - 2, state.dimensions.height - 2)

    override def updateCellPosition(currentPosition: Position, nextPosition: Position): EditorModel =
      (state.board.find(_.position == nextPosition), state.board.find(_.position == currentPosition)) match {
        case (None, Some(c)) =>
          LevelEditorModelImpl(
            state.copy(
              board = state.board.filter(_.position != currentPosition) + c.changePositionProperty(_ => nextPosition)
            )
          )
        case _ => this
      }

    override def addCell(cell: BaseCell): EditorModel =
      LevelEditorModelImpl(state.copy(board = state.board + cell.toPlayableCell(_ => true)))

    override def removeCell(position: Position): EditorModel =
      LevelEditorModelImpl(state.copy(board = state.board.filter(_.position != position)))

    override def addPlayableArea(position: Position, dimensions: Dimensions): EditorModel =
      LevelEditorModelImpl(state.copy(playableArea = Some(PlayableArea(dimensions)(position))))

    override def removePlayableArea: EditorModel = LevelEditorModelImpl(state.copy(playableArea = None))
  }

  private def addCornerWalls(levelBuilder: EditorModelState): EditorModelState = {
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
      .map(p => EditorModelState(PlayableArea(p.dimensions)((p.position.x + 1, p.position.y + 1))))
      .getOrElse(EditorModelState.apply: Dimensions => Board[PlayableCell] => EditorModelState)(
        (levelBuilder.dimensions.width + 2, levelBuilder.dimensions.height + 2)
      )(
        levelBuilder.board ++ walls
      )
  }

  /** Returns a new instance of [[EditorModel]] when editing an already existing level.
    *
    * @param level
    *   the existing level to be edited
    * @return
    *   a new instance of [[EditorModel]]
    */
  def apply(level: Level[BaseCell]): EditorModel =
    LevelEditorModelImpl(
      addCornerWalls(
        EditorModelState(
          level.playableArea
        )(
          level.dimensions
        )(
          level.board.map(_.toPlayableCell(_ => true)).map(_.changePositionProperty(p => (p.x + 1, p.y + 1)))
        )
      )
    )

  /** Returns a new instance of [[EditorModel]] when creating a new level.
    *
    * @param width
    *   the width of the level
    * @param height
    *   the height of the level
    * @return
    *   a new instance of [[EditorModel]]
    */
  def apply(width: Int, height: Int): EditorModel =
    LevelEditorModelImpl(addCornerWalls(EditorModelState((width, height))(Board.empty[PlayableCell])))
}
