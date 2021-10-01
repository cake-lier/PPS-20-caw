package it.unibo.pps.caw.common.model.cell

import it.unibo.pps.caw.common.model.Position

/** A cell, an element of a [[Board]] in the game.
  *
  * The [[Cell]] represents the atomic entity of the game, its smallest component. A cell is alive and, being so, it has a
  * behavior that keeps to express until it dies and is removed from the game. There are different types of cells, each with its
  * peculiar behavior. The cells are what the user manipulates in the game for winning a [[Level]] and advance to the next until
  * all levels are completed. A cell necessarily has a [[Position]] in a [[Board]], because boards is what contains them, and no
  * two distinct cells can share the same position.
  */
trait Cell extends Ordered[Cell] {

  /** Returns the [[Position]] of this [[Cell]] into the [[Board]] in which is inserted. */
  val position: Position

  final override def compare(that: Cell): Int = (position.x - that.position.x) + (position.y - that.position.y)

  final override def equals(obj: Any): Boolean = obj match {
    case c: Cell => position == c.position
    case _       => false
  }

  final override def hashCode(): Int = position.hashCode()
}

/** The rotator [[Cell]], the cell that can rotate other cells.
  *
  * This cell can rotate other cells which have a specific [[Orientation]] or [[Push]] so as to make them assume another value
  * orientation or push direction, depending if the [[Rotation]] is clockwise or counterclockwise. For example, under a clockwise
  * rotation, a right orientation will become a bottom orientation, an horizontal push a vertical one and so on and so forth. A
  * [[BlockCell]] with a push in both direction will not be affected. Under a counterclockwise rotation the vice versa is valid.
  */
trait RotatorCell extends Cell {

  /** Returns the direction of [[Rotation]] of this [[RotatorCell]]. */
  val rotation: Rotation
}

/** The generator [[Cell]], the cell that can generate other cells.
  *
  * This cell can generate each other cell in the game, with the exception of the [[EnemyCell]]. The cell that can be generated is
  * the one with a [[Position]] adjacent to this [[GeneratorCell]], but at the opposite end of its [[Orientation]]. In simpler
  * terms, the cell that can be generated is the one "behind" this generator. If no cell is present, no generation occurs. If the
  * generation occurs, the cell is placed "in front" of the generator, so adjacent to it but in the same direction of its
  * orientation. The generation exerts a force on the other cells in front of the generator, pushing them one position in the
  * orientation of the cell. If there is no place to move, no generation occurs. Another case in which generation does not occur
  * is when a cell exerting an opposite force is in front of the generator and, directly or indirectly, adjacent to it. These
  * cells can be other [[GeneratorCell]] or [[MoverCell]]. This is also true for unmovable cells, like [[WallCell]] and
  * [[BlockCell]] with a [[Push]] direction different from the orientation of this cell.
  */
trait GeneratorCell extends Cell {

  /** Returns the [[Orientation]] of this [[GeneratorCell]], the direction in which new cells will be generated. */
  val orientation: Orientation
}

/** An enemy [[Cell]], a cell to be killed by the player.
  *
  * This cell is what gives a goal to the game. Indeed, the player, for a [[Level]] to be considered fully completed, needs to
  * kill all [[EnemyCell]] that are present inside it using the correct arrangement of the other cells in the level, exploiting
  * their behavior in their favor. An enemy cell, to be destroyed, needs that something is pushed against it or that the cell
  * itself is pushed against another cell. This will result also in the killing of the cell that was pushed against the enemy
  * cell.
  */
trait EnemyCell extends Cell

/** A mover [[Cell]], a cell that can move on its own.
  *
  * This cell can move in the [[Board]] by one [[Position]] according to its [[Orientation]]. This means that, if this cell has a
  * right orientation, it can move one position to the right. The movement made by the cell can exert a force on the neighboring
  * cells, more specifically the ones adjacent to the mover cell in the direction which is the same of the cell [[Orientation]].
  * In simpler words, on the cells "in front" of the mover cell. If the cells in front can not be moved, the movement is not made
  * and the cells not pushed. A mover cell can not directly move a [[GeneratorCell]] from behind (and in general from the front)
  * or a [[RotatorCell]] or even another [[MoverCell]] with the opposite orientation. Other cells that cannot be moved are the
  * unmovable ones: the [[WallCell]] or a [[BlockCell]] with a [[Push]] direction different from this cell orientation.
  */
trait MoverCell extends Cell {

  /** Returns the [[Orientation]] of this [[MoverCell]], the one in which the cell is going to move if allowed to. */
  val orientation: Orientation
}

/** A block [[Cell]], a simple cell that can be pushed around.
  *
  * The only behavior that is exhibited by this cell is to be moved when pushed. This means that it can be moved by a
  * [[MoverCell]] or a [[GeneratorCell]], either directly or indirectly, when in contact with them. This is true only if the
  * direction of the [[Push]] exerted on the block cell is coherent with the [[Orientation]] of the pushing cell. In any other
  * case the movement will be denied. A [[RotatorCell]] can change the push direction of this cell.
  */
trait BlockCell extends Cell {

  /** Returns the direction for which a [[Push]] can be made on this [[Cell]]. */
  val push: Push
}

/** A wall [[Cell]], an unmovable cell.
  *
  * This cell is, by definition, unmovable. No [[MoverCell]] or [[GeneratorCell]], either directly or indirectly, can push this
  * cell away from its position. It can be moved by the player during the setup of the [[Level]], but not in any other moment.
  */
trait WallCell extends Cell
