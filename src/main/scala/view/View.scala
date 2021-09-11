package it.unibo.pps.caw.view

import it.unibo.pps.caw.model.Level

/* mock view */
trait View {

  def drawLevel(level: Level): Unit

}

object View {
  private var currLevel: Int = _

  private final class ViewImpl extends View{

    def drawLevel(level: Level): Unit = println(level.getString())

  }

  def apply(): View = ViewImpl()
}

