package it.unibo.pps.caw
package view

trait Component[A] {
  def innerComponent: A
}

object ViewComponent {
  import javafx.fxml.FXMLLoader
  import scala.language.implicitConversions

  abstract class AbstractViewComponent[A](fxmlFileName: String)
      extends Component[A] {
    protected val loader = FXMLLoader()
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("fxml/" + fxmlFileName))

    given Conversion[Component[A], A] with
      def apply(component: Component[A]) = component.innerComponent
  }
}
