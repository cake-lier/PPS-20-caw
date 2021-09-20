package it.unibo.pps.caw

import javafx.scene.Scene
import scalafx.stage.FileChooser

import java.io.File

/** Wraps another view component.
  *
  * This trait creates modular view components that can be re-instantied multiple times without specifying how to initialize them
  * since it incapsulates all the logic needed to create and style view components. The instantiation is made through its
  * companion object.
  *
  * @tparam A
  *   the type of component wrapped
  */
trait ViewComponent[A] {

  /** Returns the inner JavaFX component wrapped by this [[ViewComponent]]. */
  val innerComponent: A
}

/** Factory for new [[ViewComponent]] instances. */
object ViewComponent {
  import javafx.fxml.FXMLLoader

  /** A wrapper for JavaFX components instantied throught FXML files.
    *
    * The FXMLLoader is set but does not actually load the file, so as to allow subclasses to bind their fields to the created
    * objects. For this reason, the FXMLLoader is assigned to a protected property.
    * @constructor
    *   create a new instance of [[ViewComponent]] by specifying the name of the FXML file.
    * @param fxmlFileName
    *   the name of the FXML file from which instantiating the [[ViewComponent]]
    * @tparam A
    *   the type of JavaFX component to be wrapped
    */
  abstract class AbstractViewComponent[A](fxmlFileName: String) extends ViewComponent[A] {
    protected val loader = FXMLLoader()
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("fxml/" + fxmlFileName))

    /** Converts a component into its wrapped object through [[ViewComponent.innerComponent]] property
      * @param component
      *   the [[ViewComponent]] to convert
      * @tparam A
      *   the type of the wrapped component
      * @return
      *   the wrapped component
      */
    given Conversion[ViewComponent[A], A] with
      def apply(component: ViewComponent[A]) = component.innerComponent
  }
}

object FilePicker {
  def pickFile(scene: Scene): Option[File] = {
    val chooser: FileChooser = FileChooser()
    chooser.title = "Choose a level file"
    chooser.extensionFilters.add(FileChooser.ExtensionFilter("Level file", "*.json"))
    Option(chooser.showOpenDialog(scene.getWindow))
  }
  def saveFile(scene: Scene): Option[File] = {
    val chooser: FileChooser = FileChooser()
    Option(chooser.showSaveDialog(scene.getWindow))
  }
}