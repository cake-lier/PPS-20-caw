package it.unibo.pps.caw.editor.controller

import java.io.File

/** The parent controller to the [[EditorMenuController]].
  *
  * This trait abstracts the functionalities that the [[EditorMenuController]] needs from its parent controller. In this way, the
  * [[EditorMenuController]] is more modular because it can be reused in multiple contexts with multiple parent controllers.
  */
trait ParentLevelEditorMenuController {

  /** Asks the parent controller to exit the menu. */
  def goBack(): Unit

  /** Asks the parent controller to show the editor with an empty level of a given width and height.
    * @param width
    *   the width of the level
    * @param height
    *   the height of the level
    */
  def startEditor(width: Int, height: Int): Unit

  /** Asks the parent controller to show the editor with a level loaded from file.
    * @param path
    *   the path of the level
    */
  def startEditor(path: String): Unit
}

/** The controller that manages the editor menu.
  *
  * This controller provides the necessary functionalities of the editor menu. It must be constructed through its companion
  * object.
  */
sealed trait EditorMenuController {

  /** Shows the editor with an empty level of a given width and height.
    * @param width
    *   the width of the level
    * @param height
    *   the height of the level
    */
  def startLevelEditor(width: Int, height: Int): Unit

  /** Shows the editor with a level loaded from file.
    * @param path
    *   the path of the level
    */
  def startLevelEditor(path: String): Unit
}

/** The companion object of the trait [[EditorMenuController]], containing its factory method. */
object EditorMenuController {
  /* Implementation of EditorMenuController. */
  private case class LevelEditorMenuControllerImpl(parentLevelEditorMenuController: ParentLevelEditorMenuController)
    extends EditorMenuController {
    def startLevelEditor(width: Int, height: Int): Unit = parentLevelEditorMenuController.startEditor(width, height)
    def startLevelEditor(path: String): Unit = parentLevelEditorMenuController.startEditor(path)
  }

  /** Returns a new instance of [[EditorMenuController]]. It must receive the [[ParentEditorMenuController]], which represents its
    * parent controller that provides all the functionalities that must be delegated to this type of controller.
    *
    * @param parentLevelEditorMenuController
    *   the parent controller of [[EditorMenuController]]
    * @return
    *   a new instance of [[EditorMenuController]]
    */
  def apply(parentLevelEditorMenuController: ParentLevelEditorMenuController): EditorMenuController =
    LevelEditorMenuControllerImpl(parentLevelEditorMenuController)
}
