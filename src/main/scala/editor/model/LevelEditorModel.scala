package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.common.model.{Board, Dimensions, PlayableArea, Position}
import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.game.model.GameModelHelpers

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
      currentLevel.playableArea
        .map(a =>
          Level(
            Dimensions(currentLevel.width - 2, currentLevel.height - 2),
            currentLevel.board.cells
              .filter(_.playable)
              .map(tranformPosition(_)(p => (p.x - 1, p.y - 1)))
              .map(_ match {
                case PlayableRotatorCell(p, r, _)   => BaseRotatorCell(p, r)
                case PlayableGeneratorCell(p, o, _) => BaseGeneratorCell(p, o)
                case PlayableEnemyCell(p, _)        => BaseEnemyCell(p)
                case PlayableMoverCell(p, o, _)     => BaseMoverCell(p, o)
                case PlayableBlockCell(p, d, _)     => BaseBlockCell(p, d)
                case PlayableWallCell(p, _)         => BaseWallCell(p)
              }),
            PlayableArea((a.position.x - 1, a.position.y - 1), a.dimensions)
          )
        )
    }

    override def resetLevel: LevelEditorModel = LevelEditorModel(currentLevel.width, currentLevel.height)

    override def updateCellPosition(oldPosition: Position, newPosition: Position): LevelEditorModel = {
      val updatedCell: PlayableCell = currentLevel.board.cells
        .find(_.position == oldPosition)
        .map {
          case PlayableWallCell(_, playable)  => PlayableWallCell(newPosition, playable)
          case PlayableEnemyCell(_, playable) => PlayableEnemyCell(newPosition, playable)
          case PlayableRotatorCell(_, rotationDirection, playable) =>
            PlayableRotatorCell(newPosition, rotationDirection, playable)
          case PlayableGeneratorCell(_, orientation, playable) => PlayableGeneratorCell(newPosition, orientation, playable)
          case PlayableMoverCell(_, orientation, playable)     => PlayableMoverCell(newPosition, orientation, playable)
          case PlayableBlockCell(_, push, playable)            => PlayableBlockCell(newPosition, push, playable)
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

  private def apply(levelBuilder: LevelBuilder): LevelEditorModel = {
    val walls: Set[PlayableCell] =
      Set(
        (0 to levelBuilder.width + 1).map(i => PlayableWallCell((i, 0), false)),
        (0 to levelBuilder.width + 1)
          .map(i => PlayableWallCell((i, levelBuilder.height + 1), false)),
        (1 to levelBuilder.height).map(i => PlayableWallCell((0, i), false)),
        (1 to levelBuilder.height)
          .map(i => PlayableWallCell((levelBuilder.width + 1, i), false))
      ).flatten

    LevelEditorModelImpl(
      levelBuilder.playableArea
        .map(p =>
          LevelBuilder(
            levelBuilder.width + 2,
            levelBuilder.height + 2,
            Board(levelBuilder.board ++ walls),
            PlayableArea((p.position.x + 1, p.position.y + 1), p.dimensions)
          )
        )
        .getOrElse(LevelBuilder(levelBuilder.width + 2, levelBuilder.height + 2, Board(levelBuilder.board ++ walls)))
    )
  }

  private def createPlayableFromBase(cell: BaseCell): PlayableCell = cell match {
    case BaseRotatorCell(p, r)   => PlayableRotatorCell(p, r, true)
    case BaseGeneratorCell(p, o) => PlayableGeneratorCell(p, o, true)
    case BaseEnemyCell(p)        => PlayableEnemyCell(p, true)
    case BaseMoverCell(p, o)     => PlayableMoverCell(p, o, true)
    case BaseBlockCell(p, d)     => PlayableBlockCell(p, d, true)
    case BaseWallCell(p)         => PlayableWallCell(p, true)
  }

  private def tranformPosition(cell: PlayableCell)(f: Position => Position): PlayableCell = cell match {
    case PlayableRotatorCell(p, r, m)   => PlayableRotatorCell(f(p), r, m)
    case PlayableGeneratorCell(p, o, m) => PlayableGeneratorCell(f(p), o, m)
    case PlayableEnemyCell(p, m)        => PlayableEnemyCell(f(p), m)
    case PlayableMoverCell(p, o, m)     => PlayableMoverCell(f(p), o, m)
    case PlayableBlockCell(p, d, m)     => PlayableBlockCell(f(p), d, m)
    case PlayableWallCell(p, m)         => PlayableWallCell(f(p), m)
  }

  /** Returns a new instance of [[LevelEditorModel]] when editing an already existing level.
    * @param level
    *   the existing level to be edited
    * @return
    *   a new instance of [[LevelEditorModel]]
    */
  def apply(level: Level[BaseCell]): LevelEditorModel = LevelEditorModel(
    LevelBuilder(
      level.dimensions.width,
      level.dimensions.height,
      Board(level.board.cells.map(createPlayableFromBase).map(tranformPosition(_)(p => (p.x + 1, p.y + 1)))),
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
