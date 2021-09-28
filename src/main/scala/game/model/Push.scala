package it.unibo.pps.caw.game.model

/** Enum that represent the direction to which some cells can be pushed */
enum Push(val name: String) {
  case Horizontal extends Push("horizontal")
  case Vertical extends Push("vertical")
  case Both extends Push("both")
}

object Push {

  /** get the [[Option]] of given push to [[Push]]
    *
    * @param name
    *   the value of [[Push]] as string
    */
  def fromName(name: String): Option[Push] = Push.values.find(_.name == name)
}
