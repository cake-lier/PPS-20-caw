package it.unibo.pps.caw.editor.controller

import it.unibo.pps.caw.common.model.cell.BaseCell
import it.unibo.pps.caw.common.model.{Dimensions, Level, Position}
import it.unibo.pps.caw.editor.model.EditorModel
import it.unibo.pps.caw.editor.view.EditorView

/** The parent controller to the [[EditorController]].
  *
  * This trait abstracts the functionalities that the [[EditorController]] needs from its parent controller. In this way, the
  * [[EditorController]] is more modular since it can be reused in multiple contexts with multiple parent controllers.
  */
trait ParentEditorController {

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
  * It acts as a bridge between the [[it.unibo.pps.caw.editor.view.EditorView]] and the
  * [[it.unibo.pps.caw.editor.model.EditorModel]]. It receives the player inputs from the
  * [[it.unibo.pps.caw.editor.view.EditorView]] and consequently updates the [[it.unibo.pps.caw.editor.model.EditorModel]]; it
  * then updates the [[it.unibo.pps.caw.editor.view.EditorView]] with the newly updated
  * [[it.unibo.pps.caw.editor.model.EditorModel]]. It must be constructed through its companion object.
  */
trait EditorController {

  /** Closes the editor. */
  def closeEditor(): Unit

  /** Resets the editor board, removing the playable area and all the cells present in the board. */
  def resetLevel(): Unit

  /** Adds a new cell to the [[it.unibo.pps.caw.editor.model.EditorModel]].
    *
    * @param cell
    *   the [[it.unibo.pps.caw.common.model.cell.BaseCell]] to be added
    */
  def addCell(cell: BaseCell): Unit

  /** Updates the [[it.unibo.pps.caw.editor.model.EditorModel]] by moving the cell whose
    * [[it.unibo.pps.caw.common.model.Position]] is equal to the given current position to the given next position.
    *
    * @param currentPosition
    *   the [[it.unibo.pps.caw.common.model.Position]] in which the cell is currently located
    * @param nextPosition
    *   the [[it.unibo.pps.caw.common.model.Position]] to which the cell is going to be moved
    */
  def updateCellPosition(currentPosition: Position, nextPosition: Position): Unit

  /** Removes the cell whose [[it.unibo.pps.caw.common.model.Position]] is equal to the given position from the
    * [[it.unibo.pps.caw.editor.model.EditorModel]].
    *
    * @param position
    *   the [[it.unibo.pps.caw.common.model.Position]] of the cell that has to be removed
    */
  def removeCell(position: Position): Unit

  /** Adds a [[it.unibo.pps.caw.common.model.PlayableArea]] to the [[it.unibo.pps.caw.editor.model.EditorModel]].
    *
    * @param position
    *   the upper left corner [[it.unibo.pps.caw.common.model.Position]] where the [[it.unibo.pps.caw.common.model .PlayableArea]]
    *   is located
    * @param dimensions
    *   the [[it.unibo.pps.caw.common.model.Dimensions]] of the [[it.unibo.pps.caw.common.model.PlayableArea]]
    */
  def addPlayableArea(position: Position, dimensions: Dimensions): Unit

  /** Removes the [[it.unibo.pps.caw.common.model.PlayableArea]] from the [[it.unibo.pps.caw.editor.model.EditorModel]] . */
  def removePlayableArea(): Unit

  /** Saves the current [[it.unibo.pps.caw.common.model.Level]] built by the user.
    * @param path
    *   the path of the file where the level will be saved
    */
  def saveLevel(path: String): Unit
}

/** The companion object of [[EditorController]], containing its factory methods. */
object EditorController {
  /* Abstract implementation of EditorController to factorize common behaviors. */
  abstract class AbstractEditorController(parentController: ParentEditorController, view: EditorView) extends EditorController {
    private var levelEditorModel: EditorModel = createEditorModel()

    view.drawState(levelEditorModel.state)

    protected def createEditorModel(): EditorModel

    override def resetLevel(): Unit = updateShowLevel(levelEditorModel.resetLevel)

    override def removeCell(position: Position): Unit = updateShowLevel(levelEditorModel.removeCell(position))

    override def addCell(cell: BaseCell): Unit = updateShowLevel(levelEditorModel.addCell(cell))

    override def removePlayableArea(): Unit = updateShowLevel(levelEditorModel.removePlayableArea)

    override def addPlayableArea(position: Position, dimensions: Dimensions): Unit =
      updateShowLevel(levelEditorModel.addPlayableArea(position, dimensions))

    override def saveLevel(path: String): Unit =
      levelEditorModel
        .builtLevel
        .fold(
          view.showError(message = "No playable area was set, could not save")
        )(
          parentController.saveLevel(path, _)
        )

    override def closeEditor(): Unit = parentController.closeEditor()

    override def updateCellPosition(currentPosition: Position, nextPosition: Position): Unit =
      updateShowLevel(levelEditorModel.updateCellPosition(currentPosition, nextPosition))

    private def updateShowLevel(newLevelEditorModel: EditorModel): Unit = {
      levelEditorModel = newLevelEditorModel
      view.drawState(levelEditorModel.state)
    }
  }

  /* Extension of the AbstractEditorController to create a new level from a empty board. */
  private class EmptyEditorController(
    parentController: ParentEditorController,
    editorView: EditorView,
    width: Int,
    height: Int
  ) extends AbstractEditorController(parentController: ParentEditorController, editorView: EditorView) {

    override def createEditorModel(): EditorModel = EditorModel(width, height)
  }

  /* Extension of the AbstractEditorController to create a new level from an existing level. */
  case class LevelEditorController(
    parentController: ParentEditorController,
    view: EditorView,
    level: Level[BaseCell]
  ) extends AbstractEditorController(parentController: ParentEditorController, view: EditorView) {

    override def createEditorModel(): EditorModel = EditorModel(level)
  }

  /** Returns a new instance of the [[EditorController]] trait. It must receive the [[ParentEditorController]], which it
    * represents its parent controller which provides all the functionalities which must be delegated to this type of controller,
    * the [[it.unibo.pps.caw.editor.view.EditorView]] which will be called by and will call the returned [[EditorController]]
    * instance and the width and height of the new level the player wants to create.
    *
    * @param parentController
    *   the parent controller of the returned [[EditorController]]
    * @param view
    *   the [[it.unibo.pps.caw.editor.view.EditorView]]
    * @param width
    *   the width of the level the player wants to create
    * @param height
    *   the height of the level the playaer wants to create
    * @return
    *   a new instance [[EditorController]]
    */
  def apply(
    parentController: ParentEditorController,
    view: EditorView,
    width: Int,
    height: Int
  ): EditorController =
    EmptyEditorController(parentController, view, width, height)

  /** Returns a new instance of the [[EditorController]] trait. It must receive the [[ParentEditorController]], which it
    * represents its parent controller which provides all the functionalities which must be delegated to this type of controller,
    * the [[it.unibo.pps.caw.editor.view.EditorView]] which will be called by and will call the returned [[EditorController]]
    * instance and the [[it.unibo.pps.caw.common.model.Level]] that the player wants to edit.
    *
    * @param parentController
    *   the parent controller of the returned [[EditorController]]
    * @param view
    *   the [[it.unibo.pps.caw.editor.view.EditorView]]
    * @param level
    *   the [[it.unibo.pps.caw.common.model.Level]] the player wants to edit
    * @return
    *   a new instance [[EditorController]]
    */
  def apply(
    parentController: ParentEditorController,
    view: EditorView,
    level: Level[BaseCell]
  ): EditorController =
    LevelEditorController(parentController, view, level)
}
