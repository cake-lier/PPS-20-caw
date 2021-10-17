package it.unibo.pps.caw.menu.controller

/** The controller that manages the editor menu.
  *
  * This controller provides the necessary functionalities of the editor menu. It must be constructed through its companion
  * object.
  */
trait EditorMenuController {

  /** Shows the editor with an empty level of a given width and height.
    * @param width
    *   the width of the level
    * @param height
    *   the height of the level
    */
  def openEditor(width: Int, height: Int): Unit

  /** Shows the editor with a level loaded from file.
    * @param levelPath
    *   the path of the level
    */
  def openEditor(levelPath: String): Unit

  /** Returns to the main menu. */
  def goBack(): Unit
}
