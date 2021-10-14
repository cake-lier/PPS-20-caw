package it.unibo.pps.caw.editor.controller

import it.unibo.pps.caw.common.model.cell.BaseCell
import it.unibo.pps.caw.common.model.{Dimensions, Level, Position}
import it.unibo.pps.caw.editor.model.LevelEditorModel
import it.unibo.pps.caw.editor.view.EditorView

/** The parent controller to the [[EditorController]].
  *
  * This trait abstracts the functionalities that the [[EditorController]] needs from its parent controller. In this way, the
  * [[EditorController]] is more modular since it can be reused in multiple contexts with multiple parent controllers.
  */
trait ParentLevelEditorController {

  /** Asks the parent controller to close the editor. */
  def closeEditor(): Unit

  /** Asks the parent controller to save the edited level.
    * @param path
    *   the path of the file where the level will be saved
    * @param level
    *   the level to be saved
    */
  def saveLevel(path: String, level: Level[BaseCell]): Unit
}

/** The controller that manages the editor.
  *
  * It acts as a bridge between the [[EditorView]] and the [[LevelEditorModel]]. It receives the player inputs from the
  * [[EditorView]] and consequently updates the [[LevelEditorModel]]; it then updates the [[EditorView]] with the newly updated
  * [[LevelEditorModel]]. It must be constructed through its companion object.
  */
sealed trait EditorController {

  /** Closes the editor. */
  def closeEditor(): Unit

  /** Resets the editor board, removing the playable area and all the cells present in the board. */
  def resetLevel(): Unit

  /** Adds a new cell to the [[LevelEditorModel]].
    * @param cell
    *   the [[BaseCell]] to be added
    */
  def setCell(cell: BaseCell): Unit

  /** Updates the [[LevelEditorModel]] by moving the cell whose [[Position]] is equal to the given old [[Position]] to the new
    * given [[Position]].
    * @param oldPosition
    *   the [[Position]] in which the cell was originally located
    * @param newPosition
    *   the [[Position]] to which the cell was moved
    */
  def updateCellPosition(oldPosition: Position, newPosition: Position): Unit

  /** Removes the cell whose [[Position]] is equal to the given [[Position]] from the [[LevelEditorModel]].
    * @param position
    *   the [[Position]] of the cell that has to be removed
    */
  def removeCell(position: Position): Unit

  /** Adds a [[it.unibo.pps.caw.common.model.PlayableArea]] to the [[LevelEditorModel]].
    * @param position
    *   the upper left [[Position]] where the [[it.unibo.pps.caw.common.model.PlayableArea]] is located
    * @param dimensions
    *   the [[Dimensions]] of the [[it.unibo.pps.caw.common.model.PlayableArea]]
    */
  def setPlayableArea(position: Position, dimensions: Dimensions): Unit

  /** Removes the [[it.unibo.pps.caw.common.model.PlayableArea]] from the [[LevelEditorModel]] . */
  def removePlayableArea(): Unit

  /** Saves the current [[Level]] built by the user.
    * @param path
    *   the path of the file where the level will be saved
    */
  def saveLevel(path: String): Unit
}

/** The companion object of [[EditorController]], containing its factory methods. */
object EditorController {
  /* Abstract implementation of EditorController to factorize common behaviors. */
  abstract class AbstractEditorController(parentLevelEditorController: ParentLevelEditorController, view: EditorView)
    extends EditorController {
    private var levelEditorModel: LevelEditorModel = createEditorModel()

    view.drawLevelState(levelEditorModel.currentState)

    protected def createEditorModel(): LevelEditorModel

    override def resetLevel(): Unit = updateShowLevel(levelEditorModel.resetLevel)

    override def removeCell(position: Position): Unit = updateShowLevel(levelEditorModel.unsetCell(position))

    override def setCell(cell: BaseCell): Unit = updateShowLevel(levelEditorModel.setCell(cell))

    override def removePlayableArea(): Unit = updateShowLevel(levelEditorModel.unsetPlayableArea)

    override def setPlayableArea(position: Position, dimensions: Dimensions): Unit =
      updateShowLevel(levelEditorModel.setPlayableArea(position, dimensions))

    override def saveLevel(path: String): Unit =
      levelEditorModel
        .builtLevel
        .fold(
          view.showError(message = "No playable area was set, could not save")
        )(
          parentLevelEditorController.saveLevel(path, _)
        )

    override def closeEditor(): Unit = parentLevelEditorController.closeEditor()

    override def updateCellPosition(oldPosition: Position, newPosition: Position): Unit =
      updateShowLevel(levelEditorModel.updateCellPosition(oldPosition, newPosition))

    private def updateShowLevel(newLevelEditorModel: LevelEditorModel): Unit = {
      levelEditorModel = newLevelEditorModel
      view.drawLevelState(levelEditorModel.currentState)
    }
  }

  /* Extension of the AbstractEditorController to create a new level from a empty board. */
  private class EmptyEditorController(
    parentLevelEditorController: ParentLevelEditorController,
    levelEditorView: EditorView,
    width: Int,
    height: Int
  ) extends AbstractEditorController(parentLevelEditorController: ParentLevelEditorController, levelEditorView: EditorView) {

    override def createEditorModel(): LevelEditorModel = LevelEditorModel(width, height)
  }

  /* Extension of the AbstractEditorController to create a new level from an existing level. */
  case class LevelEditorController(
    parentLevelEditorController: ParentLevelEditorController,
    levelEditorView: EditorView,
    level: Level[BaseCell]
  ) extends AbstractEditorController(parentLevelEditorController: ParentLevelEditorController, levelEditorView: EditorView) {

    override def createEditorModel(): LevelEditorModel = LevelEditorModel(level)
  }

  /** Returns a new instance of the [[EditorController]] trait. It must receive the [[ParentLevelEditorController]], which it
    * represents its parent controller which provides all the functionalities which must be delegated to this type of controller,
    * the [[EditorView]] which will be called by and will call the returned [[EditorController]] instance and the width and height
    * of the new level the player wants to create.
    *
    * @param parentLevelEditorController
    *   the parent controller of the returned [[EditorController]]
    * @param levelEditorView
    *   the [[EditorView]]
    * @param width
    *   the width of the level the player wants to create
    * @param height
    *   the height of the level the playaer wants to create
    * @return
    *   a new instance [[EditorController]]
    */
  def apply(
    parentLevelEditorController: ParentLevelEditorController,
    levelEditorView: EditorView,
    width: Int,
    height: Int
  ): EditorController =
    EmptyEditorController(parentLevelEditorController, levelEditorView, width, height)

  /** Returns a new instance of the [[EditorController]] trait. It must receive the [[ParentLevelEditorController]], which it
    * represents its parent controller which provides all the functionalities which must be delegated to this type of controller,
    * the [[EditorView]] which will be called by and will call the returned [[EditorController]] instance and the [[Level]] that
    * the player wants to edit.
    *
    * @param parentLevelEditorController
    *   the parent controller of the returned [[EditorController]]
    * @param levelEditorView
    *   the [[EditorView]]
    * @param level
    *   the [[Level]] the player wants to edit
    * @return
    *   a new instance [[EditorController]]
    */
  def apply(
    parentLevelEditorController: ParentLevelEditorController,
    levelEditorView: EditorView,
    level: Level[BaseCell]
  ): EditorController =
    LevelEditorController(parentLevelEditorController, levelEditorView, level)
}
