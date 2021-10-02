package it.unibo.pps.caw.game.model.engine

import it.unibo.pps.caw.common.model.{Board, Position}
import it.unibo.pps.caw.common.model.cell.{Orientation, Push, Rotation}
import it.unibo.pps.caw.game.model._

import alice.tuprolog.Term

import scala.util.matching.Regex

/* An utility object for prolog serialization and deserialization */
private object PrologParser {

  /* Returns a Prolog cell(id, cellType, x, y) given its Scala cell */
  def serializeCell(cell: UpdateCell): String = {
    val cellType: String = cell match {
      case _: UpdateWallCell  => "wall"
      case _: UpdateEnemyCell => "enemy"
      case m: UpdateMoverCell => "mover_" + m.orientation.name
      case b: UpdateBlockCell =>
        "block" + (b.push match {
          case Push.Horizontal => "_hor"
          case Push.Vertical   => "_ver"
          case Push.Both       => ""
        })
      case g: UpdateGeneratorCell => "generator_" + g.orientation.name
      case r: UpdateRotatorCell   => "rotator_" + r.rotation.name
    }
    "cell" + Seq(cell.id, cellType, cell.position.x, cell.position.y).mkString("(", ",", ")")
  }

  /* Returns a Prolog term given its cell.

     If the cell is a mover or a rotator, it returns mover/ratator_next_state[board, x, y, NB].
     If the cell is a generator, it returns generator_next_state[board, maxId, x, y, NB] */
  def createSerializedPredicate(board: Board[UpdateCell], maxId: Long, cell: UpdateCell): String = {
    var seq = Seq("[" + board.cells.map(serializeCell).mkString(",") + "]", cell.position.x, cell.position.y)
    val action: String = cell match {
      case m: UpdateMoverCell => "mover_" + m.orientation.name
      case g: UpdateGeneratorCell =>
        seq = seq :+ maxId.toString
        "generator_" + g.orientation.name
      case r: UpdateRotatorCell => "rotator_" + r.rotation.name
    }

    seq = seq :+ "NB"

    action
      + "_next_state"
      + seq.mkString("(", ",", ")")

  }

  /* Returns a Scala Board of fake cells given the Prolog Board */
  def deserializeBoard(stringBoard: String): Board[UpdateCell] = {
    val regex: Regex =
      "cell\\(\\d+,(?:mover_right|mover_left|mover_top|mover_down|generator_right|generator_left|generator_top|generator_down|rotator_clockwise|rotator_counterclockwise|block|block_hor|block_ver|enemy|wall),\\d+,\\d+\\)".r
    Board(
      regex
        .findAllMatchIn(stringBoard)
        .map(_.toString)
        .map(PrologParser.deserializeCell)
        .toSet
    )
  }

  /* Returns a Scala fake cell given its Prolog cell*/
  def deserializeCell(stringCell: String): UpdateCell = {
    val s"cell($id,$cellType,$stringX,$stringY)" = stringCell
    val cellId = id.toInt
    val position = Position(stringX.toInt, stringY.toInt)
    val updated = false // default value, properly set in nextState()

    cellType match {
      case s"mover_$orientation" => UpdateMoverCell(position, Orientation.fromName(orientation).get, cellId, updated)
      case "enemy"               => UpdateEnemyCell(position, cellId, updated)
      case "wall"                => UpdateWallCell(position, cellId, updated)
      case s"block$movement" =>
        UpdateBlockCell(
          position,
          movement match {
            case "_hor" => Push.Horizontal
            case "_ver" => Push.Vertical
            case _      => Push.Both
          },
          cellId,
          updated
        )
      case s"generator_$orientation" =>
        UpdateGeneratorCell(position, Orientation.fromName(orientation).get, cellId, updated)
      case s"rotator_$rotation" => UpdateRotatorCell(position, Rotation.fromName(rotation).get, cellId, updated)
    }
  }
}
