package it.unibo.pps.caw
package view

import it.unibo.pps.caw.view.Memoize.memoize

object Memoize {
  def memoize[I, O](f: I => O): I => O = new collection.mutable.HashMap[I, O]() {
    override def apply(key: I) = get(key) match {
      case Some(output) => if (output == (f(key))) return output else f(key)
      case None         => f(key)
    }
  }
}

object ViewTestHelper {
  import javafx.scene.control.Button
  import javafx.scene.image.{Image, ImageView}
  import org.testfx.api.FxRobot

  private val robot = new FxRobot()
  val getButtonById: String => Button = memoize(id => {
    robot
      .lookup((b: Button) => b.getId.equals(id))
      .queryButton()
  })

  val getImageViewByImage: Image => ImageView = memoize(image => {
    robot
      .lookup((i: ImageView) => i.getImage.equals(image))
      .query[ImageView]()
  })
}
