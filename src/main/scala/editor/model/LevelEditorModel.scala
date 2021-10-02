package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.*

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

  /* Converts the given BaseCell to a PlayableCell. */
  private def createPlayableFromBase(cell: BaseCell): PlayableCell = cell match {
    case BaseRotatorCell(p, r)   => PlayableRotatorCell(p, r, true)
    case BaseGeneratorCell(p, o) => PlayableGeneratorCell(p, o, true)
    case BaseEnemyCell(p)        => PlayableEnemyCell(p, true)
    case BaseMoverCell(p, o)     => PlayableMoverCell(p, o, true)
    case BaseBlockCell(p, d)     => PlayableBlockCell(p, d, true)
    case BaseWallCell(p)         => PlayableWallCell(p, true)
  }

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
              .cells
              .map {
                case PlayableRotatorCell(p, r, _) => BaseRotatorCell(p, r)
                case PlayableGeneratorCell(p, o, _) => BaseGeneratorCell(p, o)
                case PlayableEnemyCell(p, _) => BaseEnemyCell(p)
                case PlayableMoverCell(p, o, _) => BaseMoverCell(p, o)
                case PlayableBlockCell(p, d, _) => BaseBlockCell(p, d)
                case PlayableWallCell(p, _) => BaseWallCell(p)
              },
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
          case PlayableWallCell(_, playable) => PlayableWallCell(newPosition, playable)
          case PlayableEnemyCell(_, playable) => PlayableEnemyCell(newPosition, playable)
          case PlayableRotatorCell(_, rotationDirection, playable) =>
            PlayableRotatorCell(newPosition, rotationDirection, playable)
          case PlayableGeneratorCell(_, orientation, playable) => PlayableGeneratorCell(newPosition, orientation, playable)
          case PlayableMoverCell(_, orientation, playable) => PlayableMoverCell(newPosition, orientation, playable)
          case PlayableBlockCell(_, push, playable) => PlayableBlockCell(newPosition, push, playable)
        }
        .get
      LevelEditorModelImpl(currentLevel.copy(board = currentLevel.board.cells.filter(_.position != oldPosition) + updatedCell))
    }

    override def setCell(cell: BaseCell): LevelEditorModel =
      LevelEditorModelImpl(currentLevel.copy(board = currentLevel.board.cells + createPlayableFromBase(cell)))

    override def unsetCell(position: Position): LevelEditorModel =
      LevelEditorModelImpl(currentLevel.copy(board = currentLevel.board.cells.filter(_.position != position)))

    override def setPlayableArea(position: Position, dimensions: Dimensions): LevelEditorModel =
      LevelEditorModelImpl(currentLevel.copy(playableArea = Some(PlayableArea(position, dimensions))))

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
        Board(level.board.cells.map(createPlayableFromBase)),
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
