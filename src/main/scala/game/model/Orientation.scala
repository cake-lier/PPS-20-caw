package it.unibo.pps.caw.game.model

/** Enum that represent the orientation of some cells */
enum Orientation(val name: String) {
  case Right extends Orientation("right")
  case Left extends Orientation("left")
  case Down extends Orientation("down")
  case Top extends Orientation("top")
}

object Orientation {

  /** get the [[Option]] of given orientation to [[Orientation]]
    * @param name
    *   the value of [[Orientation]] as string
    */
  def fromName(name: String): Option[Orientation] = Orientation.values.find(_.name == name)
}
